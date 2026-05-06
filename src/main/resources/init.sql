-- 校园失物招领平台 - Oracle数据库初始化脚本
-- 创建时间：2026-04-16

-- 1. 创建序列
-- 用户序列
CREATE SEQUENCE SEQ_USERS
    START WITH 1000
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 物品序列
CREATE SEQUENCE SEQ_ITEMS
    START WITH 1000
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 评论序列
CREATE SEQUENCE SEQ_COMMENTS
    START WITH 1000
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 公告序列
CREATE SEQUENCE SEQ_ANNOUNCEMENTS
    START WITH 1000
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 2. 创建用户表
CREATE TABLE users (
    user_id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(100) NOT NULL,
    nickname VARCHAR2(50) NOT NULL,
    contact VARCHAR2(100),
    avatar VARCHAR2(200),
    role NUMBER(1) DEFAULT 0, -- 0-普通用户, 1-管理员
    create_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    update_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    status NUMBER(1) DEFAULT 0 -- 0-正常, 1-禁用
);

select * from users;

COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.user_id IS '用户ID';
COMMENT ON COLUMN users.username IS '用户名（学号）';
COMMENT ON COLUMN users.password IS '密码';
COMMENT ON COLUMN users.nickname IS '昵称';
COMMENT ON COLUMN users.contact IS '联系方式';
COMMENT ON COLUMN users.avatar IS '头像URL';
COMMENT ON COLUMN users.role IS '用户角色：0-普通用户，1-管理员';
COMMENT ON COLUMN users.create_time IS '创建时间';
COMMENT ON COLUMN users.update_time IS '更新时间';
COMMENT ON COLUMN users.status IS '用户状态：0-正常，1-禁用';

-- 3. 创建物品表
CREATE TABLE items (
    item_id NUMBER(19) PRIMARY KEY,
    item_type NUMBER(1) NOT NULL, -- 1-证件, 2-钥匙, 3-电子设备, 4-衣物, 5-钱包, 6-其他
    publish_type NUMBER(1) NOT NULL, -- 0-丢失, 1-捡到
    title VARCHAR2(200) NOT NULL,
    description CLOB,
    features VARCHAR2(500),
    images VARCHAR2(1000), -- 多个图片用逗号分隔
    location VARCHAR2(200) NOT NULL,
    event_time TIMESTAMP NOT NULL,
    publisher_id NUMBER(19) NOT NULL,
    publisher_name VARCHAR2(50) NOT NULL,
    contact VARCHAR2(100),
    storage_location VARCHAR2(200),
    status NUMBER(1) DEFAULT 0, -- 0-未找回/未归还, 1-已找回/已归还
    view_count NUMBER(10) DEFAULT 0,
    collect_count NUMBER(10) DEFAULT 0,
    comment_count NUMBER(10) DEFAULT 0,
    create_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    update_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_items_publisher FOREIGN KEY (publisher_id) REFERENCES users(user_id)
);

COMMENT ON TABLE items IS '物品信息表';
COMMENT ON COLUMN items.item_id IS '物品ID';
COMMENT ON COLUMN items.item_type IS '物品类型：1-证件, 2-钥匙, 3-电子设备, 4-衣物, 5-钱包, 6-其他';
COMMENT ON COLUMN items.publish_type IS '发布类型：0-丢失, 1-捡到';
COMMENT ON COLUMN items.title IS '物品标题';
COMMENT ON COLUMN items.description IS '物品描述';
COMMENT ON COLUMN items.features IS '物品特征';
COMMENT ON COLUMN items.images IS '图片URL（多个图片用逗号分隔）';
COMMENT ON COLUMN items.location IS '丢失/捡到地点';
COMMENT ON COLUMN items.event_time IS '丢失/捡到时间';
COMMENT ON COLUMN items.publisher_id IS '发布人ID';
COMMENT ON COLUMN items.publisher_name IS '发布人昵称';
COMMENT ON COLUMN items.contact IS '联系方式';
COMMENT ON COLUMN items.storage_location IS '存放地点（仅招领信息有效）';
COMMENT ON COLUMN items.status IS '物品状态：0-未找回/未归还, 1-已找回/已归还';
COMMENT ON COLUMN items.view_count IS '浏览次数';
COMMENT ON COLUMN items.collect_count IS '收藏次数';
COMMENT ON COLUMN items.comment_count IS '评论次数';
COMMENT ON COLUMN items.create_time IS '创建时间';
COMMENT ON COLUMN items.update_time IS '更新时间';

