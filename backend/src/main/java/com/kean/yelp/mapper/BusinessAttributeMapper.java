package com.kean.yelp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kean.yelp.entity.BusinessAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家属性Mapper接口
 */
@Mapper
public interface BusinessAttributeMapper extends BaseMapper<BusinessAttribute> {

    /**
     * 获取商家的所有属性
     */
    List<BusinessAttribute> findByBusinessId(@Param("businessId") String businessId);

    /**
     * 获取商家的指定属性值
     */
    String getAttributeValue(@Param("businessId") String businessId,
                             @Param("attributeName") String attributeName);

    /**
     * 获取商家价格区间
     */
    Integer getPriceRange(@Param("businessId") String businessId);
}
