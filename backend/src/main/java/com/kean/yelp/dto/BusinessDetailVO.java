package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商家详情响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDetailVO {

    private String businessId;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal stars;
    private Integer reviewCount;
    private Boolean isOpen;
    private String categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 扩展信息
    private Integer priceRange;
    private List<String> categoryList;
    private Map<String, String> attributes;
    private List<BusinessHourVO> businessHours;

    // 评分分布 (1-5星各多少)
    private Map<Integer, Long> ratingDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessHourVO {
        private String dayOfWeek;
        private String openTime;
        private String closeTime;
        private Boolean isClosed;
    }
}
