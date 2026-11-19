#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Yelp数据导入脚本
将Yelp的JSON数据文件导入到MySQL 8.0数据库

使用方法:
    pip install mysql-connector-python
    python scripts/import_data.py --data-dir data/demo
"""

import json
import mysql.connector
from mysql.connector import Error
from datetime import datetime
import os

# 数据库连接配置
from data_io import read_records
import argparse
DB_CONFIG = {}
BUSINESS_FILE = REVIEW_FILE = TIP_FILE = None

class YelpDataImporter:
    """Yelp数据导入器"""
    
    def __init__(self, db_config):
        self.db_config = db_config
        self.connection = None
        self.cursor = None
        self.users_cache = set()  # 缓存已插入的用户ID
        self.businesses_cache = set()  # 缓存已插入的商家ID
        
    def connect(self):
        """建立数据库连接"""
        try:
            self.connection = mysql.connector.connect(**self.db_config)
            self.cursor = self.connection.cursor()
            print("✓ 成功连接到MySQL数据库")
            return True
        except Error as e:
            print(f"✗ 数据库连接失败: {e}")
            return False
    
    def disconnect(self):
        """关闭数据库连接"""
        if self.cursor:
            self.cursor.close()
        if self.connection and self.connection.is_connected():
            self.connection.close()
            print("✓ 数据库连接已关闭")
    
    def load_json_file(self, file_path):
        return list(read_records(file_path))

    def import_businesses(self, businesses):
        """导入商家数据"""
        print(f"\n正在导入商家数据 ({len(businesses)} 条)...")
        
        business_sql = """
            INSERT INTO businesses 
            (business_id, name, address, city, state, postal_code, 
             latitude, longitude, stars, review_count, is_open, categories)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
            name = VALUES(name), address = VALUES(address)
        """
        
        attr_sql = """
            INSERT INTO business_attributes (business_id, attribute_name, attribute_value)
            VALUES (%s, %s, %s)
            ON DUPLICATE KEY UPDATE attribute_value = VALUES(attribute_value)
        """
        
        hours_sql = """
            INSERT INTO business_hours (business_id, day_of_week, open_time, close_time, is_closed)
            VALUES (%s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE open_time = VALUES(open_time), close_time = VALUES(close_time)
        """
        
        category_sql = """
            INSERT INTO business_categories (business_id, category)
            VALUES (%s, %s)
            ON DUPLICATE KEY UPDATE category = VALUES(category)
        """
        
        success_count = 0
        
        for i, business in enumerate(businesses):
            try:
                business_id = business.get('business_id')
                
                # 插入商家基本信息
                self.cursor.execute(business_sql, (
                    business_id,
                    business.get('name', '')[:255],
                    business.get('address', '')[:255] if business.get('address') else None,
                    business.get('city', '')[:100] if business.get('city') else None,
                    business.get('state', '')[:10] if business.get('state') else None,
                    business.get('postal_code', '')[:20] if business.get('postal_code') else None,
                    business.get('latitude'),
                    business.get('longitude'),
                    business.get('stars'),
                    business.get('review_count', 0),
                    business.get('is_open', 1),
                    business.get('categories', '')
                ))
                
                self.businesses_cache.add(business_id)
                
                # 插入商家属性
                attributes = business.get('attributes')
                if attributes and isinstance(attributes, dict):
                    for attr_name, attr_value in attributes.items():
                        if attr_value is not None:
                            self.cursor.execute(attr_sql, (
                                business_id,
                                attr_name[:100],
                                str(attr_value)[:500]
                            ))
                
                # 插入营业时间
                hours = business.get('hours')
                if hours and isinstance(hours, dict):
                    for day, time_range in hours.items():
                        if time_range:
                            open_time, close_time, is_closed = self.parse_hours(time_range)
                            self.cursor.execute(hours_sql, (
                                business_id,
                                day,
                                open_time,
                                close_time,
                                is_closed
                            ))
                
                # 插入商家类别
                categories = business.get('categories', '')
                if categories:
                    for category in categories.split(','):
                        category = category.strip()
                        if category:
                            self.cursor.execute(category_sql, (
                                business_id,
                                category[:100]
                            ))
                
                success_count += 1
                
                # 每100条提交一次
                if (i + 1) % 100 == 0:
                    self.connection.commit()
                    print(f"  已处理: {i + 1}/{len(businesses)}")
                    
            except Error as e:
                print(f"  ✗ 导入商家 {business.get('business_id')} 失败: {e}")
                raise
        
        self.connection.commit()
        print(f"✓ 商家导入完成: {success_count}/{len(businesses)} 成功")
    
    def parse_hours(self, time_range):
        """解析营业时间字符串"""
        try:
            if not time_range or time_range == '0:0-0:0':
                return None, None, 0  # 24小时营业
            
            parts = time_range.split('-')
            if len(parts) != 2:
                return None, None, 0
            
            open_time = self.format_time(parts[0])
            close_time = self.format_time(parts[1])
            
            return open_time, close_time, 0
        except Exception:
            return None, None, 0
    
    def format_time(self, time_str):
        """格式化时间字符串为 HH:MM:SS"""
        try:
            parts = time_str.strip().split(':')
            hour = int(parts[0])
            minute = int(parts[1]) if len(parts) > 1 else 0
            return f"{hour:02d}:{minute:02d}:00"
        except Exception:
            return None
    
    def ensure_user_exists(self, user_id):
        """确保用户存在于数据库中"""
        if user_id in self.users_cache:
            return True
        
        try:
            self.cursor.execute(
                "INSERT IGNORE INTO users (user_id) VALUES (%s)",
                (user_id,)
            )
            self.users_cache.add(user_id)
            return True
        except Error:
            return False
    
    def ensure_business_exists(self, business_id):
        """确保商家存在于数据库中（创建占位记录）"""
        if business_id in self.businesses_cache:
            return True
        
        try:
            self.cursor.execute(
                """INSERT IGNORE INTO businesses 
                   (business_id, name, latitude, longitude, stars, review_count, is_open)
                   VALUES (%s, %s, %s, %s, %s, %s, %s)""",
                (business_id, f'Unknown Business ({business_id[:8]}...)', 0.0, 0.0, 0.0, 0, 1)
            )
            self.businesses_cache.add(business_id)
            return True
        except Error as e:
            print(f"  ✗ 创建商家占位记录失败: {e}")
            return False
    
    def import_reviews(self, reviews):
        """导入评论数据"""
        print(f"\n正在导入评论数据 ({len(reviews)} 条)...")
        
        review_sql = """
            INSERT INTO reviews 
            (review_id, user_id, business_id, stars, useful, funny, cool, text, review_date)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
            stars = VALUES(stars), text = VALUES(text)
        """
        
        success_count = 0
        skipped_count = 0
        
        for i, review in enumerate(reviews):
            try:
                business_id = review.get('business_id')
                user_id = review.get('user_id')
                
                # 确保商家存在（如不存在则创建占位记录）
                self.ensure_business_exists(business_id)
                
                # 确保用户存在
                self.ensure_user_exists(user_id)
                
                # 解析日期
                date_str = review.get('date', '')
                review_date = datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S') if date_str else datetime.now()
                
                self.cursor.execute(review_sql, (
                    review.get('review_id'),
                    user_id,
                    business_id,
                    review.get('stars', 0),
                    review.get('useful', 0),
                    review.get('funny', 0),
                    review.get('cool', 0),
                    review.get('text', ''),
                    review_date
                ))
                
                success_count += 1
                
                # 每100条提交一次
                if (i + 1) % 100 == 0:
                    self.connection.commit()
                    print(f"  已处理: {i + 1}/{len(reviews)}")
                    
            except Error as e:
                print(f"  ✗ 导入评论 {review.get('review_id')} 失败: {e}")
                raise
        
        self.connection.commit()
        print(f"✓ 评论导入完成: {success_count}/{len(reviews)} 成功")
    
    def import_tips(self, tips):
        """导入提示数据"""
        print(f"\n正在导入提示数据 ({len(tips)} 条)...")
        
        tip_sql = """
            INSERT INTO tips 
            (user_id, business_id, text, tip_date, compliment_count)
            VALUES (%s, %s, %s, %s, %s)
        """
        
        success_count = 0
        skipped_count = 0
        
        for i, tip in enumerate(tips):
            try:
                business_id = tip.get('business_id')
                user_id = tip.get('user_id')
                
                # 确保商家存在（如不存在则创建占位记录）
                self.ensure_business_exists(business_id)
                
                # 确保用户存在
                self.ensure_user_exists(user_id)
                
                # 解析日期
                date_str = tip.get('date', '')
                tip_date = datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S') if date_str else datetime.now()
                
                self.cursor.execute(tip_sql, (
                    user_id,
                    business_id,
                    tip.get('text', ''),
                    tip_date,
                    tip.get('compliment_count', 0)
                ))
                
                success_count += 1
                
                # 每100条提交一次
                if (i + 1) % 100 == 0:
                    self.connection.commit()
                    print(f"  已处理: {i + 1}/{len(tips)}")
                    
            except Error as e:
                print(f"  ✗ 导入提示失败: {e}")
                raise
        
        self.connection.commit()
        print(f"✓ 提示导入完成: {success_count}/{len(tips)} 成功")
    
    def show_statistics(self):
        """显示导入统计"""
        print("\n" + "=" * 50)
        print("数据导入统计")
        print("=" * 50)
        
        tables = ['businesses', 'business_attributes', 'business_hours', 
                  'business_categories', 'users', 'reviews', 'tips']
        
        for table in tables:
            try:
                self.cursor.execute(f"SELECT COUNT(*) FROM {table}")
                count = self.cursor.fetchone()[0]
                print(f"  {table}: {count} 条记录")
            except Error as e:
                print(f"  {table}: 查询失败 - {e}")
        
        print("=" * 50)
    
    def run(self):
        """运行数据导入"""
        print("=" * 50)
        print("Yelp 数据导入工具")
        print("=" * 50)
        
        # 连接数据库
        if not self.connect():
            return False
        
        try:
            # 清空现有数据
            # Never clear existing tables.
            
            # 加载JSON数据
            businesses = self.load_json_file(BUSINESS_FILE)
            reviews = self.load_json_file(REVIEW_FILE)
            tips = self.load_json_file(TIP_FILE)
            
            if not businesses:
                print("✗ 没有商家数据，退出导入")
                return False
            
            # 导入数据
            self.import_businesses(businesses)
            self.import_reviews(reviews)
            self.import_tips(tips)
            
            # 显示统计
            self.show_statistics()
            
            print("\n✓ 数据导入完成!")
            return True
            
        except Exception as e:
            print(f"\n✗ 导入过程中发生错误: {e}")
            self.connection.rollback()
            return False
            
        finally:
            self.disconnect()


def main():
    global BUSINESS_FILE, REVIEW_FILE, TIP_FILE
    p=argparse.ArgumentParser(description="Import into a dedicated database; never clears tables")
    p.add_argument("--data-dir",required=True)
    a=p.parse_args()
    BUSINESS_FILE=os.path.join(a.data_dir,"businesses.json")
    REVIEW_FILE=os.path.join(a.data_dir,"reviews.json")
    TIP_FILE=os.path.join(a.data_dir,"tips.json")
    for file in (BUSINESS_FILE, REVIEW_FILE, TIP_FILE):
        list(read_records(file))  # Validate all input before touching the database.
    config=dict(host=os.getenv("DB_HOST","localhost"),port=int(os.getenv("DB_PORT","3306")),
        user=os.environ["DB_USER"],password=os.environ["DB_PASSWORD"],
        database=os.getenv("DB_NAME","yelp_recommendation"),charset="utf8mb4",autocommit=False)
    if not YelpDataImporter(config).run(): raise SystemExit(1)
if __name__=="__main__": main()
