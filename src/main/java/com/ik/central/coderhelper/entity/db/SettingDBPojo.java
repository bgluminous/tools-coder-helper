package com.ik.central.coderhelper.entity.db;

import lombok.Data;

/**
 * 工具设置实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Data
public class SettingDBPojo {
  /** 数据库地址 */
  private String url;
  /** 数据库用户名 */
  private String user;
  /** 数据库密码 */
  private String password;
  /** 数据库名 */
  private String databaseName;

  /** 输出路径 (绝对路径) */
  private String outputPath;
  /** 目标包全名 */
  private String targetPackageFullName;
  /** 枚举类包全名 */
  private String typePackageFullName;

  /** 是否启用lombok */
  private Boolean enableLombok;
  /** 写入模式 all only-new only-update */
  private String writeFileMode;
}
