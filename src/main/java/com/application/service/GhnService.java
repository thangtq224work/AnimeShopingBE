package com.application.service;

import com.application.common.Store;
import com.application.dto.response.ghn.*;
import com.application.inject.GhnBean;
import com.application.dto.request.CalculateFeeReq;
import com.application.exception.ParamInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GhnService {
    @Autowired
    private GhnBean ghnBean;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Store store;
    public Object getProvince(){
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token",ghnBean.getToken());
            HttpEntity entity = new HttpEntity<>(headers);
            log.info(ghnBean.getGetProvince());
//            ResponseEntity<String> response = template.exchange(ghnBean.getGetProvince(), HttpMethod.GET,entity,String.class);
//            if(response.getStatusCode().value() == 200){
//                JsonNode node = mapper.readTree(response.getBody());
//                System.out.println("//////");
//                System.out.println(node.get("code").asText());
//                System.out.println(node.get("message").asText());
//                System.out.println(node.get("data").asText());
//                return node.asText();
//            }
            ResponseEntity<ProvinceResp> response = template.exchange(ghnBean.getGetProvince(), HttpMethod.GET,entity, ProvinceResp.class);
            if(response.getStatusCode().value() == 200){
                return response.getBody().getData();
            }
        }
        catch (Exception ex){
            log.error(ex.getMessage());
            throw new ParamInvalidException("Param invalid");
        }
        return null;
    }
    @Cacheable(cacheManager = "addressCache", value = "district",key = "#province_id")
    public Object getDistrict(Integer province_id) {
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token", ghnBean.getToken());
            Map<String,Integer> map = new HashMap<>();
            map.put("province_id",province_id);
            HttpEntity entity = new HttpEntity<>(mapper.writeValueAsString(map),headers);
            log.info(ghnBean.getGetDistrict());
            ResponseEntity<DistrictResp> response = template.exchange(ghnBean.getGetDistrict(), HttpMethod.POST, entity, DistrictResp.class);
            if (response.getStatusCode().value() == 200) {
                return response.getBody().getData();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ParamInvalidException("Param invalid");
        }
        return null;
    }
    @Cacheable(cacheManager = "addressCache", value = "ward",key = "#district_id")
    public Object getWard(Integer district_id){
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token",ghnBean.getToken());
            MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
            queryParams.put("district_id", Collections.singletonList(district_id.toString()));
            HttpEntity entity = new HttpEntity<>(headers);
            String url = ServletUriComponentsBuilder.fromHttpUrl(ghnBean.getGetWard()).queryParams(queryParams).toUriString();
            log.info(ghnBean.getGetWard());
            ResponseEntity<WardResp> response = template.exchange(url, HttpMethod.GET,entity, WardResp.class);

            if(response.getStatusCode().value() == 200){
                return response.getBody().getData();
            }
        }
        catch (Exception ex){
            log.error(ex.getMessage());
            throw new ParamInvalidException("Param invalid");
        }
        return null;
    }
    public Object calculate(CalculateFeeReq req){
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token",ghnBean.getToken());
            Map<String,Object> body = new HashMap<>();
            body.put("to_ward_code",req.getWardCode());
            body.put("to_district_id",req.getDistrictId());
            body.put("weight",req.getWeight());
            body.put("insurance_value",req.getTotalPrice() < 5000000?req.getTotalPrice():5000000);
//            body.put("service_id",req.getServiceType()); // ghn api test has error . it just accepts service_id = 2;
            body.put("service_type_id",2); // ghn api test has error . it just accepts service_id = 2;
            HttpEntity entity = new HttpEntity<>(mapper.writeValueAsString(body),headers);
            log.info(ghnBean.getCalculateFee());
            ResponseEntity<CalculateFeeResp> response = template.exchange(ghnBean.getCalculateFee(), HttpMethod.POST,entity, CalculateFeeResp.class);
            if(response.getStatusCode().value() == 200){
                return response.getBody().getFee();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new ParamInvalidException("Param invalid");
        }
        return null;
    }
    public StoreResp.StoreGhn getStore(){
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token",ghnBean.getToken());
            HttpEntity entity = new HttpEntity<>(headers);
            log.info(ghnBean.getGetStore());
            ResponseEntity<StoreResp> response = template.exchange(ghnBean.getGetStore(), HttpMethod.GET,entity, StoreResp.class);
            if(response.getStatusCode().value() == 200){
                return response.getBody().getStore().getStores().get(0);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new ParamInvalidException("Param invalid");
        }
        return null;
    }
    @Cacheable(cacheManager = "addressCache", value = "service",key = "#to_district_id")
    public Object getService(Integer to_district_id){
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token",ghnBean.getToken());
            Map<String,Integer> map = new HashMap<>();
            map.put("shop_id", ghnBean.getShopId());
            map.put("from_district", store.getStoreGhn().getDistrictId());
            map.put("to_district", to_district_id);
            System.out.println(mapper.writeValueAsString(map));
            HttpEntity entity = new HttpEntity<>(mapper.writeValueAsString(map),headers);
            log.info(ghnBean.getGetService());
            ResponseEntity<ServiceResp> response = template.exchange(ghnBean.getGetService(), HttpMethod.POST,entity, ServiceResp.class);
            if(response.getStatusCode().value() == 200){
                return response.getBody().getServices();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new ParamInvalidException("Param invalid");
        }
        return null;
    }
}

