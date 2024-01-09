package com.ik.central.coderhelper.entity;

import com.ik.central.coderhelper.entity.db.SettingDBPojo;
import lombok.Data;

/**
 * 根设置实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Data
public class SettingPojo {
  /** 数据库相关设置 */
  private SettingDBPojo db;
}
