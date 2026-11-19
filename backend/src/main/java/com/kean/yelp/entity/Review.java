package com.kean.yelp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("reviews")
public class Review {

    @TableId(value = "review_id", type = IdType.INPUT)
    private String reviewId;

    private String userId;

    private String businessId;

    private BigDecimal stars;

    private Integer useful;

    private Integer funny;

    private Integer cool;

    private String text;

    private LocalDateTime reviewDate;

    private LocalDateTime createdAt;
}
