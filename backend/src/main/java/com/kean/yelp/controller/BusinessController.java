package com.kean.yelp.controller;

import com.kean.yelp.dto.*;
import com.kean.yelp.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("服务运行中", "OK"));
    }

    /**
     * 高级条件查询商家列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageVO<BusinessListVO>>> queryBusinessList(BusinessQueryDTO query) {
        PageVO<BusinessListVO> result = businessService.queryBusinessList(query);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取商家详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessDetailVO>> getBusinessDetail(@PathVariable String id) {
        BusinessDetailVO detail = businessService.getBusinessDetail(id);
        if (detail == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "商家不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    /**
     * 获取商家评分趋势
     */
    @GetMapping("/{id}/rating-trend")
    public ResponseEntity<ApiResponse<RatingTrendVO>> getRatingTrend(@PathVariable String id) {
        RatingTrendVO trend = businessService.getRatingTrend(id);
        if (trend == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "商家不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(trend));
    }

    /**
     * 获取热门商家
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> getPopularBusinesses(
            @RequestParam(defaultValue = "10") int limit) {
        List<BusinessListVO> list = businessService.getPopularBusinesses(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 获取高评分商家
     */
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> getTopRatedBusinesses(
            @RequestParam(defaultValue = "4.0") BigDecimal minStars,
            @RequestParam(defaultValue = "10") int limit) {
        List<BusinessListVO> list = businessService.getTopRatedBusinesses(minStars, limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 获取性价比商家
     */
    @GetMapping("/value-for-money")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> getValueForMoneyBusinesses(
            @RequestParam(defaultValue = "10") int limit) {
        List<BusinessListVO> list = businessService.getValueForMoneyBusinesses(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 获取家庭友好型商家
     */
    @GetMapping("/family-friendly")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> getFamilyFriendlyBusinesses(
            @RequestParam(defaultValue = "10") int limit) {
        List<BusinessListVO> list = businessService.getFamilyFriendlyBusinesses(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 获取高端餐厅
     */
    @GetMapping("/high-end")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> getHighEndBusinesses(
            @RequestParam(defaultValue = "10") int limit) {
        List<BusinessListVO> list = businessService.getHighEndBusinesses(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 根据类别查找商家
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> findByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<BusinessListVO> list = businessService.findByCategory(category, page, size);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 查找附近商家
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<BusinessListVO>>> findNearbyBusinesses(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "5") double radiusKm,
            @RequestParam(defaultValue = "10") int limit) {
        List<BusinessListVO> list = businessService.findNearbyBusinesses(lat, lng, radiusKm, limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
