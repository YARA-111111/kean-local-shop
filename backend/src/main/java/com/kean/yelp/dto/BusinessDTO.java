package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDTO {

    private String businessId;

    @NotBlank(message = "商家名称不能为空")
    private String name;

    private String address;

    private String city;

    private String state;

    private String postalCode;

    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度必须大于等于-90")
    @DecimalMax(value = "90.0", message = "纬度必须小于等于90")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度必须大于等于-180")
    @DecimalMax(value = "180.0", message = "经度必须小于等于180")
    private BigDecimal longitude;

    @DecimalMin(value = "1.0", message = "评分必须大于等于1")
    @DecimalMax(value = "5.0", message = "评分必须小于等于5")
    private BigDecimal stars;

    private Integer reviewCount;

    private Boolean isOpen;

    private String categories;
}
