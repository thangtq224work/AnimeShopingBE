package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.*;
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
import java.net.URI;
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

    @Override
    public PageData<ProductResp> getAll(Pageable pageable) {
        Page<Product> page = productRepo.findAll(pageable);
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
        return PageData.of(products, products.toList().stream().map(i -> new ProductResp(i)).collect(Collectors.toList()));
    }

    @Override
    public ProductResp insert(ProductDto dto) {
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
    public ProductResp update(ProductDto dto) throws IOException {
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
    public Map<String, Object> show(String url) throws IOException {
        return uploadUtil.show(url);
    }

    private String buildUrl(String resource) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
        builder.pathSegment("product", "show");
        builder.queryParam("url", resource);
        return builder.toUriString();
    }

    private Boolean validSort(String by) {
        if (by == null) {
            return false;
        }
        return by.equals("priceSell") || by.equals("name") || by.equals("createAt");
    }
}
