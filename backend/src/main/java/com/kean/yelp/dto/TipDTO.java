package com.kean.yelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipDTO {

    private Long id;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "商家ID不能为空")
    private String businessId;

    @NotBlank(message = "提示内容不能为空")
    private String text;

    private LocalDateTime tipDate;

    private Integer complimentCount;

    // 用于显示的商家名称
    private String businessName;
}
