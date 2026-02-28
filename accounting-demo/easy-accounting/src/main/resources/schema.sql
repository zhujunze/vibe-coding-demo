-- 创建数据库
CREATE DATABASE IF NOT EXISTS `accounting_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `accounting_db`;

-- 1. 用户表 (users)
-- 存储用户基础信息和认证信息，支持手机号验证码注册。
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号（主登录方式）',
  `password` varchar(255) DEFAULT NULL COMMENT '密码(BCrypt加密，可选)',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT 1 COMMENT '状态(1-正常，0-禁用)',
  `register_type` varchar(20) DEFAULT 'phone' COMMENT '注册方式(phone-手机, guest-游客)',
  `failed_login_attempts` int DEFAULT 0 COMMENT '登录失败次数',
  `lockout_time` datetime DEFAULT NULL COMMENT '锁定截止时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 账单分类表 (categories)
-- 支持系统预置分类和用户自定义分类，按类型区分收入和支出。
CREATE TABLE IF NOT EXISTS `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户ID(NULL表示系统预置)',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `type` varchar(10) NOT NULL COMMENT '类型(expense-支出，income-收入)',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标标识',
  `color` varchar(7) DEFAULT NULL COMMENT '颜色值(十六进制)',
  `sort_order` int DEFAULT 0 COMMENT '排序权重',
  `is_system` tinyint DEFAULT 0 COMMENT '是否系统预置(1-是，0-否)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_system` (`is_system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单分类表';

-- 初始化系统预置分类数据
INSERT INTO `categories` (name, type, icon, color, is_system, sort_order) VALUES
('餐饮', 'expense', 'food', '#FF6B6B', 1, 1),
('交通', 'expense', 'transport', '#4ECDC4', 1, 2),
('购物', 'expense', 'shopping', '#45B7D1', 1, 3),
('娱乐', 'expense', 'entertainment', '#96CEB4', 1, 4),
('居住', 'expense', 'housing', '#FFD93D', 1, 5),
('医疗', 'expense', 'medical', '#FF8C94', 1, 6),
('工资', 'income', 'salary', '#51CF66', 1, 1),
('兼职', 'income', 'parttime', '#FFD93D', 1, 2),
('理财', 'income', 'investment', '#6A89CC', 1, 3),
('礼金', 'income', 'gift', '#F8EFBA', 1, 4);

-- 3. 账单记录表 (transactions)
-- 支持快速记账流程，优化索引提升查询性能。
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `type` varchar(10) NOT NULL COMMENT '类型(expense-支出，income-收入)',
  `date` date NOT NULL COMMENT '记账日期',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `sync_status` tinyint DEFAULT 1 COMMENT '同步状态(1-已同步，0-待同步)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_date` (`date`),
  KEY `idx_user_date` (`user_id`, `date`),
  KEY `idx_user_type_date` (`user_id`, `type`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单记录表';

-- 4. 分类预算表 (category_budgets)
-- 支持按消费类别设置月度预算，实现预算监控和预警。
CREATE TABLE IF NOT EXISTS `category_budgets` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预算ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `month` varchar(7) NOT NULL COMMENT '预算月份(YYYY-MM)',
  `budget_amount` decimal(12,2) NOT NULL COMMENT '预算金额',
  `alert_threshold` decimal(5,2) DEFAULT 0.80 COMMENT '预警阈值(默认80%)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_category_month` (`user_id`, `category_id`, `month`),
  KEY `idx_user_month` (`user_id`, `month`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类预算表';

-- 5. 用户统计表 (user_stats)
-- 缓存用户记账统计数据，提升查询性能。
CREATE TABLE IF NOT EXISTS `user_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_days` int DEFAULT 0 COMMENT '累计记账天数',
  `total_records` int DEFAULT 0 COMMENT '记账总笔数',
  `continuous_days` int DEFAULT 0 COMMENT '连续打卡天数',
  `last_record_date` date DEFAULT NULL COMMENT '最后记账日期',
  `current_month_expense` decimal(12,2) DEFAULT 0.00 COMMENT '当月支出',
  `current_month_income` decimal(12,2) DEFAULT 0.00 COMMENT '当月收入',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户统计表';
