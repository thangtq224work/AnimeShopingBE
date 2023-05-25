package com.application.service.impl;

import com.application.common.PageData;
import com.application.dto.ImageDto;
import com.application.dto.ProductDto;
import com.application.dto.ProductImageRepo;
import com.application.dto.ProductResp;
import com.application.entity.*;
import com.application.exception.NotFoundException;
import com.application.repository.*;
import com.application.service.*;
import com.application.utils.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return PageData.of(page,page.toList().stream().map(i->new ProductResp(i)).collect(Collectors.toList()));
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
        for (int i = 0;i< deletedImage.size();i++){
            for (ImageDto imageDto : dto.getImages()){
                if(deletedImage.get(i).getId() == imageDto.getId()){
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
        Product product = productRepo.findById(id).orElseThrow(()->{
            log.warn("Product {} not found",id);
            return new NotFoundException("Product not found");
        });

        List<ProductImage> productImages =  Arrays.stream(uploadUtil.upload(files)).map((url)-> new ProductImage(null,url,new Product(id))).collect(Collectors.toList());
        productImageRepo.saveAll(productImages);
        return product.getProductImages().stream().map(i->{
            i.setUrl(buildUrl(i.getUrl()));
            return new ImageDto(i);
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> show(String url) throws IOException {
        return uploadUtil.show(url);
    }
    private String buildUrl(String resource){
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
        builder.pathSegment("product","show");
        builder.queryParam("url",resource);
        return builder.toUriString();
    }

}
