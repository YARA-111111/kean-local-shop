package com.kean.yelp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kean.yelp.entity.Tip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 提示Mapper接口
 */
@Mapper
public interface TipMapper extends BaseMapper<Tip> {

    /**
     * 分页获取商家提示
     */
    List<Tip> findByBusinessId(@Param("businessId") String businessId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    /**
     * 统计商家提示总数
     */
    long countByBusinessId(@Param("businessId") String businessId);

    /**
     * 分页获取所有提示
     */
    List<Tip> findAllPaged(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计所有提示总数
     */
    long countAll();
}
