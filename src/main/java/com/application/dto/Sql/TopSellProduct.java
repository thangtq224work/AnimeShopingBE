package com.application.dto.Sql;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSellProduct {
    private Integer pId;
    private String pName;
    private Integer quantity;


}
