-- 用户表新增密码字段（BCrypt哈希）
ALTER TABLE `user` ADD COLUMN `password` VARCHAR(255) DEFAULT NULL COMMENT 'BCrypt密码哈希';
