/*
 Navicat Premium Dump SQL

 Source Server         : ub-do-local
 Source Server Type    : MySQL
 Source Server Version : 80409 (8.4.9)
 Source Host           : 192.168.160.128:3306
 Source Schema         : bella_openapi

 Target Server Type    : MySQL
 Target Server Version : 80409 (8.4.9)
 File Encoding         : 65001

 Date: 09/06/2026 09:01:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apikey
-- ----------------------------
DROP TABLE IF EXISTS `apikey`;
CREATE TABLE `apikey`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'ak编码',
  `ak_sha` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '加密ak',
  `ak_display` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '脱敏ak',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '名字',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '父ak',
  `out_entity_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '授权实体code',
  `service_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '服务id',
  `owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者类型（系统/组织/个人）',
  `owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者系统号',
  `owner_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者名称',
  `manager_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '管理人编码',
  `manager_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '管理人姓名',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色编码',
  `certify_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '安全认证码',
  `safety_scene_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '安全认证场景code',
  `safety_level` tinyint NOT NULL DEFAULT 0 COMMENT '安全等级',
  `month_quota` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '每月额度',
  `qps_limit` int NULL DEFAULT 200 COMMENT 'QPS限制（每秒请求数，0使用系统默认值，负数不限制）',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
  `remark` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_code`(`code` ASC) USING BTREE,
  UNIQUE INDEX `uniq_idx_ak_sha`(`ak_sha` ASC) USING BTREE,
  INDEX `idx_parent_out_entity_code`(`parent_code` ASC, `out_entity_code` ASC) USING BTREE,
  INDEX `idx_owner_type_code`(`owner_type` ASC, `owner_code` ASC) USING BTREE,
  INDEX `idx_manager_code`(`manager_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'ak' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apikey
-- ----------------------------
INSERT INTO `apikey` VALUES (7, 'ak-bootstrap-admin', '4c4f1b00eb835a37f6e34e513a087f152c53a4b91114c33ccaaf1b761d177533', 'be****-key', '初始管理员密钥', '', '', '', 'console', '1', 'admin', '', '', 'all', '', '', 40, 999999.00, 200, 'active', 'Bootstrap admin key', 0, 'system', 0, 'system', '2026-06-03 20:57:52', '2026-06-03 21:59:46');
INSERT INTO `apikey` VALUES (8, 'ak-0d95b2e5-ba0f-48c6-8ebb-827dc320134d', 'edd3641319a43a7aa5bfdf437ffa3df39ebbd1157e342457798f21d45cfc41cb', 'f1****4457', '', '', '', '', 'person', '1', 'admin', '1', 'admin', 'all', '', '', 40, 50.00, NULL, 'active', '', 1, 'admin', 1, 'admin', '2026-06-03 21:20:53', '2026-06-03 21:28:59');
INSERT INTO `apikey` VALUES (9, 'ak-15fca215-0959-41f0-9673-3ed3c2c72d7e', '4ef2984aea9e26862034181a17629e9b15e24bc4db4287e62b309400d8ab027c', 'ef****2994', '', '', '', '', 'person', '1', 'admin', '1', 'admin', 'low', '', '', 40, 50.00, NULL, 'active', '', 1, 'admin', 1, 'admin', '2026-06-04 22:18:23', '2026-06-04 22:18:23');
INSERT INTO `apikey` VALUES (10, 'ak-76de11c7-a4e2-42b1-b2f3-63de8e96cb24', '2d8599055aa2f369405c54868a08d288822bb7a90adaa433a211efe20bcc5c22', 'e6****2fb6', 'dfg', '', '', '', 'org', '234', 'dsf', '1', 'admin', 'low', '', '', 40, 50.00, NULL, 'active', '', 1, 'admin', 1, 'admin', '2026-06-04 22:21:51', '2026-06-04 22:21:51');
INSERT INTO `apikey` VALUES (11, 'ak-2e4c5e9c-0a72-4890-92af-e6c9d5448da4', 'bac0c0895550552183494fae30fc1c42bd6d83977de8b07b20344008e646a629', '77****0469', 'Console AK', '', '', '', 'console', '10000000', 'jason', '', '', 'low', '', '', 40, 20.00, NULL, 'active', '', 0, 'system', 0, 'system', '2026-06-08 20:43:30', '2026-06-08 20:43:30');
INSERT INTO `apikey` VALUES (12, 'ak-c970c737-15e4-49e5-ae28-85ee80a7327b', '6a18ffafe976ae829a5721f5b65b2c604a852eb56dec5c8265eefe12bcd4e6d8', '86****121a', '', '', '', '', 'person', '', '', '', '', 'low', '', '', 40, 50.00, NULL, 'active', '', 10000000, 'jason', 10000000, 'jason', '2026-06-08 20:44:30', '2026-06-08 20:44:30');
INSERT INTO `apikey` VALUES (13, 'ak-53ac0bd0-f3c3-46d1-81fa-0542091b7d2f', 'ef03b9b655aef4c894c120ccc6d01826f5cc348ed6e0326f221256660cbb4368', '79****7682', '', '', '', '', 'person', '10000000', 'jason', '10000000', 'jason', 'low', '', '', 40, 50.00, NULL, 'active', '', 10000000, 'jason', 10000000, 'jason', '2026-06-08 20:44:39', '2026-06-08 20:44:39');
INSERT INTO `apikey` VALUES (14, 'ak-d5747156-b82c-4ce6-a367-38c4605b60de', '7e91082787cada48e5637289f6ae19762fc8179bc445cc504bd692fcd05c9369', 'f9****1229', '', '', '', '', 'person', '10000000', 'jason', '10000000', 'jason', 'low', '', '', 40, 50.00, NULL, 'active', '', 10000000, 'jason', 10000000, 'jason', '2026-06-08 20:45:03', '2026-06-08 20:49:50');

-- ----------------------------
-- Table structure for apikey_change_log
-- ----------------------------
DROP TABLE IF EXISTS `apikey_change_log`;
CREATE TABLE `apikey_change_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `action_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更类型(owner_transfer/owner_change/parent_change)',
  `ak_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '入口AK编码',
  `affected_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '受影响AK编码列表(JSON)',
  `from_owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更前所有者类型',
  `from_owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更前所有者编码',
  `from_owner_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更前所有者名称',
  `to_owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更后所有者类型',
  `to_owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更后所有者编码',
  `to_owner_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更后所有者名称',
  `from_parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更前父AK编码',
  `to_parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更后父AK编码',
  `from_manager_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更前管理者编码',
  `from_manager_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更前管理者名称',
  `to_manager_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更后管理者编码',
  `to_manager_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更后管理者名称',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '变更原因',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'completed' COMMENT '变更状态',
  `operator_uid` bigint NOT NULL DEFAULT 0 COMMENT '操作人用户ID',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '操作人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ak_code`(`ak_code` ASC) USING BTREE,
  INDEX `idx_action_type`(`action_type` ASC) USING BTREE,
  INDEX `idx_ctime`(`ctime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API Key变更历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apikey_change_log
-- ----------------------------

-- ----------------------------
-- Table structure for apikey_month_cost
-- ----------------------------
DROP TABLE IF EXISTS `apikey_month_cost`;
CREATE TABLE `apikey_month_cost`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ak_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'ak编码',
  `month` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `amount` decimal(12, 4) NOT NULL DEFAULT 0.0000 COMMENT '开销（分）',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_ak_code_month`(`ak_code` ASC, `month` ASC) USING BTREE,
  INDEX `idx_month`(`month` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'ak月花费' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apikey_month_cost
-- ----------------------------
INSERT INTO `apikey_month_cost` VALUES (1, 'ak-bootstrap-admin', '2026-06', 9.4006, '2026-06-03 22:00:28', '2026-06-08 20:41:55');
INSERT INTO `apikey_month_cost` VALUES (2, 'ak-0d95b2e5-ba0f-48c6-8ebb-827dc320134d', '2026-06', 0.0671, '2026-06-04 08:42:56', '2026-06-07 17:24:14');
INSERT INTO `apikey_month_cost` VALUES (3, 'ak-2e4c5e9c-0a72-4890-92af-e6c9d5448da4', '2026-06', 2.5012, '2026-06-08 22:53:01', '2026-06-08 22:57:01');

-- ----------------------------
-- Table structure for apikey_role
-- ----------------------------
DROP TABLE IF EXISTS `apikey_role`;
CREATE TABLE `apikey_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'ak编码',
  `path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '授权的path',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'ak角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apikey_role
-- ----------------------------
INSERT INTO `apikey_role` VALUES (1, 'low', '{\"included\":[\"/v*/**\", \"/console/apikey/**\", \"/console/userInfo\"], \"excluded\":[\"/v*/apikey/create\", \"/v*/route/**\", \"/v*/log/**\", \"/console/apikey/quota/update\", \"/console/apikey/role/update\"]}', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `apikey_role` VALUES (2, 'high', '{\"included\":[\"/v*/**\", \"/console/apikey/**\", \"/console/userInfo\"], \"excluded\":[\"/v*/route/**\", \"/v*/log/**\", \"/console/apikey/quota/update\", \"/console/apikey/role/update\"]}', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `apikey_role` VALUES (3, 'console', '{\"included\":[\"/v*/**\", \"/console/**\"], \"excluded\":[]}', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `apikey_role` VALUES (4, 'all', '{\"included\":[\"/**\"], \"excluded\":[]}', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');

