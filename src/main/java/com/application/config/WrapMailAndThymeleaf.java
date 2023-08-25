package com.application.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WrapMailAndThymeleaf {
    MailInfor mailInfor;
    RegisterForThymeleaf register;
}
