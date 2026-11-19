package com.kean.yelp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("businesses")
public class Business {

    @TableId(value = "business_id", type = IdType.INPUT)
    private String businessId;

    private String name;

    private String address;

    private String city;

    private String state;

    private String postalCode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal stars;

    private Integer reviewCount;

    @TableField("is_open")
    private Boolean isOpen;

    private String categories;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
