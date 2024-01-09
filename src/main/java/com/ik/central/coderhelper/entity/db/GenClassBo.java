package com.ik.central.coderhelper.entity.db;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 生成实体类文件的数据传递实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Accessors(chain = true)
@Data
public class GenClassBo {
  /** Import字符串 */
  private String importStr;
  /** Import字符串(Native) */
  private String importNative;
  /** 实体类Doc */
  private String classDoc;
  /** 实体类表名 */
  private String tableName;
  /** 实体类列数据 */
  private String colData;
}
