package com.kean.yelp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("business_categories")
public class BusinessCategory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String businessId;

    private String categoryName;
}
