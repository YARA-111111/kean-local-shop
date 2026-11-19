package com.kean.yelp.controller;

import com.kean.yelp.dto.ApiResponse;
import com.kean.yelp.dto.PageVO;
import com.kean.yelp.entity.Tip;
import com.kean.yelp.service.TipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 提示控制器
 */
@RestController
@RequestMapping("/api/tips")
public class TipController {

    @Autowired
    private TipService tipService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("服务运行中", "OK"));
    }

    /**
     * 分页获取所有提示
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageVO<Tip>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageVO<Tip> result = tipService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取商家提示列表
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<PageVO<Tip>>> getByBusinessId(
            @PathVariable String businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageVO<Tip> result = tipService.getByBusinessId(businessId, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取单个提示
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tip>> getById(@PathVariable Long id) {
        Tip tip = tipService.getById(id);
        if (tip == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "提示不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(tip));
    }
}
