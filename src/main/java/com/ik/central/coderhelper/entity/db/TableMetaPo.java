package com.ik.central.coderhelper.entity.db;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据表元数据实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Accessors(chain = true)
@Data
public class TableMetaPo {
  /** 表名 */
  private String name;
  /** 表注释 */
  private String comment;
  /** 列元数据 */
  private List<TableColMetaPo> colData;
}
