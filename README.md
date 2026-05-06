# 校园失物招领平台

## 项目简介

一个功能完整的校园失物招领平台，采用Java Spring Boot + Oracle + MyBatis企业级技术栈，专门解决校园里丢饭卡、钥匙、耳机、书本、证件等高频问题。平台支持用户注册登录、物品发布管理、图片上传、智能搜索、动态统计等核心功能，为校园失物招领提供一站式解决方案。

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2.0
- **数据库**: Oracle 21c
- **ORM框架**: MyBatis 3.5.13 + PageHelper 5.3.3（分页插件）
- **前端**: 
  - 模板引擎: Thymeleaf 3.1.2
  - UI框架: Bootstrap 5.3.0 + 响应式设计
  - 图标: Font Awesome 6.4.0
  - 交互技术: JavaScript + AJAX 异步请求
  - 图片上传: 原生JS实现拖拽上传
- **构建工具**: Maven

## 🚀 项目状态概览

| 模块 | 状态 | 完成度 | 备注 |
|------|------|--------|------|
| 用户管理 | ✅ 已完成 | 100% | 注册、登录、个人中心、密码管理 |
| 物品管理 | ✅ 已完成 | 100% | 发布、编辑、删除、状态更新 |
| 图片上传 | ✅ 已完成 | 100% | 拖拽上传、预览、多文件支持 |
| 搜索浏览 | ✅ 已完成 | 100% | 分页、筛选、我的物品管理 |
| 首页统计 | ✅ 已完成 | 100% | 动态数据、找回率、平均时间 |
| 评论互动 | 🔄 待开发 | 0% | 下一阶段重点 |
| 管理员后台 | 🔄 待开发 | 0% | 需要权限管理模块 |
| 移动端优化 | 📋 计划中 | 0% | 响应式已基本完成 |

**总体完成度**: 85% (核心功能已全部实现)

## 功能模块

### ✅ 已完成
1. **用户管理模块**
   - 用户注册（学号验证、密码加密）
   - 用户登录（Session管理）
   - 个人信息管理（昵称、联系方式、头像）
   - 密码修改
   - 动态导航栏（登录状态感知）

2. **物品管理模块**
   - 物品发布（丢失/捡到两种类型）
   - 物品编辑与删除（发布者权限控制）
   - 物品状态更新（待处理/已找回/已归还）
   - 物品详情查看（浏览次数统计）

3. **图片上传模块**
   - 支持拖拽上传
   - 多图片上传（最多5张，每张≤10MB）
   - 图片预览与删除
   - 图片格式验证（JPG/PNG/GIF/WEBP）
   - 静态资源映射

4. **搜索与浏览模块**
   - 物品列表分页展示
   - 高级搜索（关键词、类型、状态）
   - 我的物品管理
   - 收藏功能

5. **首页功能模块**
   - 动态统计数据（实时从数据库获取）
     - 已发布信息总数
     - 成功找回数量
     - 找回率计算
     - 平均找回时间
   - 快速发布按钮（"我丢了东西"、"我捡到东西"）
   - 响应式界面设计

### ⏳ 待完成/优化
1. **评论互动模块**
2. **管理员后台模块**
3. **物品匹配提醒功能**
4. **校园地图集成**
5. **数据统计报表**
6. **移动端优化**

## 数据库设计

### 表结构
1. **users** (用户表)
   - user_id, username, password, nickname, contact, avatar, role, status

2. **items** (物品表)
   - item_id, item_type, publish_type, title, description, features, images, location, event_time, publisher_id, status

3. **comments** (评论表)
   - comment_id, item_id, user_id, content, parent_id, create_time

4. **announcements** (公告表)
   - announcement_id, title, content, publisher_id, is_top, create_time

## 快速开始

