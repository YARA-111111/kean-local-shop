# Kean Local Shop Finder

Vue 3、Spring Boot、MySQL 8 课程项目：基于评分、价格和属性规则筛选店铺，包含分页、详情和评分趋势；不宣称机器学习个性化推荐。

肖雅薇负责 Yelp 数据筛选、核验与分类，协助功能调试和数据展示。前后端整体为团队成果，见 CONTRIBUTORS.md。

## 环境与启动（PowerShell）
需要 JDK 17、Maven 3.9、Node.js 22、Python 3.10+、MySQL 8。使用新建的专用数据库，不连接重要业务数据库。

1. 在 MySQL 客户端创建 `yelp_recommendation` 数据库及专用账号，在该数据库运行 `database/schema.sql`。
2. 在启动后端及导入数据的终端设置 `$env:DB_USER="你的专用账号"` 和 `$env:DB_PASSWORD="你的密码"`。不要将密码提交到 Git。默认本机 3306；后端可用 DB_URL 覆盖连接地址，导入器使用 DB_HOST、DB_PORT、DB_NAME。
3. `python -m pip install -r scripts/requirements.txt`
4. `python scripts/import_data.py --data-dir data/demo`
5. 后端：`cd backend`，执行 `mvn clean package`，再 `mvn spring-boot:run`。
6. 新终端进入 frontend，执行 `npm ci`、`npm run dev`，打开终端显示的地址。

演示店铺完全虚构；reviews/tips 为空，评论列表及趋势可以为空，这是预期行为。导入器不清空表，失败返回非零；原有分批提交行为保留，失败前可能已写入部分记录，建议只在专用演示库使用。

## 数据处理
`python scripts/filter_data.py INPUT OUTPUT --bounds 40.665 40.690 -74.255 -74.215`

也可使用 `--business-ids ids.txt` 按商户ID筛选评论。输入兼容 JSON 数组与逐行 JSON，输出为逐行 JSON；拒绝覆盖已存在输出。数组格式需要读入内存，处理大型原始数据优先使用逐行 JSON。

导入目录使用 businesses.json、reviews.json、tips.json 三个文件名，均支持数组或逐行 JSON。自行取得且有权使用的数据放在被 Git 忽略的 data/private/。本仓库不包含真实 Yelp 数据、评论、用户记录或原数据库导出；Yelp 数据受其官方协议约束：https://www.yelp.com/dataset 。

## 发布范围
这是本地教学演示，尚无登录鉴权，不应直接部署为公网生产服务。原报告包含未核对的结论，因此不随发布版本提供。许可证与协作者名单需团队确认后决定。
