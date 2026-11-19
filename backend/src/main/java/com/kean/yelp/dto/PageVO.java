package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;  // 当前页码 (从0开始)
    private boolean first;
    private boolean last;

    public static <T> PageVO<T> of(List<T> content, long total, int page, int size) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setContent(content);
        pageVO.setTotalElements(total);
        pageVO.setSize(size);
        pageVO.setNumber(page);
        pageVO.setTotalPages((int) Math.ceil((double) total / size));
        pageVO.setFirst(page == 0);
        pageVO.setLast(page >= pageVO.getTotalPages() - 1);
        return pageVO;
    }
}
