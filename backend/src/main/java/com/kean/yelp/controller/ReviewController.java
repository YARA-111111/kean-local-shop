package com.kean.yelp.controller;

import com.kean.yelp.dto.ApiResponse;
import com.kean.yelp.dto.PageVO;
import com.kean.yelp.entity.Review;
import com.kean.yelp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("服务运行中", "OK"));
    }

    /**
     * 分页获取所有评论
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageVO<Review>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageVO<Review> result = reviewService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取商家评论列表
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<PageVO<Review>>> getByBusinessId(
            @PathVariable String businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageVO<Review> result = reviewService.getByBusinessId(businessId, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取单个评论
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Review>> getById(@PathVariable String id) {
        Review review = reviewService.getById(id);
        if (review == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "评论不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(review));
    }
}
