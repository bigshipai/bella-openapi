SET NAMES utf8mb4;
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL auto_increment COMMENT '用户ID',
                        `user_name` VARCHAR(64)  COMMENT '用户名',
                        `email` VARCHAR(128)  COMMENT '邮箱',
                        `source` VARCHAR(32)  NOT NULL COMMENT '用户来源',
                        `source_id` VARCHAR(32)  NOT NULL COMMENT '来源ID',
                        `manager_ak` VARCHAR(255)  COMMENT '管理员ak-code',
                        `optional_info` text  COMMENT '扩展信息',
                        `ctime`  datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `mtime`  datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_source_source_id` (`source`, `source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000  COMMENT='用户表';
