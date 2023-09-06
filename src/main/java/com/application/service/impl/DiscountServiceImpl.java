package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.request.DiscountReq;
import com.application.entity.Discount;
import com.application.exception.NotFoundException;
import com.application.exception.ParamInvalidException;
import com.application.repository.DiscountRepo;
import com.application.service.DiscountService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepo discountRepo;

    @Autowired
    public DiscountServiceImpl(DiscountRepo discountRepo) {
        this.discountRepo =discountRepo;
    }

    @Override
    public DiscountReq save(DiscountReq discountReq) {
        Discount discountEntity = new Discount(discountReq);
        discountEntity =discountRepo.save(discountEntity);
        return new DiscountReq(discountEntity);
    }

    @Override
    public void delete(Integer id) {

        Discount discountEntity =discountRepo.findById(id).orElseThrow(()->new NotFoundException("Discount not found : "+id));
        discountEntity.setStatus(Constant.Status.NON_ACTIVE);
        discountRepo.save(discountEntity);
    }

    @Override
    public DiscountReq update(DiscountReq discountReq) {
        if(discountReq.getId() == null) throw new ParamInvalidException("id is not null ");

        Discount discountEntity = discountRepo.findById(discountReq.getId()).orElseThrow(()->new NotFoundException("Discount not found : "+discountReq.getId()));
        discountEntity = new Discount(discountReq);
        discountEntity = discountRepo.save(discountEntity);
        return new DiscountReq(discountEntity);
    }

    @Override
    public PageData<DiscountReq> findAll(Pageable pageable, String name, Boolean status, String from, String to){
        Specification<Discount> specification = (root, cq, cb) -> {
            System.out.println("apply status for discount : "+name + " --- "+status);
            Predicate predicate = buildLikeExp(name) == null?cb.and(): cb.like(root.get("discountName"),buildLikeExp(name));
//            Predicate predicate = cb.equal(root.get("discountName"),name);
//            Predicate predicate = cb.equal(root.get("discountName"),name);
            Predicate statusPredicate = status == null ? cb.equal(root.get("status"),Constant.Status.ACTIVE):cb.equal(root.get("status"),status);

            return cb.and(predicate,statusPredicate);
//                    QueryUtils.buildLikeFilter(root, cb, name, "discountName"),
//                    QueryUtils.buildGreaterThanFilter(root, cb, "discountStart", from, Constant.DATE_FORMAT_2),
//                    QueryUtils.buildLessThanFilter(root, cb, "discountEnd", to, Constant.DATE_FORMAT_2),
//                    QueryUtils.buildEqFilter(root, cb, "status", status));
        };

        discountRepo.findAll().stream().forEach(i-> System.out.println(i.toString()));
        Page<Discount> page = discountRepo.findAll(specification,pageable);
        page.toList().stream().forEach(i-> System.out.println(i));
        return PageData.of(page,page.toList().stream().map((item)->new DiscountReq(item)).collect(Collectors.toList()));
    }

    @Override
    public DiscountReq findById(Integer id) {

        Discount discountEntity =discountRepo.findById(id).orElseThrow(()->new NotFoundException("Discount not found : "+id));
//        blackListService.getBlackListByDiscountId(discountEntity.getId());
        return new DiscountReq(discountEntity);
    }

    private static String buildLikeExp(final String query) {
        if (query == null || query.isEmpty()) {
            System.out.println("logger");
            return null;
        }
        return "%" + query.trim() + "%";
    }
}
