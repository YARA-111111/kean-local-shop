package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评分趋势分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingTrendVO {

    private String businessId;
    private String businessName;
    private BigDecimal currentRating;

    // 按时间段分组的评分数据
    private List<RatingPeriod> trendData;

    // 总体趋势 (RISING, DECLINING, STABLE)
    private String overallTrend;

    // 平均每月评论数
    private Double avgMonthlyReviews;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingPeriod {
        private String period;        // 时间段标识 (如 2024-01)
        private BigDecimal avgRating; // 平均评分
        private Long reviewCount;     // 评论数量
    }
}