-- ----------------------------
-- Table structure for apikey_transfer_log
-- ----------------------------
DROP TABLE IF EXISTS `apikey_transfer_log`;
CREATE TABLE `apikey_transfer_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ak_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'API Key编码',
  `from_owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原所有者类型',
  `from_owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原所有者编码',
  `from_owner_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原所有者姓名',
  `to_owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '新所有者类型',
  `to_owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '新所有者编码',
  `to_owner_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '新所有者姓名',
  `transfer_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转移原因',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'completed' COMMENT '转移状态(pending/completed/failed)',
  `operator_uid` bigint NOT NULL DEFAULT 0 COMMENT '操作人用户ID',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '操作人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ak_code`(`ak_code` ASC) USING BTREE,
  INDEX `idx_from_owner`(`from_owner_type` ASC, `from_owner_code` ASC) USING BTREE,
  INDEX `idx_to_owner`(`to_owner_type` ASC, `to_owner_code` ASC) USING BTREE,
  INDEX `idx_ctime`(`ctime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API Key所有权转移审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apikey_transfer_log
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '类目编码',
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '类目名',
  `parent_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '父类目编码',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_category_code`(`category_code` ASC) USING BTREE,
  UNIQUE INDEX `uniq_idx_parent_code_category_name`(`parent_code` ASC, `category_name` ASC) USING BTREE,
  INDEX `idx_category_name`(`category_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '类目' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '0001', '语言类', '', 'active', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `category` VALUES (2, '0002', '语音类', '', 'active', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `category` VALUES (3, '0003', '图像类', '', 'active', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `category` VALUES (4, '0002-0001', '语音合成', '0002', 'active', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `category` VALUES (5, '0002-0002', '语音识别', '0002', 'active', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `category` VALUES (6, '0004', '视频类', '', 'active', 0, 'system', 0, 'system', '2026-06-03 20:41:22', '2026-06-03 20:41:22');

-- ----------------------------
-- Table structure for channel
-- ----------------------------
DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entity_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'model' COMMENT '实体类型（endpoint/model）',
  `entity_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '实体编码',
  `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '渠道编码',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态状态(active/inactive)',
  `owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者类型（组织/个人）',
  `owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者系统号',
  `owner_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者名称',
  `visibility` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'public' COMMENT '是否公开(private/public)',
  `trial_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否支持试用',
  `data_destination` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'inner' COMMENT '数据流向(inner/mainland/overseas)',
  `priority` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'normal' COMMENT '优先级(high/normal/low)',
  `protocol` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '协议',
  `supplier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '服务商',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '请求通道的url',
  `channel_info` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '{}' COMMENT '渠道信息',
  `price_info` varchar(8192) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '{}' COMMENT '单价',
  `queue_mode` tinyint NOT NULL DEFAULT 0 COMMENT '队列模式(0:无队列;1:pull模式;2:route模式;3:pull+route模式)',
  `queue_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '队列名称',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_channel_code`(`channel_code` ASC) USING BTREE,
  INDEX `idx_entity_type_code`(`entity_type` ASC, `entity_code` ASC) USING BTREE,
  INDEX `idx_protocol`(`protocol` ASC) USING BTREE,
  INDEX `idx_supplier`(`supplier` ASC) USING BTREE,
  INDEX `idx_queue_name`(`queue_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通道' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of channel
-- ----------------------------
INSERT INTO `channel` VALUES (1, 'model', 'deepseek-v4-pro', 'ch-ae592ed1-ee26-443c-8eaa-fc6a7c83f623', 'active', '', '', '', 'public', 1, 'mainland', 'high', 'OpenAIAdaptor', 'DeepSeek', 'https://api.deepseek.com/chat/completions', '{\"apiVersion\":\"\",\"auth\":{\"apiKey\":\"sk-c914b8b1978c4b77ae3f9817810e1d58\",\"type\":\"BEARER\"},\"deployName\":\"deepseek-v4-pro\"}', '{\"supplierDiscount\":1,\"batchDiscount\":1,\"tiers\":[{\"inputRangePrice\":{\"minToken\":0,\"maxToken\":2147483647,\"input\":0.0435,\"output\":0.087,\"imageInput\":null,\"imageOutput\":null,\"cachedRead\":0.0003625,\"cachedCreation\":null}}],\"unit\":\"分/千token\"}', 0, '', 1, 'admin', 1, 'admin', '2026-06-03 21:54:31', '2026-06-03 22:08:14');
INSERT INTO `channel` VALUES (2, 'model', 'glm-5.1', 'ch-f845b78b-ae07-49ee-aa87-62b3f624def2', 'active', '', '', '', 'public', 1, 'mainland', 'high', 'OpenAIAdaptor', 'GLM', 'https://open.bigmodel.cn/api/paas/v4/chat/completions', '{\"apiVersion\":\"\",\"auth\":{\"apiKey\":\"fcbe2e9e433b448ab604aa21141b4542.VEV2kHlfN9EMB6UK\",\"type\":\"BEARER\"},\"deployName\":\"glm-5.1\"}', '{\"supplierDiscount\":1,\"batchDiscount\":1,\"tiers\":[{\"inputRangePrice\":{\"minToken\":0,\"maxToken\":32000,\"input\":0.6,\"output\":2.4,\"imageInput\":null,\"imageOutput\":null,\"cachedRead\":0.13,\"cachedCreation\":null}},{\"inputRangePrice\":{\"minToken\":32000,\"maxToken\":2147483647,\"input\":0.8,\"output\":2.8,\"imageInput\":null,\"imageOutput\":null,\"cachedRead\":0.2,\"cachedCreation\":null}}],\"unit\":\"分/千token\"}', 0, '', 1, 'admin', 1, 'admin', '2026-06-05 11:16:28', '2026-06-05 11:17:25');
INSERT INTO `channel` VALUES (3, 'model', 'qwen3.7-max', 'ch-dbbb9ee4-c317-400f-9f1b-691ccf5d8ed1', 'active', '', '', '', 'public', 1, 'mainland', 'high', 'QwenAdaptor', 'ALI', 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions', '{\"apiVersion\":\"\",\"deployName\":\"qwen3.7-max\",\"auth\":{\"apiKey\":\"sk-4b9900c7f80646a9bfbb81a66ba7cc41\",\"type\":\"BEARER\"}}', '{\"supplierDiscount\":1,\"batchDiscount\":1,\"tiers\":[{\"inputRangePrice\":{\"minToken\":0,\"maxToken\":2147483647,\"input\":1.2,\"output\":3.6,\"imageInput\":null,\"imageOutput\":null,\"cachedRead\":0.24,\"cachedCreation\":null}}],\"unit\":\"分/千token\"}', 0, '', 1, 'admin', 1, 'admin', '2026-06-05 12:09:51', '2026-06-05 12:23:07');
INSERT INTO `channel` VALUES (4, 'model', 'text_embedding_v3', 'ch-3847b9ec-90b0-422d-afae-46784a28fc27', 'active', '', '', '', 'public', 1, 'mainland', 'high', 'OpenAIAdaptor', 'ALI', 'https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings', '{\"apiVersion\":\"\",\"batchSize\":10000,\"deployName\":\"text-embedding-v3\",\"auth\":{\"apiKey\":\"sk-4b9900c7f80646a9bfbb81a66ba7cc41\",\"type\":\"BEARER\"},\"encodingType\":\"txt\"}', '{\"input\":0.1}', 0, '', 1, 'admin', 1, 'admin', '2026-06-05 13:06:23', '2026-06-05 13:10:18');
INSERT INTO `channel` VALUES (5, 'model', 'qwen3-tts-flash', 'ch-c13e011c-6d1b-44b9-8c01-d1a5b1cda125', 'active', '', '', '', 'public', 1, 'inner', 'high', 'OpenAIAdaptor', 'ALI', 'https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation', '{\"defaultSampleRate\":0,\"defaultVoice\":\"\",\"deployName\":\"qwen3-tts-flash\",\"defaultContentType\":\"\",\"auth\":{\"type\":\"BEARER\",\"apiKey\":\"sk-4b9900c7f80646a9bfbb81a66ba7cc41\"}}', '{\"input\":22}', 0, '', 1, 'admin', 1, 'admin', '2026-06-05 23:44:55', '2026-06-05 23:44:55');
INSERT INTO `channel` VALUES (6, 'model', 'seed-tts-2.0', 'ch-180bb9fd-ac48-4157-b3e0-354bf4c9286b', 'active', '', '', '', 'public', 1, 'mainland', 'high', 'HuoShanV3Adaptor', 'huoshan', 'https://openspeech.bytedance.com/api/v3/tts/unidirectional', '{\"defaultSampleRate\":0,\"resourceId\":\"seed-tts-2.0\",\"accessKey\":\"21d96eac-5296-461b-938d-87b60f6c5ca6\",\"appId\":\"21d96eac-5296-461b-938d-87b60f6c5ca6\",\"defaultVoice\":\"zh_female_vv_uranus_bigtts\",\"defaultContentType\":\"\"}', '{\"input\":1}', 0, '', 1, 'admin', 1, 'admin', '2026-06-07 09:45:31', '2026-06-07 18:22:14');

-- ----------------------------
-- Table structure for channel_backup
-- ----------------------------
DROP TABLE IF EXISTS `channel_backup`;
CREATE TABLE `channel_backup`  (
  `id` bigint NOT NULL DEFAULT 0 COMMENT '主键ID',
  `entity_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'model' COMMENT '实体类型（endpoint/model）',
  `entity_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '实体编码',
  `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '渠道编码',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态状态(active/inactive)',
  `owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者类型（组织/个人）',
  `owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者系统号',
  `owner_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者名称',
  `visibility` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'public' COMMENT '是否公开(private/public)',
  `trial_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否支持试用',
  `data_destination` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'inner' COMMENT '数据流向(inner/mainland/overseas)',
  `priority` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'normal' COMMENT '优先级(high/normal/low)',
  `protocol` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '协议',
  `supplier` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '服务商',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '请求通道的url',
  `channel_info` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '{}' COMMENT '渠道信息',
  `price_info` varchar(8192) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '{}' COMMENT '单价',
  `queue_mode` tinyint NOT NULL DEFAULT 0 COMMENT '队列模式(0:无队列;1:pull模式;2:route模式;3:pull+route模式)',
  `queue_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '队列名称',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of channel_backup
-- ----------------------------

-- ----------------------------
-- Table structure for endpoint
-- ----------------------------
DROP TABLE IF EXISTS `endpoint`;
CREATE TABLE `endpoint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `endpoint` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '请求path',
  `endpoint_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '能力点编码',
  `endpoint_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '能力点名称',
  `document_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文档地址',
  `maintainer_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '维护人ucid',
  `maintainer_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '维护人姓名',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
  `cost_script` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '计费脚本',
  `category_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类别编码',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_endpoint_code`(`endpoint_code` ASC) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_endpoint`(`endpoint` ASC) USING BTREE,
  INDEX `uniq_idx_uni_endpoint_name`(`endpoint_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '能力点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of endpoint
-- ----------------------------
INSERT INTO `endpoint` VALUES (1, '/v1/chat/completions', 'ep-cd2190a7-38dc-427e-a167-2f138b9078e7', '智能问答', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (2, '/v1/embeddings', 'ep-4eb69c9c-4acc-40e8-a671-0b25d9473139', '向量化', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (3, '/v1/audio/speech', 'ep-c73addd8-0517-4ad9-8272-8fb1286ef456', '语音合成', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (4, '/v1/audio/transcriptions', 'ep-bd4b69f0-f9b8-4175-8296-3b267bf76fb1', '语音识别', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (5, '/v1/audio/asr/stream', 'ep-e2b95d24-8b74-4316-8cab-5943e899854d', '流式语音识别', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (6, '/v1/audio/asr/flash', 'ep-0371270b-0b90-4bb2-a760-25a00b4f9ef6', '一句话语音识别', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (7, '/v1/images/generations', 'ep-c1079a7b-1c29-44fa-b5d8-ef8a2bfd493d', '文生图', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (8, '/v1/images/edits', 'ep-ed2a031e-15e7-4199-9f93-73e1d190798e', '图生图', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint` VALUES (9, '/v1/videos', 'ep-videos-001', '视频生成', '', '0', 'system', 'active', '', NULL, NULL, 0, 'system', 0, 'system', '2026-06-03 20:41:22', '2026-06-03 20:41:22');

-- ----------------------------
-- Table structure for endpoint_category_rel
-- ----------------------------
DROP TABLE IF EXISTS `endpoint_category_rel`;
CREATE TABLE `endpoint_category_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `endpoint` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '能力点',
  `category_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '类目编码',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_endpoint_category_code`(`endpoint` ASC, `category_code` ASC) USING BTREE,
  INDEX `idx_category_code`(`category_code` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '能力点类目' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of endpoint_category_rel
-- ----------------------------
INSERT INTO `endpoint_category_rel` VALUES (1, '/v1/chat/completions', '0001', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (2, '/v1/embeddings', '0001', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (3, '/v1/audio/speech', '0002-0001', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (4, '/v1/audio/transcriptions', '0002-0002', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (5, '/v1/audio/asr/flash', '0002-0002', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (6, '/v1/audio/asr/stream', '0002-0002', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (7, '/v1/images/generations', '0003', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (8, '/v1/images/edits', '0003', 0, 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `endpoint_category_rel` VALUES (9, '/v1/videos', '0004', 0, 0, 'system', 0, 'system', '2026-06-03 20:41:22', '2026-06-03 20:41:22');

-- ----------------------------
-- Table structure for instance
-- ----------------------------
DROP TABLE IF EXISTS `instance`;
CREATE TABLE `instance`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `port` int NOT NULL DEFAULT 0,
  `status` int NOT NULL DEFAULT 0,
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_ip_port`(`ip` ASC, `port` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of instance
-- ----------------------------
INSERT INTO `instance` VALUES (1, '192.168.5.106', 8080, 1, '2026-06-03 20:45:31', '2026-06-08 23:31:39');
INSERT INTO `instance` VALUES (24, '192.168.5.106', 0, 0, '2026-06-07 22:10:06', '2026-06-07 22:10:10');

-- ----------------------------
-- Table structure for model
-- ----------------------------
DROP TABLE IF EXISTS `model`;
CREATE TABLE `model`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '模型名称',
  `document_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文档地址',
  `visibility` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'private' COMMENT '是否公开(private/public)',
  `openness_type` tinyint NOT NULL DEFAULT 0 COMMENT '开放程度(0:未知/1:闭源/2:开源)',
  `owner_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者类型（系统/组织/个人）',
  `owner_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者系统号',
  `owner_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者名称',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'active' COMMENT '状态(active/inactive)',
  `properties` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '{}' COMMENT '属性',
  `features` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '{}' COMMENT '特性',
  `linked_to` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '模型软链',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_model_name`(`model_name` ASC) USING BTREE,
  INDEX `idx_owner_type_code`(`owner_type` ASC, `owner_code` ASC) USING BTREE,
  INDEX `idx_owner_name`(`owner_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of model
-- ----------------------------
INSERT INTO `model` VALUES (1, 'deepseek-v4-pro', 'https://api-docs.deepseek.com/zh-cn/', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":1000000,\"max_output_context\":384000}', '{\"json_format\":false,\"stream_function_call\":true,\"stream\":true,\"function_call\":true,\"agent_thought\":true,\"reason_content\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 21:45:10');
INSERT INTO `model` VALUES (2, 'deepseek-v4-flash', 'https://api-docs.deepseek.com/zh-cn/', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":1000000,\"max_output_context\":384000}', '{\"json_format\":false,\"stream_function_call\":true,\"stream\":true,\"function_call\":true,\"parallel_tool_calls\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 21:45:30');
INSERT INTO `model` VALUES (3, 'claude-3.7-sonnet', 'https://docs.anthropic.com/en/docs/intro-to-claude', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":128000,\"max_output_context\":128000}', '{\"vision\":true,\"json_format\":false,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"stream\":true,\"function_call\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (4, 'claude-3.5-sonnet', 'https://docs.anthropic.com/en/docs/intro-to-claude', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":200000,\"max_output_context\":200000}', '{\"vision\":true,\"json_format\":false,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"stream\":true,\"function_call\":true,\"reason_content\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (5, 'o3', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":200000,\"max_output_context\":100000}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":true,\"vision\":true,\"json_format\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (6, 'o1', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":200000,\"max_output_context\":100000}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":true,\"vision\":true,\"json_format\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (7, 'gpt-4o', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":128000,\"max_output_context\": 16384}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":true,\"vision\":true,\"json_format\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (8, 'qwen-vl-max', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":129024,\"max_output_context\":8192}', '{\"vision\":true,\"json_format\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"stream\":true,\"function_call\":true,\"json_schema\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (9, 'Doubao-1.5-vision-pro-32k', 'https://www.volcengine.com/docs/82379/1330310', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":32000,\"max_output_context\":12288}', '{\"vision\":true,\"json_format\":true,\"stream_function_call\":false,\"parallel_tool_calls\":false,\"stream\":true,\"function_call\":false,\"json_schema\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (10, 'qwen3.7-max', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":991000,\"max_output_context\":64000}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 1, 'admin', '2026-06-03 20:40:00', '2026-06-05 11:48:00');
INSERT INTO `model` VALUES (11, 'qwen-vl-plus', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":129024,\"max_output_context\":8192}', '{\"json_format\":true,\"stream_function_call\":true,\"vision\":true,\"stream\":true,\"parallel_tool_calls\":false,\"function_call\":true,\"json_schema\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (12, 'qwen-plus', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":129024,\"max_output_context\":8192}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (13, 'qwen-turbo', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":1000000,\"max_output_context\":8192}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (14, 'qwen-long', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":10000000,\"max_output_context\":6000}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (15, 'Doubao-1.5-pro-256k', 'https://www.volcengine.com/docs/82379/1330310', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":256000,\"max_output_context\":12288}', '{\"json_format\":true,\"stream\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (16, 'Doubao-1.5-pro-32k', 'https://www.volcengine.com/docs/82379/1330310', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":32000,\"max_output_context\":12288}', '{\"json_format\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"stream\":true,\"function_call\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (17, 'doubao-pro-256k', 'https://www.volcengine.com/docs/82379/1330310', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":256000,\"max_output_context\":4096}', '{\"json_format\":true,\"function_call\":true,\"stream_function_call\":false,\"stream\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (18, 'doubao-pro-32k', 'https://www.volcengine.com/docs/82379/1330310', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":32000,\"max_output_context\":4096}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (19, 'moonshot-v1-128k', 'https://platform.moonshot.cn/docs', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":128000,\"max_output_context\":4096}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (20, 'moonshot-v1-32k', 'https://platform.moonshot.cn/docs', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":32000,\"max_output_context\":4096}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (21, 'moonshot-v1-8k', 'https://platform.moonshot.cn/docs', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":8000,\"max_output_context\":4096}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (22, 'glm-5.1', 'https://docs.bigmodel.cn/cn/guide/models/text/glm-5.1', 'public', 1, 'system', '0', 'system', 'active', '{\"max_input_context\":200000,\"max_output_context\":128000}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 1, 'admin', '2026-06-03 20:40:00', '2026-06-05 11:10:21');
INSERT INTO `model` VALUES (23, 'gpt-4o-mini', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":128000,\"max_output_context\":16384}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":true,\"vision\":true,\"json_format\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (24, 'o1-mini', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":128000,\"max_output_context\":65536}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":true,\"vision\":false,\"json_format\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (25, 'o3-mini', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":200000,\"max_output_context\":100000}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":true,\"vision\":false,\"json_format\":true,\"json_schema\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (26, 'qwen2-72b-instruct', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":131072,\"max_output_context\":129024}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (27, 'qwen2.5-72b-instruct', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":131072,\"max_output_context\":129024}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (28, 'qwen2.5-32b-instruct', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":131072,\"max_output_context\":129024}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (29, 'qwen2.5-14b-instruct', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":131072,\"max_output_context\":129024}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (30, 'qwen2.5-7b-instruct', 'https://help.aliyun.com/zh/model-studio/getting-started/models', 'public', 0, 'system', '0', 'system', 'active', '{\"max_input_context\":131072,\"max_output_context\":129024}', '{\"stream\":true,\"function_call\":true,\"stream_function_call\":true,\"parallel_tool_calls\":false,\"vision\":false,\"json_format\":false}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (31, 'text_embedding_v1', 'https://help.aliyun.com/zh/model-studio/user-guide/embedding', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (32, 'text_embedding_v2', 'https://help.aliyun.com/zh/model-studio/user-guide/embedding', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (33, 'text_embedding_v3', 'https://help.aliyun.com/zh/model-studio/user-guide/embedding', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (34, 'text-embedding-3-small', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (35, 'text-embedding-3-large', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (36, 'text-embedding-ada-002', 'https://platform.openai.com/docs/models', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (37, 'doubao-embedding', 'https://www.volcengine.com/docs/82379/1330310#doubao-embedding', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (38, 'doubao-embedding-large', 'https://www.volcengine.com/docs/82379/1330310#doubao-embedding', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (39, 'tts-1', 'https://platform.openai.com/docs/models/tts-1', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (40, 'tts-1-hd', 'https://platform.openai.com/docs/models/tts-1-hd', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (41, 'huoshan-tts', 'https://www.volcengine.com/docs/6561/162929', 'public', 0, 'system', '0', 'system', 'active', '{}', '{\"stream\":true}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (42, 'whisper-1', 'https://platform.openai.com/docs/guides/speech-to-text', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (43, 'huoshan-flash-asr', 'https://www.volcengine.com/docs/6561/162929', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (44, 'huoshan-realtime-asr', 'https://www.volcengine.com/docs/6561/162929', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (45, 'dall-e-3', 'https://platform.openai.com/docs/models/dall-e-3', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (46, 'dall-e-2', 'https://platform.openai.com/docs/models/dall-e-2', 'public', 0, 'system', '0', 'system', 'active', '{}', '{}', '', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model` VALUES (47, 'cosyvoice-v3-flash', '', 'public', 0, 'system', '', '', 'active', '{}', '{}', '', 0, '', 0, '', '2026-06-05 23:20:42', '2026-06-05 23:27:31');
INSERT INTO `model` VALUES (48, 'qwen3-tts-flash', '', 'public', 0, 'system', '', '', 'active', '{}', '{}', '', 0, '', 0, '', '2026-06-05 23:27:12', '2026-06-05 23:27:26');
INSERT INTO `model` VALUES (49, 'seed-tts-2.0', 'https://www.volcengine.com/docs/6561/1598757?lang=zh', 'public', 0, 'system', 'HuoShan', 'HuoShan', 'active', '{}', '{\"stream\":true,\"customize_sound_color\":true}', '', 1, 'admin', 1, 'admin', '2026-06-07 09:41:25', '2026-06-07 09:54:09');

-- ----------------------------
-- Table structure for model_authorizer_rel
-- ----------------------------
DROP TABLE IF EXISTS `model_authorizer_rel`;
CREATE TABLE `model_authorizer_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '模型名称',
  `authorizer_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者类型（组织/个人）',
  `authorizer_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者系统号',
  `authorizer_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '所有者名称',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_model_name_authorizer_code`(`model_name` ASC, `authorizer_code` ASC) USING BTREE,
  INDEX `idx_authorizer_code`(`authorizer_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模型授权信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of model_authorizer_rel
-- ----------------------------

-- ----------------------------
-- Table structure for model_endpoint_rel
-- ----------------------------
DROP TABLE IF EXISTS `model_endpoint_rel`;
CREATE TABLE `model_endpoint_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '模型名称',
  `endpoint` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '请求path',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人id',
  `cu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '编辑人id',
  `mu_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '编辑人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_uni_endpoint_model`(`endpoint` ASC, `model_name` ASC) USING BTREE,
  INDEX `idx_model_name`(`model_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模型能力点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of model_endpoint_rel
-- ----------------------------
INSERT INTO `model_endpoint_rel` VALUES (1, 'deepseek-v4-flash', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 21:46:29');
INSERT INTO `model_endpoint_rel` VALUES (2, 'deepseek-v4-pro', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 21:46:36');
INSERT INTO `model_endpoint_rel` VALUES (3, 'claude-3.7-sonnet', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (4, 'claude-3.5-sonnet', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (5, 'o3', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (6, 'o1', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (7, 'gpt-4o', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (8, 'qwen-vl-max', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (9, 'Doubao-1.5-vision-pro-32k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (10, 'qwen3.7-max', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-05 11:35:24');
INSERT INTO `model_endpoint_rel` VALUES (11, 'qwen-vl-plus', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (12, 'qwen-plus', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (13, 'qwen-turbo', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (14, 'qwen-long', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (15, 'Doubao-1.5-pro-256k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (16, 'Doubao-1.5-pro-32k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (17, 'doubao-pro-256k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (18, 'doubao-pro-32k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (19, 'moonshot-v1-128k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (20, 'moonshot-v1-32k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (21, 'moonshot-v1-8k', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (22, 'glm-5.1', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-05 11:08:49');
INSERT INTO `model_endpoint_rel` VALUES (23, 'gpt-4o-mini', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (24, 'o1-mini', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (25, 'o3-mini', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (26, 'qwen2-72b-instruct', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (27, 'qwen2.5-72b-instruct', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (28, 'qwen2.5-32b-instruct', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (29, 'qwen2.5-14b-instruct', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (30, 'qwen2.5-7b-instruct', '/v1/chat/completions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (31, 'text_embedding_v1', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (32, 'text_embedding_v2', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (33, 'text_embedding_v3', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (34, 'text-embedding-3-small', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (35, 'text-embedding-3-large', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (36, 'text-embedding-ada-002', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (37, 'doubao-embedding', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (38, 'doubao-embedding-large', '/v1/embeddings', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (39, 'tts-1', '/v1/audio/speech', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (40, 'tts-1-hd', '/v1/audio/speech', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (41, 'huoshan-tts', '/v1/audio/speech', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (42, 'whisper-1', '/v1/audio/transcriptions', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (43, 'huoshan-flash-asr', '/v1/audio/asr/flash', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (44, 'huoshan-realtime-asr', '/v1/audio/asr/stream', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (45, 'dall-e-3', '/v1/images/generations', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (46, 'dall-e-2', '/v1/images/generations', 0, 'system', 0, 'system', '2026-06-03 20:40:00', '2026-06-03 20:40:00');
INSERT INTO `model_endpoint_rel` VALUES (47, 'qwen3-tts-flash', '/v1/audio/speech', 0, 'system', 0, 'system', '2026-06-05 23:27:58', '2026-06-05 23:28:09');
INSERT INTO `model_endpoint_rel` VALUES (48, 'seed-tts-2.0', '/v1/audio/speech', 0, 'system', 0, 'system', '2026-06-07 09:41:25', '2026-06-07 09:49:29');

-- ----------------------------
-- Table structure for space
-- ----------------------------
DROP TABLE IF EXISTS `space`;
CREATE TABLE `space`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `space_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '空间编码',
  `space_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '空间名称',
  `space_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '空间描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '删除状态(0未删除，-1已删除)',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `owner_uid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '空间拥有人系统号',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '空间创建人系统号',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '空间最后一次更新人系统号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_space_code`(`space_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '空间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space
-- ----------------------------

-- ----------------------------
-- Table structure for space_member
-- ----------------------------
DROP TABLE IF EXISTS `space_member`;
CREATE TABLE `space_member`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `space_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '空间编码',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色编码',
  `member_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '成员姓名',
  `member_uid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '成员系统号',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '删除状态(0未删除，-1已删除)',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次修改时间',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人系统号',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '最后一次更新人系统号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_uid`(`member_uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '空间成员信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space_member
-- ----------------------------

-- ----------------------------
-- Table structure for space_role
-- ----------------------------
DROP TABLE IF EXISTS `space_role`;
CREATE TABLE `space_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `space_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '团队编码',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色编码',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_desc` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色描述',
  `role_type` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '角色类型(1系统内置，2自定义)',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '删除状态(0未删除，-1已删除)',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人系统号',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '最后一次更新人系统号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_space_code_role_code`(`space_code` ASC, `role_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '空间角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space_role
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户来源',
  `source_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源ID',
  `manager_ak` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员ak-code',
  `optional_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展信息',
  `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'BCrypt密码哈希',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_source_source_id`(`source` ASC, `source_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10000001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (10000000, 'jason', 'florightel@126.com', 'email', 'florightel@126.com', '7797334c-1ea9-46ac-9a44-f57118760469', NULL, '2026-06-08 20:43:30', '2026-06-08 20:43:30', '$2a$10$qPHYznj.wDFiZ3KQljtxbOISardVSFRYgEQx2tEqg8MWqlPnzbxnG');

-- ----------------------------
-- Table structure for video_job
-- ----------------------------
DROP TABLE IF EXISTS `video_job`;
CREATE TABLE `video_job`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '视频ID/任务ID',
  `space_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '空间编码',
  `ak_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'API Key编码',
  `model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '模型名称',
  `progress` int NOT NULL DEFAULT 0 COMMENT '进度百分比',
  `prompt` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提示词',
  `input_reference_file_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '输入参考文件ID（用户上传的参考视频/图片）',
  `seconds` bigint NOT NULL DEFAULT 0 COMMENT '时长',
  `size` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '视频尺寸',
  `remixed_from_video_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '源视频ID（remix任务）',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '视频任务完成时间',
  `expires_at` timestamp NULL DEFAULT NULL COMMENT '视频任务下载过期时间',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'queued' COMMENT '任务状态(queued/submitting/processing/completed/failed/cancelled)',
  `bound_file_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '绑定的文件ID（用于file api转储检索）',
  `callback_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回调URL',
  `callback_status` tinyint NOT NULL DEFAULT 0 COMMENT '回调状态(-1：回调失败；0：未回调；1：回调成功)',
  `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '渠道编码',
  `channel_video_id` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '渠道视频ID',
  `error` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误详情（JSON格式，包含code/message等）',
  `cuid` bigint NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `cu_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `muid` bigint NOT NULL DEFAULT 0 COMMENT '修改人ID',
  `mu_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_job_id`(`video_id` ASC) USING BTREE,
  INDEX `idx_space_code`(`space_code` ASC) USING BTREE,
  INDEX `idx_ak_code`(`ak_code` ASC) USING BTREE,
  INDEX `idx_model`(`model` ASC) USING BTREE,
  INDEX `idx_channel_video_id`(`channel_code` ASC, `channel_video_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_job
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
