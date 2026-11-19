package com.kean.yelp.service;

import com.kean.yelp.dto.PageVO;
import com.kean.yelp.entity.Tip;
import com.kean.yelp.mapper.TipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提示服务层
 */
@Service
public class TipService {

    @Autowired
    private TipMapper tipMapper;

    /**
     * 分页获取商家提示
     */
    public PageVO<Tip> getByBusinessId(String businessId, int page, int size) {
        List<Tip> tips = tipMapper.findByBusinessId(businessId, page * size, size);
        long total = tipMapper.countByBusinessId(businessId);
        return PageVO.of(tips, total, page, size);
    }

    /**
     * 获取单个提示
     */
    public Tip getById(Long tipId) {
        return tipMapper.selectById(tipId);
    }

    /**
     * 分页获取所有提示
     */
    public PageVO<Tip> getAll(int page, int size) {
        List<Tip> tips = tipMapper.findAllPaged(page * size, size);
        long total = tipMapper.countAll();
        return PageVO.of(tips, total, page, size);
    }
}
