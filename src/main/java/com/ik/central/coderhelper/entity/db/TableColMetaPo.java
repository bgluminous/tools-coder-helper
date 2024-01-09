package com.ik.central.coderhelper.entity.db;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据表列元数据实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Accessors(chain = true)
@Data
public class TableColMetaPo {
  /** 列名 */
  private String name;
  /** 列类型 */
  private String type;
  /** 是否可以为NULL */
  private String allowNull;
  /** 是否为主键 */
  private String key;
  /** 默认数据 */
  private String defaultData;
  /** 其他信息 */
  private String extra;
  /** 列注释 */
  private String comment;
}
