package com.ik.central.coderhelper.entity.db;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 处理后的列类型数据传递实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Accessors(chain = true)
@Data
public class ProcessedColTypeBo {
  /** 需要添加的Import */
  private String addImportStr;
  /** 需要添加的 Native Import */
  private String addImportNativeStr;
  /** 类型 */
  private String typeStr;
}
