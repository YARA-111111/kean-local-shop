package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private String reviewId;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "商家ID不能为空")
    private String businessId;

    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "1.0", message = "评分必须大于等于1")
    @DecimalMax(value = "5.0", message = "评分必须小于等于5")
    private BigDecimal stars;

    private Integer useful;

    private Integer funny;

    private Integer cool;

    @NotBlank(message = "评论内容不能为空")
    private String text;

    private LocalDateTime reviewDate;

    // 用于显示的商家名称
    private String businessName;
}
