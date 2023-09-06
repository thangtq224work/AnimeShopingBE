package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.*;
import com.application.dto.request.OrderReq;
import com.application.dto.request.ProductInCartReq;
import com.application.dto.request.ProductReq;
import com.application.dto.response.ProductResp;
import com.application.entity.*;
import com.application.exception.NotFoundException;
import com.application.repository.*;
import com.application.service.*;
import com.application.utils.UploadUtil;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    TypeProductRepo typeProductRepo;
    @Autowired
    MaterialRepo materialRepo;
    @Autowired
    SupplierRepo supplierRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    ProductImageRepo productImageRepo;
    @Autowired
    UploadUtil uploadUtil;
    @Autowired
    DiscountRepo discountRepo;
    @Autowired
    ProductDiscountRepo productDiscountRepo;
    @Override
    public PageData<ProductResp> getAll(Pageable pageable,String search,Integer status) {
        Boolean tmp = status == null?null:(status==1?true:false);
        Specification<Product> specification = (root, query, criteriaBuilder) ->{
            Predicate statusPredicate = tmp == null?criteriaBuilder.and():criteriaBuilder.equal(root.get("status"),tmp);
            Predicate namePredicate = buildLikeExp(search)==null?criteriaBuilder.and():criteriaBuilder.like(root.get("name"),buildLikeExp(search));
            return  criteriaBuilder.and(statusPredicate,namePredicate);
        } ;
        Page<Product> page = productRepo.findAll(specification,pageable);
        log.info("get product");
        return PageData.of(page, page.toList().stream().map(i -> new ProductResp(i)).collect(Collectors.toList()));
    }

    @Override
    public List<ProductResp> getAll() {
        return null;
    }

    @Override
    public ProductResp getById(Integer id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        return new ProductResp(product);
    }
    public List<Product> getById(Integer [] ids){
        List<Product> products = productRepo.findAllById(Arrays.stream(ids).toList());
        return null;
    }

    @Override
    public PageData<ProductResp> getProduct(Integer page, Integer size, List<Integer> categories, List<Integer> materials, List<Integer> typeProduct, String sortBy, String direction) {
        Sort sort = Sort.by(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, validSort(sortBy) ? sortBy : "createAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            Predicate categoryPredicate = (categories == null || categories.isEmpty()) ? criteriaBuilder.and() : root.get("category").in(categories);
            Predicate categoryStatusPredicate = criteriaBuilder.isTrue(root.join("category",JoinType.INNER).get("status"));
            Predicate typePredicate = (typeProduct == null || typeProduct.isEmpty()) ? criteriaBuilder.and() : root.get("typeProduct").in(typeProduct);
            Predicate typeStatusPredicate = criteriaBuilder.isTrue(root.join("typeProduct",JoinType.INNER).get("status"));
            Predicate materialsPredicate = (materials == null || materials.isEmpty()) ? criteriaBuilder.and() : root.get("material").in(materials);
            Predicate materialsStatusPredicate = criteriaBuilder.isTrue(root.join("material",JoinType.INNER).get("status"));
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), Constant.Status.ACTIVE);
            return criteriaBuilder.and(
                    categoryPredicate, typePredicate, materialsPredicate, statusPredicate, categoryStatusPredicate, typeStatusPredicate, materialsStatusPredicate
            );
        };
        Page<Product> products = productRepo.findAll(specification, pageable);
        List<ProductResp> productResps = products.toList().stream().map(i -> new ProductResp(i,true)).collect(Collectors.toList());
        Pageable discountPageable = PageRequest.of(0,1,Sort.by(Sort.Direction.DESC,"discountStart"));
        Page<Discount> discounts = discountRepo.getDiscountActive(discountPageable,true,new Date());
        if(discounts.getTotalElements() >0){
            Discount discount = discounts.toList().get(0);
            for (ProductResp pi:productResps) {
                pi.setPrice(pi.getPriceSell());
                    if(productDiscountRepo.getByDiscountIdAndProductId(discount.getId(),pi.getId())>0L ){
                        if(discount.getDiscountType()== Constant.TypeDiscount.PERCENT){
                            // xu ly cong tong hay cong don
                            BigDecimal priceSale = pi.getPriceSell().multiply(discount.getDiscountAmount().divide(BigDecimal.valueOf(100)));
                            pi.setPriceSell(pi.getPriceSell().subtract(priceSale));
                        }else{
                            pi.setPriceSell(pi.getPriceSell().subtract(discount.getDiscountAmount()));
                        }
                        if(pi.getPriceSell().compareTo(BigDecimal.ZERO) < 0){
                            pi.setPriceSell(BigDecimal.ZERO);
                        }
                    }
            }
        }
        return PageData.of(products, productResps);
    }

    @Override
    public ProductResp insert(ProductReq dto) {
        log.info("dto : "+dto.toString());
        Category category = categoryRepo.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Category not found"));
        Material material = materialRepo.findById(dto.getMaterial()).orElseThrow(() -> new NotFoundException("Material not found"));
        Supplier supplier = supplierRepo.findById(dto.getSupplier()).orElseThrow(() -> new NotFoundException("Supplier not found"));
        TypeProduct typeProduct = typeProductRepo.findById(dto.getTypeProduct()).orElseThrow(() -> new NotFoundException("TypeProduct not found"));
        Product product = new Product(dto);
        product.setCategory(category);
        product.setMaterial(material);
        product.setSupplier(supplier);
        product.setTypeProduct(typeProduct);
        product = productRepo.save(product);
        return new ProductResp(product);
    }

    @Override
    public ProductResp update(ProductReq dto) throws IOException {
        Product product = productRepo.findById(dto.getId()).orElseThrow(() -> new NotFoundException("Product not found"));
        Category category = categoryRepo.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Category not found"));
        Material material = materialRepo.findById(dto.getMaterial()).orElseThrow(() -> new NotFoundException("Material not found"));
        Supplier supplier = supplierRepo.findById(dto.getSupplier()).orElseThrow(() -> new NotFoundException("Supplier not found"));
        TypeProduct typeProduct = typeProductRepo.findById(dto.getTypeProduct()).orElseThrow(() -> new NotFoundException("TypeProduct not found"));
        product.setCategory(category);
        product.setMaterial(material);
        product.setSupplier(supplier);
        product.setTypeProduct(typeProduct);
        product.convert(dto);
        product = productRepo.save(product);
//        weakreference https://viblo.asia/p/how-references-work-in-java-and-android-PwRGgmookEd
        List<ProductImage> deletedImage = new LinkedList<>(product.getProductImages());
//        List<ProductImage> deletedImage = product.getProductImages();
        for (int i = 0; i < deletedImage.size(); i++) {
            for (ImageDto imageDto : dto.getImages()) {
                if (deletedImage.get(i).getId() == imageDto.getId()) {
                    deletedImage.remove(i);
                }
            }
        }
        for (ProductImage image : deletedImage) {
            uploadUtil.deleteImage(image);
            productImageRepo.deleteById(image.getId());
        }
        product.getProductImages().removeAll(deletedImage);
        return new ProductResp(product);
    }

    @Override
    public List<ImageDto> saveImages(MultipartFile[] files, Integer id) throws IOException {
        Product product = productRepo.findById(id).orElseThrow(() -> {
            log.warn("Product {} not found", id);
            return new NotFoundException("Product not found");
        });

        List<ProductImage> productImages = Arrays.stream(uploadUtil.upload(files)).map((url) -> new ProductImage(null, url, new Product(id))).collect(Collectors.toList());
        productImageRepo.saveAll(productImages);
        return product.getProductImages().stream().map(i -> {
            i.setUrl(buildUrl(i.getUrl()));
            return new ImageDto(i);
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getFilter() {
        Map map = new HashMap();
        List<Material> materials = materialRepo.getMaterialByStatus(Constant.Status.ACTIVE);
        List<Category> categories = categoryRepo.getCategoryByStatus(Constant.Status.ACTIVE);
        List<TypeProduct> typeProducts = typeProductRepo.getTypeProductByStatus(Constant.Status.ACTIVE);
        map.put("materials", materials.stream().map(i -> new MaterialDto(i)).collect(Collectors.toList()));
        map.put("categories", categories.stream().map(i -> new CategoryDto(i)).collect(Collectors.toList()));
        map.put("typeProducts", typeProducts.stream().map(i -> new TypeProductDto(i)).collect(Collectors.toList()));
        return map;
    }

    @Override
    public List<ProductResp> getProductInCart(List<ProductInCartReq> productInCartReqs) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            Predicate categoryStatusPredicate = criteriaBuilder.isTrue(root.join("category",JoinType.INNER).get("status"));
            Predicate typeStatusPredicate = criteriaBuilder.isTrue(root.join("typeProduct",JoinType.INNER).get("status"));
            Predicate materialsStatusPredicate = criteriaBuilder.isTrue(root.join("material",JoinType.INNER).get("status"));
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), Constant.Status.ACTIVE);
            Predicate listPredicate = root.get("id").in(productInCartReqs.stream().map(i->i.getId()).collect(Collectors.toList()));
            return criteriaBuilder.and(
                    statusPredicate, categoryStatusPredicate, typeStatusPredicate, materialsStatusPredicate,listPredicate
            );
        };
        List<Product> products = productRepo.findAll(specification);
        for (ProductInCartReq product: productInCartReqs) {
            for (Product p : products) {
                if(p.getId() == product.getId()){
                    p.setQuantity(product.getQuantity());
                    break;
                }
            }
        }
        List<ProductResp> productResps = products.stream().map(i->new ProductResp(i)).collect(Collectors.toList());
        Pageable discountPageable = PageRequest.of(0,1,Sort.by(Sort.Direction.DESC,"discountStart"));
        Page<Discount> discounts = discountRepo.getDiscountActive(discountPageable,true,new Date());
        if(discounts.getTotalElements() >0){
            Discount discount = discounts.toList().get(0);
            for (ProductResp pi:productResps) {
                pi.setPrice(pi.getPriceSell());
                if(productDiscountRepo.getByDiscountIdAndProductId(discount.getId(),pi.getId())>0L ){
                    if(discount.getDiscountType()== Constant.TypeDiscount.PERCENT){
                        // xu ly cong tong hay cong don
                        BigDecimal priceSale = pi.getPriceSell().multiply(discount.getDiscountAmount().divide(BigDecimal.valueOf(100)));
                        pi.setPriceSell(pi.getPriceSell().subtract(priceSale));
                    }else{
                        pi.setPriceSell(pi.getPriceSell().subtract(discount.getDiscountAmount()));
                    }
                    if(pi.getPriceSell().compareTo(BigDecimal.ZERO) < 0){
                        pi.setPriceSell(BigDecimal.ZERO);
                    }
                }
            }
        }
        return productResps;
    }

    @Override
    public List<ProductResp> getProductInCartV2(List<OrderReq.Product> productInCartReqs) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            Predicate categoryStatusPredicate = criteriaBuilder.isTrue(root.join("category",JoinType.INNER).get("status"));
            Predicate typeStatusPredicate = criteriaBuilder.isTrue(root.join("typeProduct",JoinType.INNER).get("status"));
            Predicate materialsStatusPredicate = criteriaBuilder.isTrue(root.join("material",JoinType.INNER).get("status"));
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), Constant.Status.ACTIVE);
            Predicate listPredicate = root.get("id").in(productInCartReqs.stream().map(i->i.getId()).collect(Collectors.toList()));
            return criteriaBuilder.and(
                    statusPredicate, categoryStatusPredicate, typeStatusPredicate, materialsStatusPredicate,listPredicate
            );
        };
        List<Product> products = productRepo.findAll(specification);
        for (OrderReq.Product product: productInCartReqs) {
            for (Product p : products) {
                if(p.getId() == product.getId()){
                    p.setQuantity(product.getQuantity());
                    break;
                }
            }
        }
        List<ProductResp> productResps = products.stream().map(i->new ProductResp(i,false)).collect(Collectors.toList());
        // cause error if use constructor (Product product) .
        Pageable discountPageable = PageRequest.of(0,1,Sort.by(Sort.Direction.DESC,"discountStart"));
        Page<Discount> discounts = discountRepo.getDiscountActive(discountPageable,true,new Date());
        if(discounts.getTotalElements() >0){
            Discount discount = discounts.toList().get(0);
            for (ProductResp pi:productResps) {
                if(productDiscountRepo.getByDiscountIdAndProductId(discount.getId(),pi.getId())>0L ){
                    if(discount.getDiscountType()== Constant.TypeDiscount.PERCENT){
                        // xu ly cong tong hay cong don
                        BigDecimal priceSale = pi.getPriceSell().multiply(discount.getDiscountAmount().divide(BigDecimal.valueOf(100)));
                        pi.setPriceSell(pi.getPriceSell().subtract(priceSale));
                    }else{
                        pi.setPriceSell(pi.getPriceSell().subtract(discount.getDiscountAmount()));
                    }
                    if(pi.getPriceSell().compareTo(BigDecimal.ZERO) < 0){
                        pi.setPriceSell(BigDecimal.ZERO);
                    }
                }
            }
        }
        return productResps;
    }
    @Override
    public Map<String, Object> show(String url) throws IOException {
        return uploadUtil.show(url);
    }

    private String buildUrl(String resource) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
        builder.pathSegment("api","v1", "show");
        builder.queryParam("url", resource);
        return builder.toUriString();
    }

    private Boolean validSort(String by) {
        if (by == null) {
            return false;
        }
        return by.equals("priceSell") || by.equals("name") || by.equals("createAt");
    }
    private String buildLikeExp(final String query) {
        if (query == null || query.isEmpty()) {
            System.out.println("logger");
            return null;
        }
        return "%" + query.trim() + "%";
    }
}
