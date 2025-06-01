/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80200
 Source Host           : localhost:3306
 Source Schema         : qihang-oms

 Target Server Type    : MySQL
 Target Server Version : 80200
 File Encoding         : 65001

 Date: 01/06/2025 14:23:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for o_shop_platform
-- ----------------------------
DROP TABLE IF EXISTS `o_shop_platform`;
CREATE TABLE `o_shop_platform`  (
  `id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台名',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台编码',
  `app_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台回调uri',
  `server_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口访问地址',
  `status` int NULL DEFAULT NULL COMMENT '状态（0启用1关闭）',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `region_id` bigint NULL DEFAULT NULL COMMENT '国家/地区',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺平台配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of o_shop_platform
-- ----------------------------
INSERT INTO `o_shop_platform` VALUES (100, '淘宝天猫', 'TMALL', '', NULL, 'http://www.qihangerp.cn', 'http://gw.api.taobao.com/router/rest', 0, 0, 1);
INSERT INTO `o_shop_platform` VALUES (200, '京东POP', 'JD-POP', '', NULL, 'http://www.qihangerp.cn', 'https://api.jd.com/routerjson', 0, 0, 1);
INSERT INTO `o_shop_platform` VALUES (300, '拼多多', 'PDD', '', NULL, 'http://www.qihangerp.cn', 'https://gw-api.pinduoduo.com/api/router', 0, 0, 1);
INSERT INTO `o_shop_platform` VALUES (400, '抖店', 'DOUDIAN', '7496863701597783592', '999b47a2-4991-48ba-b23f-9bc30172a6b3', 'http://www.qihangerp.cn', 'https://openapi-fxg.jinritemai.com/', 0, 0, 1);
INSERT INTO `o_shop_platform` VALUES (500, '微信小店', 'WEISHOP', '', NULL, 'http://www.qihangerp.cn', 'https://api.weixin.qq.com', 0, 0, 1);
INSERT INTO `o_shop_platform` VALUES (999, '线下渠道', 'OFFLINE', NULL, NULL, NULL, NULL, 0, 0, NULL);

-- ----------------------------
-- Table structure for o_shop_region
-- ----------------------------
DROP TABLE IF EXISTS `o_shop_region`;
CREATE TABLE `o_shop_region`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区名称',
  `exchange_rate` float NULL DEFAULT NULL COMMENT '汇率',
  `num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区编码',
  `status` int NULL DEFAULT NULL COMMENT '状态0正常1禁用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺地区表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of o_shop_region
-- ----------------------------
INSERT INTO `o_shop_region` VALUES (1, '中国', 1, '86', 0, '2025-02-10 10:42:54', 'system', '2025-02-10 10:42:57', NULL);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '系统名称', 'sys.name', '启航电商ERP系统', 'Y', 'admin', '2023-08-07 19:31:38', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'Y', 'admin', '2023-08-07 19:31:38', '', NULL, '是否开启验证码功能（true开启，false关闭）');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 200 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '启航技术', 0, '老齐', '15888888888', '280645618@qq.com', '0', '0', 'admin', '2023-08-07 19:31:37', 'admin', '2024-09-15 17:52:12');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '研发中心', 1, '老齐', '15888888888', '280645618@qq.com', '0', '0', 'admin', '2023-08-07 19:31:37', 'admin', '2024-09-15 17:52:47');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '市场中心', 2, '方', '15888888888', 'market@qihangerp.cn', '0', '0', 'admin', '2023-08-07 19:31:37', 'admin', '2024-09-15 17:53:28');
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '至简', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2023-08-07 19:31:37', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '停用状态');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2023-08-07 19:31:38', '', NULL, '登录状态列表');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '订单管理', 0, 10, '/order', 'Layout', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', '2023-12-27 15:00:27', 'admin', '2024-08-25 15:45:43', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '售后管理', 0, 30, '/refund', 'Layout', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2023-12-27 15:00:27', 'admin', '2024-08-25 15:45:54', '至简官网地址');
INSERT INTO `sys_menu` VALUES (3, '店铺设置', 0, 9, 'shop', 'Layout', '', 1, 0, 'M', '0', '0', '', 'dict', 'admin', '2023-12-29 13:29:44', 'admin', '2025-05-19 14:12:51', '');
INSERT INTO `sys_menu` VALUES (4, '商品库管理', 0, 50, 'goods', 'Layout', '', 1, 0, 'M', '0', '0', '', 'international', 'admin', '2023-12-29 16:53:03', 'admin', '2025-05-19 14:06:53', '');
INSERT INTO `sys_menu` VALUES (5, '系统设置', 0, 99, '/system', 'Layout', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2023-12-27 15:00:27', 'admin', '2023-12-29 09:07:42.856856', '系统管理目录');
INSERT INTO `sys_menu` VALUES (100, '订单库', 1, 1, 'order_list', 'order/index', '', 1, 0, 'C', '0', '0', '', 'shopping', 'admin', '2023-12-27 15:00:27', 'admin', '2024-09-15 16:57:59', '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '订单拉取日志', 1, 32, 'order_pull_logs', 'order/shopOrder/pull_log', '', 1, 0, 'C', '0', '0', '', 'documentation', 'admin', '2023-12-27 15:00:27', 'admin', '2025-05-19 14:11:59', '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '店铺订单管理', 1, 3, 'shop_order_list', 'order/shopOrder/index', '', 1, 0, 'C', '0', '0', '', 'monitor', 'admin', '2023-12-27 15:00:27', 'admin', '2024-04-06 11:18:00', '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (104, '售后中心', 2, 1, 'refund_list', 'refund/index', '', 1, 0, 'C', '0', '0', '', 'tree', 'admin', '2023-12-27 15:00:27', 'admin', '2024-09-15 18:58:16', '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '售后拉取日志', 2, 36, 'refund_pull_logs', 'refund/shopRefund/pull_log', '', 1, 0, 'C', '0', '0', '', 'dict', 'admin', '2023-12-27 15:00:27', 'admin', '2025-05-19 14:12:28', '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '店铺售后管理', 2, 2, 'shop_refund_list', 'refund/shopRefund/index', '', 1, 0, 'C', '0', '0', '', 'edit', 'admin', '2023-12-27 15:00:27', 'admin', '2024-07-20 11:56:11', '参数设置菜单');
INSERT INTO `sys_menu` VALUES (108, '店铺管理', 3, 1, 'shop_list', 'shop/index', '', 1, 0, 'C', '0', '0', '', 'tree', 'admin', '2023-12-29 09:14:02', 'admin', '2025-03-24 13:03:00', '');
INSERT INTO `sys_menu` VALUES (110, '平台开关', 3, 81, 'platform/setting', 'shop/platform/index', '', 1, 0, 'C', '0', '0', '', 'system', 'admin', '2023-12-29 13:32:41', 'admin', '2025-05-20 20:36:38', '');
INSERT INTO `sys_menu` VALUES (112, '商品库SKU明细', 4, 10, 'sku_list', 'goods/spec/index', '', 1, 0, 'C', '0', '0', '', 'tree', 'admin', '2023-12-29 16:35:55', 'admin', '2024-09-08 16:14:25', '');
INSERT INTO `sys_menu` VALUES (116, '用户管理', 5, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', '', 'user', 'admin', '2023-12-27 15:00:27', '', '', '用户管理菜单');
INSERT INTO `sys_menu` VALUES (117, '菜单管理', 5, 1, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', '', 'user', 'admin', '2023-12-27 15:00:27', '', '', '用户管理菜单');
INSERT INTO `sys_menu` VALUES (2077, '店铺商品管理', 3, 21, 'shop_goods', 'shop/goods/index', NULL, 1, 0, 'C', '0', '0', '', 'shopping', 'admin', '2024-03-28 10:29:59', 'admin', '2025-03-24 12:37:43', '');
INSERT INTO `sys_menu` VALUES (2078, '添加ERP商品', 4, 99, 'create', 'goods/create', NULL, 1, 0, 'C', '1', '0', '', 'checkbox', 'admin', '2024-03-18 07:59:57', 'admin', '2024-09-07 16:41:46', '');
INSERT INTO `sys_menu` VALUES (2079, '字典管理', 5, 9, 'dict', 'system/dict/index', NULL, 1, 0, 'C', '0', '0', '', 'dict', 'admin', '2024-03-18 08:43:55', 'admin', '2024-03-18 08:44:08', '');
INSERT INTO `sys_menu` VALUES (2084, '电子面单设置', 3, 22, 'ewaybill_account', 'shop/ewaybillAccount/index', NULL, 1, 0, 'C', '0', '0', '', 'code', 'admin', '2024-03-21 20:05:09', 'admin', '2025-05-19 14:17:30', '');
INSERT INTO `sys_menu` VALUES (2086, '定时任务配置', 3, 80, 'task_list', 'shop/task/index', NULL, 1, 0, 'C', '0', '0', '', 'time-range', 'admin', '2024-03-22 19:29:20', 'admin', '2025-05-19 14:24:21', '');
INSERT INTO `sys_menu` VALUES (2087, '发货管理', 0, 20, 'ship', NULL, NULL, 1, 0, 'M', '0', '0', '', 'guide', 'admin', '2024-03-30 17:36:10', 'admin', '2024-08-25 15:45:48', '');
INSERT INTO `sys_menu` VALUES (2088, '发货设置', 2087, 9, 'ship_logistics', 'shipping/logistics/index', NULL, 1, 0, 'C', '0', '0', '', 'checkbox', 'admin', '2024-03-30 17:37:01', 'admin', '2025-05-23 20:07:04', '');
INSERT INTO `sys_menu` VALUES (2089, '发货记录', 2087, 3, 'record', 'shipping/index', NULL, 1, 0, 'C', '0', '0', '', 'guide', 'admin', '2024-03-30 17:37:42', 'admin', '2024-07-28 18:56:02', '');
INSERT INTO `sys_menu` VALUES (2090, '角色管理', 5, 2, 'role', 'system/role/index', NULL, 1, 0, 'C', '0', '0', NULL, 'peoples', 'admin', '2024-03-31 12:40:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2091, '部门管理', 5, 3, 'dept', 'system/dept/index', NULL, 1, 0, 'C', '0', '0', NULL, 'tree', 'admin', '2024-03-31 12:42:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2092, '售后处理记录', 2, 5, 'processing', 'afterSale/index', NULL, 1, 0, 'C', '0', '0', '', 'documentation', 'admin', '2024-04-06 17:27:03', 'admin', '2024-07-28 18:59:41', '');
INSERT INTO `sys_menu` VALUES (2093, '订单明细', 1, 2, 'order_item_list', 'order/item_list', NULL, 1, 0, 'C', '0', '0', NULL, 'chart', 'admin', '2024-04-06 18:58:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2094, '取号&发货', 2087, 1, 'print', 'shipping/ewaybillPrint/index', NULL, 1, 0, 'C', '0', '0', '', 'edit', 'admin', '2024-07-20 11:04:54', 'admin', '2025-05-17 20:17:32', '');
INSERT INTO `sys_menu` VALUES (2096, '备货清单', 2087, 2, 'stockup', 'shipping/stockup', NULL, 1, 0, 'C', '0', '0', '', 'email', 'admin', '2024-07-20 11:53:24', 'admin', '2024-07-28 20:44:10', '');
INSERT INTO `sys_menu` VALUES (2097, '物流跟踪', 2087, 5, 'logistics', 'shipping/stocking/index', NULL, 1, 0, 'C', '0', '1', '', 'list', 'admin', '2024-07-20 11:54:18', 'admin', '2025-05-25 14:32:27', '');
INSERT INTO `sys_menu` VALUES (2099, '私域订单管理', 1, 10, 'offline_order_list', 'order/private/index', NULL, 1, 0, 'C', '0', '1', '', 'checkbox', 'admin', '2024-07-27 17:33:03', 'admin', '2025-05-24 13:10:53', '');
INSERT INTO `sys_menu` VALUES (2100, '私域售后管理', 2, 3, 'offline_aftersale', 'refund/private/index', NULL, 1, 0, 'C', '0', '1', '', 'code', 'admin', '2024-07-27 17:33:33', 'admin', '2025-05-25 14:59:03', '');
INSERT INTO `sys_menu` VALUES (2101, '渠道商品管理', 4, 50, 'offline_goods_list', 'offline/goods/index', NULL, 1, 0, 'C', '0', '1', '', 'documentation', 'admin', '2024-07-27 17:33:54', 'admin', '2024-09-07 23:17:59', '');
INSERT INTO `sys_menu` VALUES (2103, '手动创建私域订单', 1, 49, 'offline_order_create', 'order/private/create', NULL, 1, 0, 'C', '1', '0', '', 'date', 'admin', '2024-07-27 20:30:07', 'admin', '2025-03-24 11:46:51', '');
INSERT INTO `sys_menu` VALUES (2104, '商品库管理', 4, 0, 'goods_list', 'goods/index', NULL, 1, 0, 'C', '0', '0', 'goods', 'example', 'admin', '2024-08-25 14:35:54', 'admin', '2024-09-08 16:14:12', '');
INSERT INTO `sys_menu` VALUES (2105, '库存管理', 0, 40, 'stock', NULL, NULL, 1, 0, 'M', '0', '0', '', 'lock', 'admin', '2024-08-25 15:54:14', 'admin', '2025-03-24 13:32:20', '');
INSERT INTO `sys_menu` VALUES (2106, '商品入库管理', 2105, 10, 'stock_in', 'stock/stockIn/index.vue', NULL, 1, 0, 'C', '0', '0', '', 'download', 'admin', '2024-08-25 15:56:04', 'admin', '2025-03-24 13:35:21', '');
INSERT INTO `sys_menu` VALUES (2108, '供应商管理', 4, 90, 'supplier_list', 'goods/supplier/index', NULL, 1, 0, 'C', '0', '0', '', 'people', 'admin', '2024-08-25 18:27:55', 'admin', '2024-09-07 15:51:17', '');
INSERT INTO `sys_menu` VALUES (2109, '商品分类管理', 4, 80, 'category_list', 'goods/category/index', NULL, 1, 0, 'C', '0', '0', '', 'edit', 'admin', '2024-08-25 18:43:28', 'admin', '2024-09-07 15:47:44', '');
INSERT INTO `sys_menu` VALUES (2110, '商品品牌管理', 4, 81, 'brand_list', 'goods/brand/index', NULL, 1, 0, 'C', '0', '0', '', 'icon', 'admin', '2024-08-25 18:45:47', 'admin', '2024-09-07 15:48:31', '');
INSERT INTO `sys_menu` VALUES (2111, '分类规格属性', 4, 101, 'goods_category/attribute', 'goods/category/categoryAttribute', NULL, 1, 0, 'C', '1', '0', '', 'button', 'admin', '2024-08-25 18:49:22', 'admin', '2024-09-07 16:17:01', '');
INSERT INTO `sys_menu` VALUES (2112, '规格属性值', 4, 102, 'goods_category/attribute_value', 'goods/category/categoryAttributeValue', NULL, 1, 0, 'C', '1', '0', '', 'date', 'admin', '2024-08-25 18:51:55', 'admin', '2024-09-07 16:23:53', '');
INSERT INTO `sys_menu` VALUES (2114, '仓库仓位设置', 2105, 90, 'warehouse', 'stock/warehouse/index.vue', NULL, 1, 0, 'C', '0', '0', '', 'cascader', 'admin', '2024-09-21 20:07:26', 'admin', '2025-03-24 13:46:52', '');
INSERT INTO `sys_menu` VALUES (2115, '商品库存管理', 2105, 0, 'goods_inventory', 'stock/goodsInventory/index.vue', NULL, 1, 0, 'C', '0', '0', '', 'chart', 'admin', '2024-09-21 20:43:00', 'admin', '2025-03-24 13:34:55', '');
INSERT INTO `sys_menu` VALUES (2116, '商品出库管理', 2105, 20, 'stock_out', 'stock/stockOut/index', NULL, 1, 0, 'C', '0', '0', '', 'guide', 'admin', '2024-09-21 20:44:46', 'admin', '2025-03-24 13:46:42', '');
INSERT INTO `sys_menu` VALUES (2117, '仓位管理', 2105, 91, 'position', 'stock/warehouse/position', NULL, 1, 0, 'C', '1', '0', '', '404', 'admin', '2024-09-22 11:52:18', 'admin', '2025-03-24 13:47:04', '');
INSERT INTO `sys_menu` VALUES (2118, '新建商品入库单', 2105, 11, 'stock_in/create', 'stock/stockIn/create.vue', NULL, 1, 0, 'C', '1', '0', '', '404', 'admin', '2024-09-22 14:49:40', 'admin', '2025-03-24 13:35:30', '');
INSERT INTO `sys_menu` VALUES (2129, '发货&分配发货', 2087, 0, 'manual_shipment', 'shipping/shipment/index', NULL, 1, 0, 'C', '0', '0', '', 'checkbox', 'admin', '2025-06-01 13:36:57', 'admin', '2025-06-01 13:39:59', '');

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`  (
  `oss_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件id',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件名',
  `original_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '原名',
  `file_suffix` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件后缀名',
  `url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'URL地址',
  `object_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '对象名',
  `bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '桶名',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`oss_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oss
-- ----------------------------
INSERT INTO `sys_oss` VALUES (1, '主图画板 1.jpg', '主图画板 1.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/09/07/a3e935aa-d1b3-4524-bd84-e567df01f2e3.jpg', '/images/2024/09/07/a3e935aa-d1b3-4524-bd84-e567df01f2e3.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 17:38:57', '', NULL);
INSERT INTO `sys_oss` VALUES (2, '主图画板2.jpg', '主图画板2.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/09/07/1dbe1530-787f-4461-9f20-3efa3c332588.jpg', '/images/2024/09/07/1dbe1530-787f-4461-9f20-3efa3c332588.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 17:57:54', '', NULL);
INSERT INTO `sys_oss` VALUES (3, '主图画板5.jpg', '主图画板5.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/09/07/2dc06b89-8661-4a72-a7cd-7f85f4545c6e.jpg', '/images/2024/09/07/2dc06b89-8661-4a72-a7cd-7f85f4545c6e.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 18:03:24', '', NULL);
INSERT INTO `sys_oss` VALUES (4, '主图画板 4.jpg', '主图画板 4.jpg', '.jpg', 'http://127.0.0.1:9000/ecerp//images/2024/09/07/3ed00452-5973-4c7e-91ae-5e87aa32dd23.jpg', '/images/2024/09/07/3ed00452-5973-4c7e-91ae-5e87aa32dd23.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 18:03:46', '', NULL);
INSERT INTO `sys_oss` VALUES (5, '主图画板5.jpg', '主图画板5.jpg', '.jpg', 'http://127.0.0.1:9000/ecerp/images/2024/09/07/4b2583ae-65ae-45b6-9227-ebbee99d6558.jpg', '/images/2024/09/07/4b2583ae-65ae-45b6-9227-ebbee99d6558.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 18:04:47', '', NULL);
INSERT INTO `sys_oss` VALUES (6, '主图画板2.jpg', '主图画板2.jpg', '.jpg', 'http://223.83.100.30:9001/omsimage/images/2024/09/07/a64ff14b-09d4-42d6-8d52-0344360efdbf.jpg', '/images/2024/09/07/a64ff14b-09d4-42d6-8d52-0344360efdbf.jpg', 'omsimage', 0, '0', '0', '', '2024-09-07 19:10:39', '', NULL);
INSERT INTO `sys_oss` VALUES (7, '主图画板 1.jpg', '主图画板 1.jpg', '.jpg', 'http://223.83.100.30:9001/omsimage/images/2024/09/07/af19385d-a1dd-4efb-8257-3099706d1272.jpg', '/images/2024/09/07/af19385d-a1dd-4efb-8257-3099706d1272.jpg', 'omsimage', 0, '0', '0', '', '2024-09-07 19:26:36', '', NULL);
INSERT INTO `sys_oss` VALUES (8, '主图画板 1.jpg', '主图画板 1.jpg', '.jpg', 'http://223.83.100.30:9001/omsimage/images/2024/09/07/5aac40e1-ddc1-41a8-a468-28d81e699980.jpg', '/images/2024/09/07/5aac40e1-ddc1-41a8-a468-28d81e699980.jpg', 'omsimage', 0, '0', '0', '', '2024-09-07 20:37:14', '', NULL);
INSERT INTO `sys_oss` VALUES (9, '主图画板 1.jpg', '主图画板 1.jpg', '.jpg', 'http://223.83.100.30:9001/omsimage/images/2024/09/07/b28f4e15-8997-418c-bd32-b4beb4966f6b.jpg', '/images/2024/09/07/b28f4e15-8997-418c-bd32-b4beb4966f6b.jpg', 'omsimage', 0, '0', '0', '', '2024-09-07 20:37:51', '', NULL);
INSERT INTO `sys_oss` VALUES (50, 'x4.jpg', 'x4.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/08/28/x4.jpg', '/images/2024/08/28/x4.jpg', 'ecerp', 0, '0', '0', '', '2024-08-28 22:39:05', '', NULL);
INSERT INTO `sys_oss` VALUES (51, 'x11.jpg', 'x11.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/08/28/x11.jpg', '/images/2024/08/28/x11.jpg', 'ecerp', 0, '0', '0', '', '2024-08-28 22:47:11', '', NULL);
INSERT INTO `sys_oss` VALUES (52, '主图画板 4.jpg', '主图画板 4.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/09/07/主图画板 4.jpg', '/images/2024/09/07/主图画板 4.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 17:31:44', '', NULL);
INSERT INTO `sys_oss` VALUES (53, '主图画板 4.jpg', '主图画板 4.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/09/07/主图画板 4.jpg', '/images/2024/09/07/主图画板 4.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 17:35:41', '', NULL);
INSERT INTO `sys_oss` VALUES (54, '主图画板 1.jpg', '主图画板 1.jpg', '.jpg', 'http://127.0.0.1:9000/images/2024/09/07/f35a3cb8-1b9e-41f4-8158-31d458c7efb3.jpg', '/images/2024/09/07/f35a3cb8-1b9e-41f4-8158-31d458c7efb3.jpg', 'ecerp', 0, '0', '0', '', '2024-09-07 17:36:09', '', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2023-08-07 19:31:37', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2023-08-07 19:31:37', 'admin', '2025-03-02 15:55:27', '普通角色');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 2078);
INSERT INTO `sys_role_menu` VALUES (2, 2084);
INSERT INTO `sys_role_menu` VALUES (2, 2086);
INSERT INTO `sys_role_menu` VALUES (2, 2087);
INSERT INTO `sys_role_menu` VALUES (2, 2088);
INSERT INTO `sys_role_menu` VALUES (2, 2089);
INSERT INTO `sys_role_menu` VALUES (2, 2092);
INSERT INTO `sys_role_menu` VALUES (2, 2093);
INSERT INTO `sys_role_menu` VALUES (2, 2094);
INSERT INTO `sys_role_menu` VALUES (2, 2096);
INSERT INTO `sys_role_menu` VALUES (2, 2097);
INSERT INTO `sys_role_menu` VALUES (2, 2099);
INSERT INTO `sys_role_menu` VALUES (2, 2100);
INSERT INTO `sys_role_menu` VALUES (2, 2101);
INSERT INTO `sys_role_menu` VALUES (2, 2103);
INSERT INTO `sys_role_menu` VALUES (2, 2104);
INSERT INTO `sys_role_menu` VALUES (2, 2108);
INSERT INTO `sys_role_menu` VALUES (2, 2109);
INSERT INTO `sys_role_menu` VALUES (2, 2110);
INSERT INTO `sys_role_menu` VALUES (2, 2111);
INSERT INTO `sys_role_menu` VALUES (2, 2112);

-- ----------------------------
-- Table structure for sys_task
-- ----------------------------
DROP TABLE IF EXISTS `sys_task`;
CREATE TABLE `sys_task`  (
  `id` int NOT NULL,
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `cron` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台任务配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_task
-- ----------------------------
INSERT INTO `sys_task` VALUES (1, '拉取淘宝订单', '-', NULL, '定时更新淘宝订单', '2024-03-07 09:52:40');
INSERT INTO `sys_task` VALUES (2, '拉取京东POP订单', '-', NULL, '拉取京东POP订单', '2024-03-07 09:23:36');
INSERT INTO `sys_task` VALUES (3, '拉取拼多多订单', '-', NULL, '定时拉取拼多多订单', '2024-04-09 11:24:14');
INSERT INTO `sys_task` VALUES (4, '拉取抖店订单', '-', NULL, '定时拉取抖店订单', '2024-04-09 11:24:54');
INSERT INTO `sys_task` VALUES (5, '拉取天猫退款', '-', NULL, '定时拉取天猫退款', '2024-04-09 11:25:43');
INSERT INTO `sys_task` VALUES (6, '拉取京东售后', '-', NULL, '定时拉取京东售后', '2024-04-09 11:26:26');
INSERT INTO `sys_task` VALUES (7, '拉取拼多多退款', '-', NULL, '定时拉取拼多多退款', '2024-04-09 11:27:01');
INSERT INTO `sys_task` VALUES (8, '拉取抖店退款', '-', NULL, '定时拉取抖店退款', '2024-04-09 11:27:38');
INSERT INTO `sys_task` VALUES (11, '拉取京东自营订单', '-', NULL, '拉取京东自营订单', '2024-05-27 10:57:44');
INSERT INTO `sys_task` VALUES (12, '拉取京东自营退货', '-', NULL, '拉取京东自营退货', NULL);
INSERT INTO `sys_task` VALUES (21, '推送待发货订单到ERP', '-', NULL, '推送待发货订单到ERP', '2024-04-22 15:48:48');
INSERT INTO `sys_task` VALUES (22, '推送待处理售后到ERP', '-', NULL, '推送待处理售后到ERP', '2024-04-22 15:48:48');
INSERT INTO `sys_task` VALUES (23, '推送已取消的订单到ERP', '-', NULL, '推送已取消的订单到ERP', '2024-05-29 17:57:02');

-- ----------------------------
-- Table structure for sys_task_logs
-- ----------------------------
DROP TABLE IF EXISTS `sys_task_logs`;
CREATE TABLE `sys_task_logs`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `task_id` int NULL DEFAULT NULL COMMENT '任务ID',
  `result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始运行时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` int NULL DEFAULT NULL COMMENT '状态1运行中，2已结束',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台任务运行日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_task_logs
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, 'admin', '启航老齐A', '00', '280645618@qq.com', '18123879144', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-06-01 14:21:31', 'admin', '2023-08-07 19:31:37', '', '2025-06-01 14:21:31', '管理员');
INSERT INTO `sys_user` VALUES (2, NULL, 'openapi', 'openApi接口专用', '00', '2806456181@qq.com', '15818590000', '0', '', '$2a$10$fHkhoqbMiyracAsTzl38H.55bu.M.of1FXk2EK7RQBjfic3tLU0Ue', '0', '0', '127.0.0.1', '2024-06-24 10:23:35', 'admin', '2024-03-17 14:55:22', 'admin', '2024-06-24 10:23:35', NULL);
INSERT INTO `sys_user` VALUES (101, 101, '15818590119', 'aaa123', '00', '', '', '0', '', '$2a$10$pXcT6cHaObMeKuYd9vZb5uEb8PyUdF2AcqqRN1cBqiA9rV4qYQW7G', '0', '2', '', NULL, 'admin', '2024-08-15 13:45:25', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (102, 101, '15818590119', '老齐', '00', '', '', '0', '', '$2a$10$ysk.zgJ8wh25c7vOjKyZ8uarM2hkG0S51j8GYdJSo2kZmc3f8HdKe', '0', '0', '', NULL, 'admin', '2024-08-15 13:49:59', 'admin', '2025-02-10 16:26:20', NULL);
INSERT INTO `sys_user` VALUES (103, 100, '18025303180', '老方', '00', '', '', '0', '', '$2a$10$QnLM3NluG5q1xpmWep0QUOFfvNrd02hwenL4HkV0uhMbm4cEX1uIG', '0', '0', '', NULL, 'admin', '2024-09-21 17:17:56', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (102, 2);
INSERT INTO `sys_user_role` VALUES (103, 2);

SET FOREIGN_KEY_CHECKS = 1;
