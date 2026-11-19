package com.kean.yelp.service;

import com.kean.yelp.dto.*;
import com.kean.yelp.entity.Business;
import com.kean.yelp.entity.BusinessAttribute;
import com.kean.yelp.entity.BusinessHour;
import com.kean.yelp.mapper.BusinessAttributeMapper;
import com.kean.yelp.mapper.BusinessHourMapper;
import com.kean.yelp.mapper.BusinessMapper;
import com.kean.yelp.mapper.ReviewMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商家服务层
 */
@Service
public class BusinessService {

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private BusinessAttributeMapper attributeMapper;

    @Autowired
    private BusinessHourMapper hourMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * 高级条件查询商家列表
     */
    public PageVO<BusinessListVO> queryBusinessList(BusinessQueryDTO query) {
        // 处理组合查询模式
        if (query.getQueryMode() != null) {
            applyQueryMode(query);
        }

        List<BusinessListVO> list = businessMapper.queryBusinessList(query);
        long total = businessMapper.countBusinessList(query);
        return PageVO.of(list, total, query.getPage(), query.getSize());
    }

    /**
     * 应用组合查询模式
     */
    private void applyQueryMode(BusinessQueryDTO query) {
        switch (query.getQueryMode()) {
            case "VALUE_FOR_MONEY":
                query.setMinStars(new BigDecimal("4.0"));
                query.setPriceRange(1);
                break;
            case "FAMILY_FRIENDLY":
                query.setGoodForKids(true);
                break;
            case "HIGH_END":
                query.setMinStars(new BigDecimal("4.0"));
                query.setPriceRange(3);
                break;
        }
    }

