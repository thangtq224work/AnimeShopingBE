package com.application.dto.request;

import com.application.entity.Discount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountReq extends BaseDiscountReq {
    private Integer id;
    @NotBlank
    @Pattern(regexp = "[^<>{}\\/|;^:.+,~!?@#$^=&\\[\\]*]{1,120}",message = "Có lỗi xảy ra")
    private String name;
    @Pattern(regexp = "[^<>{}\\/|;^:.+,~!?@#$^=&\\[\\]*]{0,200}",message = "Có lỗi xảy ra")
    private String description;
    @NotNull
    private Boolean status;
    public DiscountReq(Discount discount){
        this.id = discount.getId();
        this.name = discount.getDiscountName();
        this.description = discount.getDescription();
        this.status = discount.getStatus();
        this.discountAmount = discount.getDiscountAmount();
        this.discountType = discount.getDiscountType();
        this.discountEnd = discount.getDiscountEnd();
        this.discountStart = discount.getDiscountStart();
    }

}