### 环境要求
1. JDK 17 或更高版本
2. Oracle 数据库 (11g/12c/19c/21c)
3. Maven 3.6 或更高版本
4. IDE (推荐 IntelliJ IDEA 或 Eclipse)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd campus-lost-found-oracle
   ```

2. **配置Oracle数据库**
   - 安装Oracle数据库并创建实例
   - 执行 `src/main/resources/init.sql` 脚本初始化数据库
   - 修改 `application.properties` 中的数据库连接配置

3. **安装Oracle JDBC驱动**
   ```bash
   # 下载ojdbc8.jar (Oracle官网)
   mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle.database.jdbc -DartifactId=ojdbc8 -Dversion=21.11.0.0 -Dpackaging=jar
   ```

4. **配置应用程序**
   - 主要配置在 `src/main/resources/application.yml`:
     ```yaml
     server:
       port: 8081
       servlet:
         context-path: /
     
     spring:
       # 数据源配置
       datasource:
         url: jdbc:oracle:thin:@localhost:1521:XE
         username: SYSTEM
         password: your_password
         driver-class-name: oracle.jdbc.OracleDriver
       
       # 文件上传配置
       servlet:
         multipart:
           max-file-size: 10MB
           max-request-size: 50MB
           enabled: true
       
       # 静态资源映射
       web:
         resources:
           static-locations: classpath:/static/, file:${upload.path}
     
     # 自定义配置
     upload:
       path: D:/upload/campus-lost-found
       max-files: 5
       allowed-extensions: .jpg,.jpeg,.png,.gif,.webp
     ```

5. **构建并运行项目**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```
   或直接在IDE中运行 `LostFoundApplication.java`

6. **访问应用**
   - 首页: http://localhost:8081
   - 注册账号后即可使用所有功能
   - 测试数据: 注册账号后发布物品，查看动态统计变化

## 项目结构

```
campus-lost-found-oracle/
├── src/main/java/com/campus/lostfound/
│   ├── LostFoundApplication.java          # 启动类
│   ├── controller/                        # 控制器层
│   │   ├── HomeController.java            # 首页控制器（含动态统计）
│   │   ├── UserController.java            # 用户管理控制器
│   │   └── ItemController.java            # 物品管理控制器
│   ├── entity/                            # 实体类
│   │   ├── User.java                      # 用户实体
│   │   ├── Item.java                      # 物品实体
│   │   ├── Comment.java                   # 评论实体
│   │   └── Announcement.java              # 公告实体
│   ├── service/                           # 业务逻辑层
│   │   ├── UserService.java               # 用户服务
│   │   ├── ItemService.java               # 物品服务
│   │   └── ImageService.java              # 图片上传服务
│   ├── mapper/                            # MyBatis Mapper接口
│   │   ├── UserMapper.java                # 用户Mapper（含统计方法）
│   │   └── ItemMapper.java                # 物品Mapper（含统计方法）
│   └── utils/
│       └── MD5Util.java                   # 密码加密工具
├── src/main/resources/
│   ├── static/                            # 静态资源（CSS、JS、图片）
│   ├── templates/                         # 模板文件
│   │   ├── index.html                     # 首页模板（动态统计）
│   │   ├── about.html                     # 关于页面
│   │   ├── user/                          # 用户相关页面
│   │   │   ├── login.html                 # 登录页面
│   │   │   ├── register.html              # 注册页面
│   │   │   ├── profile.html               # 个人资料
│   │   │   └── change-password.html       # 修改密码
│   │   └── items/                         # 物品相关页面
│   │       ├── list.html                  # 物品列表
│   │       ├── detail.html                # 物品详情
│   │       ├── publish.html               # 发布页面（图片上传）
│   │       ├── edit.html                  # 编辑页面
│   │       ├── my.html                    # 我的物品
│   │       └── search.html                # 搜索页面
│   ├── application.properties             # 应用配置
│   ├── application.yml                    # 详细配置（文件上传、静态资源）
│   └── init.sql                           # 数据库初始化脚本
├── pom.xml                                # Maven配置（依赖管理）
└── README.md                              # 项目说明文档
```

## 开发计划

### ✅ 第一阶段（基础架构） - 已完成
- [x] 项目架构搭建（Spring Boot + Oracle + MyBatis）
- [x] 数据库设计与初始化
- [x] 实体类定义（User、Item、Comment、Announcement）
- [x] MyBatis Mapper接口定义
- [x] 首页界面设计（Bootstrap 5响应式）
- [x] 应用配置（application.yml/properties）

### ✅ 第二阶段（用户与物品管理） - 已完成
- [x] 用户注册登录功能（密码加密、Session管理）
- [x] 个人中心与密码修改
- [x] 物品发布功能（丢失/捡到两种类型）
- [x] 图片上传功能（拖拽、预览、多文件）
- [x] 物品列表展示与分页（PageHelper）
- [x] 物品搜索与筛选（关键词、类型、状态）
- [x] 物品详情页面（浏览次数统计）
- [x] 我的物品管理
- [x] 物品状态更新（待处理/已找回/已归还）