    /**
     * 获取商家详情
     */
    public BusinessDetailVO getBusinessDetail(String businessId) {
        Business business = businessMapper.selectById(businessId);
        if (business == null) {
            return null;
        }

        BusinessDetailVO vo = new BusinessDetailVO();
        BeanUtils.copyProperties(business, vo);

        // 解析类别列表
        if (business.getCategories() != null) {
            vo.setCategoryList(Arrays.stream(business.getCategories().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
        }

        // 获取属性
        List<BusinessAttribute> attributes = attributeMapper.findByBusinessId(businessId);
        Map<String, String> attrMap = attributes.stream()
                .collect(Collectors.toMap(
                        BusinessAttribute::getAttributeName,
                        BusinessAttribute::getAttributeValue,
                        (v1, v2) -> v1));
        vo.setAttributes(attrMap);

        // 获取价格区间
        String priceStr = attrMap.get("RestaurantsPriceRange2");
        if (priceStr != null) {
            try {
                vo.setPriceRange(Integer.parseInt(priceStr));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // 获取营业时间
        List<BusinessHour> hours = hourMapper.findByBusinessId(businessId);
        List<BusinessDetailVO.BusinessHourVO> hourVOs = hours.stream()
                .map(h -> new BusinessDetailVO.BusinessHourVO(
                        h.getDayOfWeek(),
                        h.getOpenTime() != null ? h.getOpenTime().toString() : null,
                        h.getCloseTime() != null ? h.getCloseTime().toString() : null,
                        h.getIsClosed()))
                .collect(Collectors.toList());
        vo.setBusinessHours(hourVOs);

        // 获取评分分布
        List<Map<String, Object>> distribution = reviewMapper.getRatingDistribution(businessId);
        Map<Integer, Long> ratingDist = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDist.put(i, 0L);
        }
        for (Map<String, Object> row : distribution) {
            Integer rating = ((Number) row.get("rating")).intValue();
            Long count = ((Number) row.get("count")).longValue();
            ratingDist.put(rating, count);
        }
        vo.setRatingDistribution(ratingDist);

        return vo;
    }

    /**
     * 获取评分趋势分析
     */
    public RatingTrendVO getRatingTrend(String businessId) {
        Business business = businessMapper.selectById(businessId);
        if (business == null) {
            return null;
        }

        RatingTrendVO vo = new RatingTrendVO();
        vo.setBusinessId(businessId);
        vo.setBusinessName(business.getName());
        vo.setCurrentRating(business.getStars());

        // 获取按月统计的评分趋势
        List<Map<String, Object>> trendData = reviewMapper.getRatingTrendByMonth(businessId);
        List<RatingTrendVO.RatingPeriod> periods = trendData.stream()
                .map(row -> new RatingTrendVO.RatingPeriod(
                        (String) row.get("period"),
                        new BigDecimal(String.valueOf(row.get("avg_rating"))),
                        ((Number) row.get("review_count")).longValue()))
                .collect(Collectors.toList());
        vo.setTrendData(periods);

        // 计算总体趋势
        if (periods.size() >= 2) {
            BigDecimal first = periods.get(0).getAvgRating();
            BigDecimal last = periods.get(periods.size() - 1).getAvgRating();
            int cmp = last.compareTo(first);
            if (cmp > 0) {
                vo.setOverallTrend("RISING");
            } else if (cmp < 0) {
                vo.setOverallTrend("DECLINING");
            } else {
                vo.setOverallTrend("STABLE");
            }
        } else {
            vo.setOverallTrend("STABLE");
        }

        // 计算平均每月评论数
        if (!periods.isEmpty()) {
            double avgReviews = periods.stream()
                    .mapToLong(RatingTrendVO.RatingPeriod::getReviewCount)
                    .average()
                    .orElse(0);
            vo.setAvgMonthlyReviews(avgReviews);
        } else {
            vo.setAvgMonthlyReviews(0.0);
        }

        return vo;
    }

    /**
     * 获取热门商家
     */
    public List<BusinessListVO> getPopularBusinesses(int limit) {
        return businessMapper.findPopularBusinesses(limit);
    }

    /**
     * 获取高评分商家
     */
    public List<BusinessListVO> getTopRatedBusinesses(BigDecimal minStars, int limit) {
        return businessMapper.findTopRatedBusinesses(minStars, limit);
    }

    /**
     * 获取性价比商家
     */
    public List<BusinessListVO> getValueForMoneyBusinesses(int limit) {
        return businessMapper.findValueForMoneyBusinesses(limit);
    }

    /**
     * 获取家庭友好型商家
     */
    public List<BusinessListVO> getFamilyFriendlyBusinesses(int limit) {
        return businessMapper.findFamilyFriendlyBusinesses(limit);
    }

    /**
     * 获取高端餐厅
     */
    public List<BusinessListVO> getHighEndBusinesses(int limit) {
        return businessMapper.findHighEndBusinesses(limit);
    }

    /**
     * 根据类别查找商家
     */
    public List<BusinessListVO> findByCategory(String category, int page, int size) {
        return businessMapper.findByCategory(category, page * size, size);
    }

    /**
     * 查找附近商家
     */
    public List<BusinessListVO> findNearbyBusinesses(BigDecimal lat, BigDecimal lng, double radiusKm, int limit) {
        return businessMapper.findNearbyBusinesses(lat, lng, radiusKm, limit);
    }

    /**
     * 创建商家
     */
    public Business createBusiness(BusinessDTO dto) {
        Business business = new Business();
        BeanUtils.copyProperties(dto, business);
        business.setBusinessId(UUID.randomUUID().toString().replace("-", "").substring(0, 22));
        businessMapper.insert(business);
        return business;
    }

    /**
     * 更新商家
     */
    public Business updateBusiness(String businessId, BusinessDTO dto) {
        Business business = businessMapper.selectById(businessId);
        if (business == null) {
            return null;
        }
        BeanUtils.copyProperties(dto, business, "businessId");
        businessMapper.updateById(business);
        return business;
    }

    /**
     * 删除商家
     */
    public boolean deleteBusiness(String businessId) {
        return businessMapper.deleteById(businessId) > 0;
    }

    /**
     * 获取商家基本信息
     */
    public Business getById(String businessId) {
        return businessMapper.selectById(businessId);
    }
}
