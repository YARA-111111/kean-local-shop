package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家查询条件DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessQueryDTO {

    // 关键词搜索
    private String keyword;

    // 城市筛选
    private String city;

    // 州筛选
    private String state;

    // 类别筛选
    private String category;

    // 价格区间筛选 (1-4)
    private Integer priceRange;

    // 最低评分
    private BigDecimal minStars;

    // 最高评分
    private BigDecimal maxStars;

    // 最低评论数
    private Integer minReviewCount;

    // 是否营业
    private Boolean isOpen;

    // 设施筛选条件
    private Boolean hasDelivery;         // 外卖
    private Boolean hasTakeOut;          // 外带
    private Boolean hasReservation;      // 可预订
    private Boolean hasOutdoorSeating;   // 户外座位
    private Boolean goodForKids;         // 适合儿童
    private Boolean wheelchairAccessible; // 轮椅可访问
    private Boolean hasWifi;             // WiFi

    // 组合查询模式
    private String queryMode;  // VALUE_FOR_MONEY(性价比), FAMILY_FRIENDLY(家庭友好), HIGH_END(高端)

    // 排序字段
    private String sortBy;  // stars, reviewCount, createdAt

    // 排序方向
    private String sortDir; // asc, desc

    // 分页
    private Integer page;
    private Integer size;

    public Integer getPage() {
        return page == null ? 0 : Math.max(0, Math.min(page, 100000));
    }

    public Integer getSize() {
        return size == null ? 10 : Math.max(1, Math.min(size, 100));
    }

    public String getSortBy() {
        return sortBy == null ? "stars" : sortBy;
    }

    public String getSortDir() {
        // 只允许 asc 或 desc，防止SQL注入
        if ("asc".equalsIgnoreCase(sortDir)) {
            return "ASC";
        }
        return "DESC";
    }

    public Integer getOffset() {
        return getPage() * getSize();
    }
}
