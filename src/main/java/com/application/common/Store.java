package com.application.common;

import com.application.dto.response.ghn.StoreResp;
import com.application.service.GhnService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Store {
    @Autowired
    private GhnService ghnService;
    @Getter
    private StoreResp.StoreGhn storeGhn;
    public void init(){
        this.storeGhn = ghnService.getStore();
    }
}
