package com.kean.yelp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kean.yelp.dto.BusinessListVO;
import com.kean.yelp.dto.BusinessQueryDTO;
import com.kean.yelp.entity.Business;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家Mapper接口
 */
@Mapper
public interface BusinessMapper extends BaseMapper<Business> {

    /**
     * 高级条件查询商家列表
     */
    List<BusinessListVO> queryBusinessList(@Param("query") BusinessQueryDTO query);

    /**
     * 统计高级查询总数
     */
    long countBusinessList(@Param("query") BusinessQueryDTO query);

    /**
     * 获取热门商家 (综合评分和评论数)
     */
    List<BusinessListVO> findPopularBusinesses(@Param("limit") int limit);

    /**
     * 获取高评分商家
     */
    List<BusinessListVO> findTopRatedBusinesses(@Param("minStars") BigDecimal minStars,
                                                 @Param("limit") int limit);

    /**
     * 性价比商家查询 (高评分 + 低价格)
     */
    List<BusinessListVO> findValueForMoneyBusinesses(@Param("limit") int limit);

    /**
     * 家庭友好型商家查询
     */
    List<BusinessListVO> findFamilyFriendlyBusinesses(@Param("limit") int limit);

    /**
     * 高端餐厅查询
     */
    List<BusinessListVO> findHighEndBusinesses(@Param("limit") int limit);

    /**
     * 根据类别查找商家
     */
    List<BusinessListVO> findByCategory(@Param("category") String category,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    /**
     * 附近商家查询
     */
    List<BusinessListVO> findNearbyBusinesses(@Param("lat") BigDecimal lat,
                                               @Param("lng") BigDecimal lng,
                                               @Param("radiusKm") double radiusKm,
                                               @Param("limit") int limit);
}
