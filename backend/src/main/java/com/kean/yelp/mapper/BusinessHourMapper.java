package com.kean.yelp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kean.yelp.entity.BusinessHour;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家营业时间Mapper接口
 */
@Mapper
public interface BusinessHourMapper extends BaseMapper<BusinessHour> {

    /**
     * 获取商家的营业时间
     */
    List<BusinessHour> findByBusinessId(@Param("businessId") String businessId);
}
