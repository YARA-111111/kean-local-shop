package com.kean.yelp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kean.yelp.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 评论Mapper接口
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 获取商家评分分布
     */
    List<Map<String, Object>> getRatingDistribution(@Param("businessId") String businessId);

    /**
     * 获取商家评分趋势 (按月统计)
     */
    List<Map<String, Object>> getRatingTrendByMonth(@Param("businessId") String businessId);

    /**
     * 分页获取商家评论
     */
    List<Review> findByBusinessId(@Param("businessId") String businessId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    /**
     * 统计商家评论总数
     */
    long countByBusinessId(@Param("businessId") String businessId);

    /**
     * 分页获取所有评论
     */
    List<Review> findAllPaged(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计所有评论总数
     */
    long countAll();
}
