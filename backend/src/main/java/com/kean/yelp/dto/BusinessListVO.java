package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商家列表响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessListVO {

    private String businessId;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal stars;
    private Integer reviewCount;
    private Boolean isOpen;
    private String categories;

    // 扩展信息
    private Integer priceRange;
    private Boolean hasDelivery;
    private Boolean hasTakeOut;
    private Boolean hasOutdoorSeating;
    private Boolean goodForKids;

    // 热度评分 (综合评分和评论数)
    private Double popularityScore;
}
