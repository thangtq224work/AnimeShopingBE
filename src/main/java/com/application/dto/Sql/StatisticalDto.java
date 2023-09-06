package com.application.dto.Sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalDto {
    private BigDecimal profit;
    private BigDecimal expense;

}
