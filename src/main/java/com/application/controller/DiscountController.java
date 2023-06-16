package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.DiscountReq;
import com.application.dto.request.ProductDiscountReq;
import com.application.service.DiscountProductService;
import com.application.service.DiscountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discount")
@CrossOrigin("*")
public class DiscountController {
    @Autowired
    private DiscountService discountService;
    @Autowired
    private DiscountProductService discountProductService;
    //    @RolesAllowed({Constant.AccountRole.USER,Constant.AccountRole.ADMIN})
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
            @RequestParam(name = "size",required = false,defaultValue = "5" )Integer pageSize,
            @RequestParam(name = "search",required = false,defaultValue = "") String search,
            @RequestParam(name = "status",required = false) Boolean status,
            @RequestParam(name = "from",required = false) String from,
            @RequestParam(name = "to",required = false) String to
    ) {
        System.out.println(from + " - " + to);
        Sort sort = Sort.by(Sort.Direction.DESC,"discountStart");
        Pageable pageable = PageRequest.of(page, pageSize,sort);
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountService.findAll( pageable,  search,  status,  from,  to)).build(), HttpStatus.OK);

    }

//    @RolesAllowed({Constant.AccountRole.ADMIN})
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountService.findById(id)).build(), HttpStatus.OK);
//        return ResponseEntity.ok(new Response(Calendar.getInstance().getTime(), ResponseTemplate.SUCCESS,discountService.findById(id)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getDiscountById(@PathVariable("id") Integer id,
                                                    @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
                                                    @RequestParam(name = "size",required = false,defaultValue = "2" )Integer pageSize,
                                                    @RequestParam(name = "categories",required = false,defaultValue = "" )Integer[] category,
                                                    @RequestParam(name = "name",required = false,defaultValue = "" )String name) {
        Pageable pageable = PageRequest.of(page,pageSize);
        System.out.println("logger");
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountProductService.getById(id,pageable,category,name)).build(),HttpStatus.OK);
    }
    @PostMapping("/product/save")
    public ResponseEntity<?> saveProductIntoDiscount(@RequestBody ProductDiscountReq productDiscountReq){
        System.out.println("Loi");
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountProductService.save(productDiscountReq)).build(),HttpStatus.OK);
    }
    @PostMapping("/product/remove")
    public ResponseEntity<?> removeProductIntoDiscount(@RequestBody ProductDiscountReq productDiscountReq){
        productDiscountReq.setStatus(false);
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountProductService.remove(productDiscountReq)).build(),HttpStatus.OK);
    }
    @PostMapping("/product/save-all")
    public ResponseEntity<?> saveAllProductIntoDiscount(@RequestBody List<ProductDiscountReq> productDiscountReqs){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountProductService.saveAll(productDiscountReqs)).build(),HttpStatus.OK);
    }
    @PostMapping("/product/remove-all")
    public ResponseEntity<?> removeAllProductIntoDiscount(@RequestBody List<ProductDiscountReq> productDiscountReqs){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountProductService.removeAll(productDiscountReqs)).build(),HttpStatus.OK);
    }
    @PostMapping("/new")
    public ResponseEntity<?> addDiscount(@Valid @RequestBody DiscountReq discountReq) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountService.save(discountReq)).build(),HttpStatus.OK);
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateDiscount(@Valid @RequestBody DiscountReq discountReq) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(discountService.update(discountReq)).build(),HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        discountService.delete(id);
        return new ResponseEntity<>(ResponseDataTemplate.OK.build(),HttpStatus.OK);
    }
}