### ✅ 第三阶段（首页与统计） - 已完成
- [x] 动态导航栏（登录状态感知）
- [x] 首页快速发布按钮（"我丢了东西"、"我捡到东西"）
- [x] 动态统计数据（实时从数据库获取）
- [x] 找回率与平均找回时间计算
- [x] 前端数字格式化与优化

### 🔄 第四阶段（高级功能） - 开发中
- [ ] 评论互动功能
- [ ] 收藏功能优化
- [ ] 物品匹配提醒
- [ ] 管理员后台模块
- [ ] 数据统计报表
- [ ] 校园地图集成

### 📱 第五阶段（优化与扩展）
- [ ] 移动端优化
- [ ] 缓存策略优化
- [ ] 安全增强（验证码、权限控制）
- [ ] API接口设计
- [ ] 微服务架构改造

## 技术特点

### 🏗️ 架构设计
1. **模块化分层架构**：清晰的Controller-Service-Mapper分层
2. **企业级技术栈**：Spring Boot + Oracle + MyBatis + PageHelper
3. **前后端分离式设计**：Thymeleaf模板引擎 + Bootstrap 5 + AJAX

### 📊 数据处理
1. **实时统计计算**：首页统计数据从数据库实时获取，无静态假数据
2. **分页优化**：PageHelper插件实现高效数据库分页
3. **搜索功能**：支持多维度组合搜索，关键词高亮显示
4. **数据验证**：前后端双重验证，确保数据完整性

### 🛡️ 安全与性能
1. **密码安全**：MD5加密存储，传输过程加密
2. **Session管理**：基于HttpSession的用户状态管理
3. **权限控制**：发布者只能编辑/删除自己的物品
4. **文件安全**：图片格式、大小验证，防止恶意上传

### 🖼️ 用户体验
1. **图片上传**：拖拽上传、实时预览、多文件支持
2. **动态交互**：AJAX无刷新操作，提升用户体验
3. **响应式设计**：Bootstrap 5实现完美多设备适配
4. **状态感知**：导航栏根据登录状态动态变化

### 🔧 开发维护
1. **异常处理**：统一异常处理机制，系统稳定性高
2. **配置灵活**：application.yml详细配置，易于部署
3. **代码规范**：遵循Java开发规范，注释完整
4. **扩展性强**：便于添加新功能模块

## ⚠️ 重要注意事项

### 数据库相关
1. **Oracle JDBC驱动**：需要手动安装到本地Maven仓库
2. **数据库初始化**：首次运行前需执行 `init.sql` 脚本
3. **连接配置**：确保 `application.yml` 中的数据库连接信息正确

### 文件上传配置
1. **上传目录**：`D:/upload/campus-lost-found` 目录会自动创建，确保有写入权限
2. **文件大小限制**：单文件最大10MB，总请求最大50MB
3. **支持格式**：JPG、PNG、GIF、WEBP
4. **静态资源访问**：上传的图片可通过 `/upload/**` 路径访问

### 统计功能说明
1. **实时统计**：首页统计数据从数据库实时计算
2. **找回率计算**：基于已找回物品数与总物品数
3. **平均找回时间**：仅计算已找回物品的事件时间差
4. **数据准确性**：需要至少一个已找回物品才能计算平均时间

### 安全与部署
1. **生产环境**：务必修改默认密码和敏感配置
2. **端口配置**：默认端口8081，可在 `application.yml` 修改
3. **权限控制**：发布者只能操作自己的物品
4. **Session管理**：用户登录状态基于HttpSession

## 许可证

本项目仅供学习交流使用，转载请注明出处。

## 联系方式

如有问题或建议，请联系项目维护者。

---

**最后更新**: 2026-04-18

**当前版本**: 1.1.0 (功能完整版)

**更新内容**:
- ✅ 完成用户管理模块（注册、登录、个人中心）
- ✅ 完成物品管理模块（发布、编辑、搜索、状态管理）
- ✅ 实现图片上传功能（拖拽、预览、多文件）
- ✅ 新增首页动态统计数据（实时从数据库获取）
- ✅ 优化前端交互体验（AJAX、响应式设计）
- ✅ 完善项目文档与配置说明