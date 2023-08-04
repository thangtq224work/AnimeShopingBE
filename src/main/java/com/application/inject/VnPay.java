package com.application.inject;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
@Data
public class VnPay {
    @Value("${vnp.vnp_TmnCode}")
    private String vnp_TmnCode;
    @Value("${vnp.vnp_HashSecret}")
    private String vnp_HashSecret;
    @Value("${vnp.vnp_Version}")
    private String vnp_Version;
    @Value("${vnp.vnp_CurrCode}")
    private String vnp_CurrCode;
//    @Value("${vnp.vnp_Command}")
//    private String vnp_Command;
    @Value("${vnp.vnp_PayUrl}")
    private String vnp_PayUrl;
    @Value("${vnp.vnp_Returnurl}")
    private String vnp_Returnurl;
    @Value("${vnp.vnp_apiUrl}")
    private String vnp_apiUrl;
}
