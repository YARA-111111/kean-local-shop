package com.kean.yelp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("business_hours")
public class BusinessHour {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String businessId;

    private String dayOfWeek;

    private LocalTime openTime;

    private LocalTime closeTime;

    @TableField("is_closed")
    private Boolean isClosed;
}
