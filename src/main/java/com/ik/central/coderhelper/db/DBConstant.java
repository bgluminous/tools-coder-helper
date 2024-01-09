package com.ik.central.coderhelper.db;

import java.util.HashMap;
import java.util.Map;

/**
 * 存放静态变量包 括类型转换Map/预设的Import/查询用的SQL
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
public class DBConstant {

  private DBConstant() {
  }

  /** 数据库类型转换Map */
  protected static final Map<String, String> COL_TYPE_TRANSFORM_MAP = new HashMap<>();

  static {
    String intStr = "Integer";
    String stringStr = "String";
    String byteArrStr = "byte[]";

    COL_TYPE_TRANSFORM_MAP.put("tinyint", "Boolean");

    COL_TYPE_TRANSFORM_MAP.put("smallint", intStr);
    COL_TYPE_TRANSFORM_MAP.put("mediumint", intStr);
    COL_TYPE_TRANSFORM_MAP.put("int", intStr);

    COL_TYPE_TRANSFORM_MAP.put("bigint", "Long");

    COL_TYPE_TRANSFORM_MAP.put("float", "Float");
    COL_TYPE_TRANSFORM_MAP.put("double", "Double");
    COL_TYPE_TRANSFORM_MAP.put("decimal", "BigDecimal");

    COL_TYPE_TRANSFORM_MAP.put("date", "Date");
    COL_TYPE_TRANSFORM_MAP.put("datetime", "Date");
    COL_TYPE_TRANSFORM_MAP.put("timestamp", "Timestamp");

    COL_TYPE_TRANSFORM_MAP.put("tinytext", stringStr);
    COL_TYPE_TRANSFORM_MAP.put("text", stringStr);
    COL_TYPE_TRANSFORM_MAP.put("mediumtext", stringStr);
    COL_TYPE_TRANSFORM_MAP.put("longtext", stringStr);

    COL_TYPE_TRANSFORM_MAP.put("char", stringStr);
    COL_TYPE_TRANSFORM_MAP.put("varchar", stringStr);

    COL_TYPE_TRANSFORM_MAP.put("enum", stringStr);
    COL_TYPE_TRANSFORM_MAP.put("set", stringStr);
    COL_TYPE_TRANSFORM_MAP.put("json", stringStr);

    COL_TYPE_TRANSFORM_MAP.put("binary", byteArrStr);
    COL_TYPE_TRANSFORM_MAP.put("varbinary", byteArrStr);
    COL_TYPE_TRANSFORM_MAP.put("blob", byteArrStr);
    COL_TYPE_TRANSFORM_MAP.put("tinyblob", byteArrStr);
    COL_TYPE_TRANSFORM_MAP.put("mediumblob", byteArrStr);
    COL_TYPE_TRANSFORM_MAP.put("longblob", byteArrStr);
    COL_TYPE_TRANSFORM_MAP.put("bit", byteArrStr);
  }

  public static final String LOMBOK_ENABLED_ANNO = """
    @Accessors(chain = true)
    @Data
    """;

  public static class Import {

    // 不要换行这个lombok的注解
    public static final String LOMBOK_ENABLED = """
      import lombok.Data;
      import lombok.experimental.Accessors;""";

    public static final String TIMESTAMP = """
      import java.sql.Timestamp;
      """;

    public static final String DATE = """
      import import java.util.Date;
      """;

    public static final String BIG_DECIMAL = """
      import java.math.BigDecimal;
      """;

    private Import() {
    }
  }

  public static class SQL {

    public static final String GET_ALL_TABLE_META = """
      SELECT
        TABLE_COMMENT, TABLE_NAME
      FROM
        information_schema.TABLES
      WHERE
        TABLE_SCHEMA = ?;
      """;

    public static final String GET_COL_META = "DESCRIBE ";

    public static final String GET_COL_COMMENT = """
      SELECT
        COLUMN_NAME, COLUMN_COMMENT
      FROM
        information_schema.COLUMNS
      WHERE
        TABLE_NAME = ?
      AND
        TABLE_SCHEMA = ?;
      """;

    private SQL() {
    }

  }
}
