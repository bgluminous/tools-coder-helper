package com.ik.central.coderhelper.entity.db;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 处理后的所有列数据传递实体
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
@Accessors(chain = true)
@Data
public class ProcessedColBo {
  /** 需要添加的Import */
  private Set<String> addImportStrSet;
  /** 需要添加的 Native Import */
  private Set<String> addImportNativeStrSet;
  /** 处理后的列内容 */
  private String colData;
}