-- 4. 创建评论表
CREATE TABLE comments (
    comment_id NUMBER(19) PRIMARY KEY,
    item_id NUMBER(19) NOT NULL,
    user_id NUMBER(19) NOT NULL,
    user_nickname VARCHAR2(50) NOT NULL,
    user_avatar VARCHAR2(200),
    content CLOB NOT NULL,
    parent_id NUMBER(19) DEFAULT 0,
    create_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    is_read NUMBER(1) DEFAULT 0, -- 0-未读, 1-已读
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items(item_id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

COMMENT ON TABLE comments IS '评论表';
COMMENT ON COLUMN comments.comment_id IS '评论ID';
COMMENT ON COLUMN comments.item_id IS '物品ID';
COMMENT ON COLUMN comments.user_id IS '用户ID';
COMMENT ON COLUMN comments.user_nickname IS '用户昵称';
COMMENT ON COLUMN comments.user_avatar IS '用户头像';
COMMENT ON COLUMN comments.content IS '评论内容';
COMMENT ON COLUMN comments.parent_id IS '父评论ID（0表示顶级评论）';
COMMENT ON COLUMN comments.create_time IS '创建时间';
COMMENT ON COLUMN comments.is_read IS '是否已读：0-未读，1-已读';

-- 5. 创建公告表
CREATE TABLE announcements (
    announcement_id NUMBER(19) PRIMARY KEY,
    title VARCHAR2(200) NOT NULL,
    content CLOB NOT NULL,
    publisher_id NUMBER(19) NOT NULL,
    publisher_name VARCHAR2(50) NOT NULL,
    is_top NUMBER(1) DEFAULT 0, -- 0-不置顶, 1-置顶
    create_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    update_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_announcements_publisher FOREIGN KEY (publisher_id) REFERENCES users(user_id)
);

COMMENT ON TABLE announcements IS '公告表';
COMMENT ON COLUMN announcements.announcement_id IS '公告ID';
COMMENT ON COLUMN announcements.title IS '公告标题';
COMMENT ON COLUMN announcements.content IS '公告内容';
COMMENT ON COLUMN announcements.publisher_id IS '发布人ID';
COMMENT ON COLUMN announcements.publisher_name IS '发布人名称';
COMMENT ON COLUMN announcements.is_top IS '是否置顶：0-不置顶，1-置顶';
COMMENT ON COLUMN announcements.create_time IS '创建时间';
COMMENT ON COLUMN announcements.update_time IS '更新时间';

-- 6. 创建索引
-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);

-- 物品表索引
CREATE INDEX idx_items_publish_type ON items(publish_type);
CREATE INDEX idx_items_item_type ON items(item_type);
CREATE INDEX idx_items_status ON items(status);
CREATE INDEX idx_items_publisher_id ON items(publisher_id);
CREATE INDEX idx_items_create_time ON items(create_time);

-- 评论表索引
CREATE INDEX idx_comments_item_id ON comments(item_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_create_time ON comments(create_time);

-- 公告表索引
CREATE INDEX idx_announcements_is_top ON announcements(is_top);
CREATE INDEX idx_announcements_create_time ON announcements(create_time);

-- 7. 插入默认管理员用户（密码：admin123，建议首次登录后修改）
INSERT INTO users (user_id, username, password, nickname, contact, role, status) 
VALUES (SEQ_USERS.NEXTVAL, 'admin', 'admin123', '系统管理员', 'admin@campus.edu', 1, 0);

-- 8. 插入示例公告
INSERT INTO announcements (announcement_id, title, content, publisher_id, publisher_name, is_top)
VALUES (SEQ_ANNOUNCEMENTS.NEXTVAL, '欢迎使用校园失物招领平台', 
        '本平台旨在帮助同学们找回丢失的物品，请大家文明使用，如实发布信息。', 
        1, '系统管理员', 1);

INSERT INTO announcements (announcement_id, title, content, publisher_id, publisher_name, is_top)
VALUES (SEQ_ANNOUNCEMENTS.NEXTVAL, '失物招领注意事项', 
        '1. 发布信息请如实描述物品特征\n2. 贵重物品请及时联系保卫处\n3. 请勿发布虚假信息\n4. 物品找回后请及时更新状态', 
        1, '系统管理员', 0);

COMMIT;
PRINT '数据库初始化完成！';