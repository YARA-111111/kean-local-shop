-- Structure only. Run in a new, dedicated MySQL 8 database.
CREATE TABLE `businesses`  (
  `business_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家唯一标识符',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '街道地址',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `state` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '州/省份',
  `postal_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `latitude` decimal(10, 7) NOT NULL COMMENT '纬度',
  `longitude` decimal(10, 7) NOT NULL COMMENT '经度',
  `stars` decimal(2, 1) NULL DEFAULT NULL COMMENT '平均评分 (1.0-5.0)',
  `review_count` int UNSIGNED NULL DEFAULT 0 COMMENT '评论总数',
  `is_open` tinyint(1) NULL DEFAULT 1 COMMENT '是否营业中 (0=关闭, 1=营业)',
  `categories` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商家类别（逗号分隔）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`business_id`) USING BTREE,
  INDEX `idx_location`(`latitude` ASC, `longitude` ASC) USING BTREE,
  INDEX `idx_city_state`(`city` ASC, `state` ASC) USING BTREE,
  INDEX `idx_stars`(`stars` ASC) USING BTREE,
  INDEX `idx_is_open`(`is_open` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  FULLTEXT INDEX `ft_name_categories`(`name`, `categories`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Yelp商家信息主表' ROW_FORMAT = Dynamic;

CREATE TABLE `users`  (
  `user_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户唯一标识符',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

CREATE TABLE `business_attributes`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `business_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家ID',
  `attribute_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '属性名称',
  `attribute_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_business_attribute`(`business_id` ASC, `attribute_name` ASC) USING BTREE,
  INDEX `idx_attribute_name`(`attribute_name` ASC) USING BTREE,
  INDEX `idx_attribute_value`(`attribute_value`(100) ASC) USING BTREE,
  CONSTRAINT `fk_attr_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`business_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT=1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家属性表' ROW_FORMAT = Dynamic;

CREATE TABLE `business_categories`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `business_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家ID',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类别名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_business_category`(`business_id` ASC, `category` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  CONSTRAINT `fk_cat_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`business_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT=1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家类别表（规范化）' ROW_FORMAT = Dynamic;

CREATE TABLE `business_hours`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `business_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家ID',
  `day_of_week` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '星期几',
  `open_time` time NULL DEFAULT NULL COMMENT '开门时间',
  `close_time` time NULL DEFAULT NULL COMMENT '关门时间',
  `is_closed` tinyint(1) NULL DEFAULT 0 COMMENT '当天是否休息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_business_day`(`business_id` ASC, `day_of_week` ASC) USING BTREE,
  INDEX `idx_day_of_week`(`day_of_week` ASC) USING BTREE,
  CONSTRAINT `fk_hours_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`business_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT=1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商家营业时间表' ROW_FORMAT = Dynamic;

CREATE TABLE `reviews`  (
  `review_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论唯一标识符',
  `user_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `business_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家ID',
  `stars` decimal(2, 1) NOT NULL COMMENT '评分 (1.0-5.0)',
  `useful` int UNSIGNED NULL DEFAULT 0 COMMENT '有用票数',
  `funny` int UNSIGNED NULL DEFAULT 0 COMMENT '有趣票数',
  `cool` int UNSIGNED NULL DEFAULT 0 COMMENT '酷票数',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评论内容',
  `review_date` datetime NOT NULL COMMENT '评论日期时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`review_id`) USING BTREE,
  INDEX `idx_business_id`(`business_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_stars`(`stars` ASC) USING BTREE,
  INDEX `idx_review_date`(`review_date` ASC) USING BTREE,
  INDEX `idx_useful`(`useful` ASC) USING BTREE,
  FULLTEXT INDEX `ft_review_text`(`text`),
  CONSTRAINT `fk_review_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`business_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户评论表' ROW_FORMAT = Dynamic;

CREATE TABLE `tips`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `business_id` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商家ID',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '提示内容',
  `tip_date` datetime NOT NULL COMMENT '提示日期时间',
  `compliment_count` int UNSIGNED NULL DEFAULT 0 COMMENT '赞美数量',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_id`(`business_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_tip_date`(`tip_date` ASC) USING BTREE,
  INDEX `idx_compliment_count`(`compliment_count` ASC) USING BTREE,
  FULLTEXT INDEX `ft_tip_text`(`text`),
  CONSTRAINT `fk_tip_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`business_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tip_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT=1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户提示表' ROW_FORMAT = Dynamic;