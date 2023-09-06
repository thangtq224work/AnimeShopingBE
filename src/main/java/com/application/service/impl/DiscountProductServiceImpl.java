package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.CategoryDto;
import com.application.dto.request.DiscountReq;
import com.application.dto.request.ProductDiscountReq;
import com.application.entity.Product;
import com.application.entity.ProductDiscount;
import com.application.exception.NotFoundException;
import com.application.repository.CategoryRepo;
import com.application.repository.ProductDiscountRepo;
import com.application.repository.ProductRepo;
import com.application.service.DiscountProductService;
import com.application.service.DiscountService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DiscountProductServiceImpl implements DiscountProductService {
    private final DiscountService discountService;
    private final CategoryRepo categoryRepo;
    private final ProductDiscountRepo productDiscountRepo;
    private final ProductRepo productRepo;

    @Override
    public ProductDiscountReq save(ProductDiscountReq productDiscountDto) {
        DiscountReq discountReq = discountService.findById(productDiscountDto.getDiscountId());
        System.out.println(productDiscountDto.getProduct());
        Product productEntity = productRepo.findById(productDiscountDto.getProduct().getId()).orElseThrow(()->new NotFoundException("Product not found : "+productDiscountDto.getProduct().getId()) );
        ProductDiscount productDiscount;
        System.out.println("lỗi3");

        if (productDiscountDto.getId() != null) {
            productDiscountRepo.findById(productDiscountDto.getId()).orElseThrow(() -> new NotFoundException("Product discount not found : " + productDiscountDto.getId()));
        }
        System.out.println("lỗi2");
        productDiscountDto.setStatus(true);
        productDiscount= productDiscountRepo.save(new ProductDiscount(productDiscountDto));
        System.out.println("lỗi");
        ProductDiscountReq.ProductForDiscountDto productForDiscountDto = new ProductDiscountReq.ProductForDiscountDto(productEntity);
        productForDiscountDto.setPriceSale(this.calculatePriceSale(discountReq,productForDiscountDto));
        ProductDiscountReq p = new ProductDiscountReq(productDiscount);
        p.setProduct(productDiscountDto.getProduct());
        return p;
    }

    @Override
    public ProductDiscountReq remove(ProductDiscountReq productDiscountDto) {
        DiscountReq discountReq = discountService.findById(productDiscountDto.getDiscountId());
        Product productEntity = productRepo.findById(productDiscountDto.getProduct().getId()).orElseThrow(()->new NotFoundException("Product not found : "+productDiscountDto.getProduct().getId()) );
        ProductDiscount productDiscountEntity;
        if (productDiscountDto.getId() != null) {
            productDiscountRepo.findById(productDiscountDto.getId()).orElseThrow(() -> new NotFoundException("Product discount not found : " + productDiscountDto.getId()));
        }
        productDiscountDto.setStatus(false);
        productDiscountEntity = productDiscountRepo.save(new ProductDiscount(productDiscountDto));

        ProductDiscountReq.ProductForDiscountDto productForDiscountDto = new ProductDiscountReq.ProductForDiscountDto(productEntity);
        productForDiscountDto.setPriceSale(this.calculatePriceSale(discountReq,productForDiscountDto));
        ProductDiscountReq p = new ProductDiscountReq(productDiscountEntity);
        p.setProduct(productDiscountDto.getProduct());
        return p;
    }

    @Override
    public List<ProductDiscountReq> saveAll(List<ProductDiscountReq> productDiscountDto) {
        return productDiscountDto.stream().map((i)-> this.save(i)).collect(Collectors.toList());
    }

    @Override
    public List<ProductDiscountReq> removeAll(List<ProductDiscountReq> productDiscountDto) {
        return productDiscountDto.stream().map((i)-> this.remove(i)).collect(Collectors.toList());
    }

    @Override
    public PageData<ProductDiscountReq> getById(Integer id, Pageable pageable, Integer[] category, String name) {
        DiscountReq discountReq = discountService.findById(id);
        Specification<Product> productEntitySpecification = (root, query, criteriaBuilder) -> {
            Predicate namePredicate = buildLikeExp(name) == null?criteriaBuilder.and(): criteriaBuilder.like(root.get("name"),buildLikeExp(name));
            Predicate statusPredicate = criteriaBuilder.isTrue(root.get("status"));
            Predicate categoriesPredicate = (category == null || category.length == 0) ? criteriaBuilder.and():root.get("category").in(category);
          return  criteriaBuilder.and(namePredicate,statusPredicate,categoriesPredicate);
        };
        Page<Product> p = productRepo.findAll(productEntitySpecification,pageable);
        System.out.println("size : "+p.toList().size());
        List<ProductDiscount> productDiscountEntities = productDiscountRepo.getByDiscountId(id);
        List<ProductDiscountReq> l =p.toList().stream().map((i)->new ProductDiscountReq(null,null,this.getV2(i,discountReq),id)).collect(Collectors.toList());
        for (int i = 0; i < l.size(); i++) {
            for(ProductDiscount item:productDiscountEntities){
                if((long)l.get(i).getProduct().getId()== item.getProductId()){
                    l.get(i).setId(item.getId());
                    l.get(i).setStatus(item.getStatus());
                }
            }
        }
        return PageData.of(p,l);
    }
    @Deprecated()
    private ProductDiscountReq.ProductForDiscountDto get(Product product,DiscountReq discountReq){
        ProductDiscountReq.ProductForDiscountDto productForDiscountDto = new ProductDiscountReq.ProductForDiscountDto(product);
        BigDecimal priceSale = this.calculatePriceSale(discountReq,productForDiscountDto);
        productForDiscountDto.setPriceSale(priceSale);
        productForDiscountDto.setCategoryId(new CategoryDto(categoryRepo.findById(product.getCategory().getId()).orElseThrow(()->new NotFoundException("Category not found"))));
        return productForDiscountDto;
    }
    private ProductDiscountReq.ProductForDiscountDto getV2(Product product,DiscountReq discountReq){
        ProductDiscountReq.ProductForDiscountDto productForDiscountDto = new ProductDiscountReq.ProductForDiscountDto(product);
        BigDecimal priceSale = this.calculatePriceSale(discountReq,productForDiscountDto);
        productForDiscountDto.setPriceSale(priceSale);
//        List<Object[]> objects = productRepo.getCustom(product.getId());
//        for (Object[] obs : objects){
//            for (Object ob:obs) {
//                if(ob instanceof SeriesEntity){
//                    productForDiscountDto.setSeriesID(mapper.toSeriesReq((SeriesEntity) ob));
//                }
//                else if(ob instanceof CategoriesEntity){
//                    productForDiscountDto.setCategoryId(mapper.toCategoriesReq((CategoriesEntity) ob));
//                }
//                else if(ob instanceof ManufacturerEntity){
//                    productForDiscountDto.setManufacturerID(mapper.toManufacturerReq((ManufacturerEntity) ob));
//                }
//            }
//        }
        return productForDiscountDto;
    }
    private BigDecimal calculatePriceSale(DiscountReq discountReq, ProductDiscountReq.ProductForDiscountDto productForDiscountDto){

        BigDecimal priceSale;
        if(discountReq.getDiscountType() == Constant.TypeDiscount.PERCENT){
            BigDecimal sale = productForDiscountDto.getPrice().multiply(discountReq.getDiscountAmount().multiply(BigDecimal.valueOf(0.01)));
            priceSale = productForDiscountDto.getPrice().subtract(sale);
        }
        else{
            priceSale = productForDiscountDto.getPrice().subtract(discountReq.getDiscountAmount());
            if(priceSale.compareTo(BigDecimal.ZERO) < 0){
                priceSale= BigDecimal.ZERO;
            }
        }
        return priceSale;
    }

    private String buildLikeExp(final String query) {
        if (query == null || query.isEmpty()) {
            System.out.println("logger");
            return null;
        }
        return "%" + query.trim() + "%";
    }
}
