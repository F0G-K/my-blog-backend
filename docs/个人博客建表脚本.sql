-- =============================================================
-- 个人博客 · 数据库建表脚本
-- 适配:MySQL 8.4 LTS / InnoDB / utf8mb4
-- 设计约定:
--   1. 逻辑外键:不建物理外键(遵循阿里开发手册),约束在应用层保证,
--      但在关联列上建索引以保证 JOIN 性能。
--   2. 状态字段用 TINYINT(省空间、有索引价值),含义见列注释;
--      接口文档里的字符串状态(如 "published")由后端做映射。
--   3. 时间字段统一 created_at / updated_at,与接口返回的 createdAt 等驼峰对应
--      (MyBatis-Plus 开启 mapUnderscoreToCamelCase 自动转换)。
--   4. 主键统一 BIGINT UNSIGNED 自增。
-- =============================================================

CREATE DATABASE IF NOT EXISTS blog
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE blog;

-- -------------------------------------------------------------
-- 1. 管理员(系统唯一)
-- -------------------------------------------------------------
CREATE TABLE admin (
  id            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键',
  username      VARCHAR(50)      NOT NULL                COMMENT '登录账号',
  password_hash VARCHAR(100)     NOT NULL                COMMENT '密码(BCrypt 哈希)',
  created_at    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员';

-- -------------------------------------------------------------
-- 2. 站点信息 / 作者资料(单行,固定 id=1)
--    同时支撑「左侧个人信息卡」与「关于作者页」
-- -------------------------------------------------------------
CREATE TABLE site_info (
  id            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键(固定为 1)',
  nickname      VARCHAR(50)      NOT NULL DEFAULT ''      COMMENT '昵称',
  avatar_url    VARCHAR(255)     NOT NULL DEFAULT ''      COMMENT '头像 URL',
  bio           VARCHAR(255)     NOT NULL DEFAULT ''      COMMENT '一句话简介(侧栏用)',
  about_content MEDIUMTEXT                                COMMENT '关于页正文(Markdown)',
  email         VARCHAR(100)     NOT NULL DEFAULT ''      COMMENT '邮箱',
  wechat        VARCHAR(50)      NOT NULL DEFAULT ''      COMMENT '微信号',
  wechat_qr_url VARCHAR(255)     NOT NULL DEFAULT ''      COMMENT '公众号二维码 URL',
  github_url    VARCHAR(255)     NOT NULL DEFAULT ''      COMMENT 'GitHub 主页',
  rss_url       VARCHAR(255)     NOT NULL DEFAULT ''      COMMENT 'RSS 地址',
  location      VARCHAR(100)     NOT NULL DEFAULT ''      COMMENT '所在城市',
  job           VARCHAR(100)     NOT NULL DEFAULT ''      COMMENT '职位 / 方向',
  updated_at    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点信息/作者资料(单行)';

-- -------------------------------------------------------------
-- 3. 分类
-- -------------------------------------------------------------
CREATE TABLE category (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  name       VARCHAR(50)     NOT NULL                COMMENT '分类名',
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类';

-- -------------------------------------------------------------
-- 4. 标签
-- -------------------------------------------------------------
CREATE TABLE tag (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  name       VARCHAR(50)     NOT NULL                COMMENT '标签名',
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签';

-- -------------------------------------------------------------
-- 5. 文章
-- -------------------------------------------------------------
CREATE TABLE article (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  title        VARCHAR(200)    NOT NULL                COMMENT '标题',
  summary      VARCHAR(500)    NOT NULL DEFAULT ''      COMMENT '摘要(文字卡片用)',
  content      MEDIUMTEXT      NOT NULL                COMMENT '正文(Markdown 原文)',
  cover_url    VARCHAR(255)             DEFAULT NULL    COMMENT '封面 URL(空 = 文字卡片)',
  category_id  BIGINT UNSIGNED NOT NULL                COMMENT '分类 ID(逻辑外键)',
  status       TINYINT         NOT NULL DEFAULT 0       COMMENT '状态:0 草稿 / 1 已发布',
  view_count   INT UNSIGNED    NOT NULL DEFAULT 0       COMMENT '阅读量',
  is_deleted   TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除:0 否 / 1 是',
  published_at DATETIME                 DEFAULT NULL    COMMENT '发布时间(发布时写入)',
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_category (category_id),
  KEY idx_status_published (status, published_at),
  KEY idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章';

-- -------------------------------------------------------------
-- 6. 文章-标签 关联(多对多)
-- -------------------------------------------------------------
CREATE TABLE article_tag (
  article_id BIGINT UNSIGNED NOT NULL COMMENT '文章 ID',
  tag_id     BIGINT UNSIGNED NOT NULL COMMENT '标签 ID',
  PRIMARY KEY (article_id, tag_id),
  KEY idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联';

-- -------------------------------------------------------------
-- 7. 评论(留言板式,最多二级)
-- -------------------------------------------------------------
CREATE TABLE comment (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  article_id BIGINT UNSIGNED NOT NULL                COMMENT '文章 ID(逻辑外键)',
  parent_id  BIGINT UNSIGNED         DEFAULT NULL    COMMENT '父评论 ID:空 = 顶级,有值 = 挂在该顶级下的二级回复',
  nickname   VARCHAR(50)     NOT NULL                COMMENT '昵称(自定义或随机生成)',
  email      VARCHAR(100)            DEFAULT NULL    COMMENT '邮箱(选填)',
  content    VARCHAR(1000)   NOT NULL                COMMENT '内容(渲染前需 XSS 净化)',
  status     TINYINT         NOT NULL DEFAULT 0       COMMENT '状态:0 待审核 / 1 通过 / 2 拒绝',
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_article_status (article_id, status),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论';

-- -------------------------------------------------------------
-- 8. 开源项目
-- -------------------------------------------------------------
CREATE TABLE project (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  name        VARCHAR(100)    NOT NULL                COMMENT '项目名',
  description VARCHAR(500)    NOT NULL DEFAULT ''      COMMENT '简介',
  tech_stack  VARCHAR(255)    NOT NULL DEFAULT ''      COMMENT '技术栈',
  github_url  VARCHAR(255)    NOT NULL DEFAULT ''      COMMENT 'GitHub 链接',
  sort        INT             NOT NULL DEFAULT 0       COMMENT '排序(小在前)',
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开源项目';

-- =============================================================
-- 初始化数据
-- =============================================================

-- 站点信息单行(先占位,后续在后台编辑)
INSERT INTO site_info (id, nickname, bio) VALUES (1, '你的昵称', '一句话简介');

-- 管理员账号
-- ⚠️ password_hash 必须替换为真实 BCrypt 哈希,不要直接用明文。
--    生成方式(任选一种):
--    a) 在 Spring 项目里临时跑一行:new BCryptPasswordEncoder().encode("你的密码")
--    b) 命令行:htpasswd -bnBC 10 "" 你的密码 | tr -d ':\n'
--    生成后把下面的占位串替换掉。
INSERT INTO admin (username, password_hash)
VALUES ('admin', '$2a$10$PLACEHOLDER_REPLACE_WITH_REAL_BCRYPT_HASH______________');
