package com.kean.yelp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tips")
public class Tip {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userId;

    private String businessId;

    private String text;

    private LocalDateTime tipDate;

    private Integer complimentCount;

    private LocalDateTime createdAt;
}
