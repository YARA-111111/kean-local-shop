package com.kean.yelp.service;

import com.kean.yelp.dto.PageVO;
import com.kean.yelp.entity.Review;
import com.kean.yelp.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务层
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * 分页获取商家评论
     */
    public PageVO<Review> getByBusinessId(String businessId, int page, int size) {
        List<Review> reviews = reviewMapper.findByBusinessId(businessId, page * size, size);
        long total = reviewMapper.countByBusinessId(businessId);
        return PageVO.of(reviews, total, page, size);
    }

    /**
     * 获取单个评论
     */
    public Review getById(String reviewId) {
        return reviewMapper.selectById(reviewId);
    }

    /**
     * 分页获取所有评论
     */
    public PageVO<Review> getAll(int page, int size) {
        List<Review> reviews = reviewMapper.findAllPaged(page * size, size);
        long total = reviewMapper.countAll();
        return PageVO.of(reviews, total, page, size);
    }
}
