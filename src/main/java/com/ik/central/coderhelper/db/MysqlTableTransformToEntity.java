package com.ik.central.coderhelper.db;

import com.heavenark.infrastructure.log.LogFactory;
import com.heavenark.infrastructure.log.Logger;
import com.ik.central.coderhelper.entity.SettingPojo;
import com.ik.central.coderhelper.entity.db.*;
import com.ik.central.coderhelper.exception.CoderHelperException;
import ink.ik.tools.toys.IkToyString;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 把数据库的表转换为对应的实体类, 同时包含注释和私有枚举类的支持。
 *
 * @author BGLuminous
 * @since 1.0.0 2024-01-09
 */
public class MysqlTableTransformToEntity {
  private static final Logger LOGGER = LogFactory.getLogger(MysqlTableTransformToEntity.class);

  /** 配置信息 */
  private static SettingDBPojo settings;
  /** Class 模板 */
  private static String classTemplate;
  /** 数据库连接 */
  private static Connection connection;
  /** 表元数据列表 */
  private static final Map<String, TableMetaPo> tableMetaMap = new HashMap<>();

  /**
   * 主逻辑和入口
   *
   * @param args --
   */
  public static void main(String[] args) {
    // 加载配置
    loadConfig();
    loadClassTemplate();
    // 建立数据库连接
    initConnect();
    // 收集表列表
    collectTableList();
    // 收集表列信息
    collectColData();
    // 写入文件
    processToClass();
  }

  /**
   * 加载配置文件
   */
  private static void loadConfig() {
    try (
      InputStream is =
        MysqlTableTransformToEntity.class.getClassLoader().getResourceAsStream("config-m24y.yml")
    ) {
      if (is == null) {
        LOGGER.err("未找到配置文件!");
        System.exit(1);
      }
      settings = new Yaml().loadAs(is, SettingPojo.class).getDb();
    } catch (Exception ex) {
      LOGGER.err(new CoderHelperException("加载配置文件错误! 错误信息: %s".formatted(ex.getMessage())));
      System.exit(1);
    }
  }

  /**
   * 加载模板文件
   */
  private static void loadClassTemplate() {
    try (
      InputStream is =
        MysqlTableTransformToEntity.class.getClassLoader().getResourceAsStream("m24y-template")
    ) {
      if (is == null) {
        LOGGER.err("未找到模板文件!");
        System.exit(1);
      }

      StringBuilder contentBuilder = new StringBuilder();
      String line;
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      while ((line = reader.readLine()) != null) {
        contentBuilder.append(line);
        contentBuilder.append("\n");
      }
      classTemplate = contentBuilder.toString();
    } catch (Exception ex) {
      LOGGER.err(new CoderHelperException("加载模板文件错误! 错误信息: %s".formatted(ex.getMessage())));
      System.exit(1);
    }
  }

  /**
   * 初始化数据连接
   */
  private static void initConnect() {
    try {
      connection = DriverManager.getConnection(
        settings.getUrl(), settings.getUser(), settings.getPassword()
      );
    } catch (Exception ex) {
      LOGGER.err(new CoderHelperException("连接数据库错误! 错误信息: %s".formatted(ex.getMessage())));
      System.exit(1);
    }
    //注册关闭数据库连接的Hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        connection.close();
      } catch (Exception ex) {
        LOGGER.err(new CoderHelperException("关闭数据库连接错误! 错误信息: %s".formatted(ex.getMessage())));
      }
    }));
  }

  /**
   * 收集数据库表信息
   */
  private static void collectTableList() {
    try (
      PreparedStatement statement = connection.prepareStatement(DBConstant.SQL.GET_ALL_TABLE_META)
    ) {
      statement.setString(1, settings.getDatabaseName());
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        TableMetaPo tableMeta = new TableMetaPo()
          .setName(rs.getString("TABLE_NAME"))
          .setComment(rs.getString("TABLE_COMMENT"));
        tableMetaMap.put(rs.getString("TABLE_NAME"), tableMeta);
      }
    } catch (Exception ex) {
      LOGGER.err(new CoderHelperException("获取表列表错误! 错误信息: %s".formatted(ex.getMessage())));
      System.exit(1);
    }
  }

  /**
   * 收集数据表列信息
   */
  private static void collectColData() {
    tableMetaMap.forEach((k, v) -> {
      try (
        PreparedStatement statement =
          connection.prepareStatement(DBConstant.SQL.GET_COL_META + k);
        PreparedStatement statement1 =
          connection.prepareStatement(DBConstant.SQL.GET_COL_COMMENT)
      ) {
        statement1.setString(1, k);
        statement1.setString(2, settings.getDatabaseName());
        ResultSet rs = statement.executeQuery();
        ResultSet rs1 = statement1.executeQuery();
        Map<String, String> commentMap = new HashMap<>();
        while (rs1.next()) {
          commentMap.put(
            rs1.getString("COLUMN_NAME"), rs1.getString("COLUMN_COMMENT")
          );
        }
        List<TableColMetaPo> tableColMetaList = new ArrayList<>();
        while (rs.next()) {
          String colName = rs.getString("Field");
          tableColMetaList.add(
            new TableColMetaPo()
              .setName(colName)
              .setType(rs.getString("Type"))
              .setAllowNull(rs.getString("Null"))
              .setKey(rs.getString("Key"))
              .setDefaultData(rs.getString("Default"))
              .setExtra(rs.getString("Extra"))
              .setComment(commentMap.get(colName))
          );
        }
        v.setColData(tableColMetaList);
      } catch (Exception ex) {
        LOGGER.err(new CoderHelperException("获取表列信息错误! 错误信息: %s".formatted(ex.getMessage())));
        System.exit(1);
      }
    });
  }

  /**
   * 将数据转换为实体类
   */
  private static void processToClass() {
    tableMetaMap.forEach((k, v) -> {
      StringBuilder importStringBuilder = new StringBuilder();
      StringBuilder importNativeStringBuilder = new StringBuilder();
      // 处理列信息
      ProcessedColBo colDataPojo = processColMeta(v.getColData());
      colDataPojo.getAddImportStrSet()
        .forEach(e -> importStringBuilder.append("\n").append(e));
      colDataPojo.getAddImportNativeStrSet()
        .forEach(e -> importNativeStringBuilder.append("\n").append(e));
      // 处理lombok依赖
      if (Boolean.TRUE.equals(settings.getEnableLombok())) {
        importStringBuilder.append("\n").append(DBConstant.Import.LOMBOK_ENABLED);
      }
      // 生成实体类Doc
      String doc = """
        /**
         * %s %s
         */
         """.formatted(k, v.getComment());
      genClass(
        new GenClassBo()
          .setImportStr(importStringBuilder.toString())
          .setImportNative(importNativeStringBuilder.toString())
          .setClassDoc(doc)
          .setTableName(k)
          .setColData(colDataPojo.getColData())
      );
    });
  }

  /**
   * 处理列元数据为实体类变量
   *
   * @param colMetaList 列元数据列表
   *
   * @return --
   */
  private static ProcessedColBo processColMeta(List<TableColMetaPo> colMetaList) {
    ProcessedColBo processedColBo = new ProcessedColBo();
    StringBuilder stringBuilder = new StringBuilder();
    Set<String> addImportStrSet = new HashSet<>();
    Set<String> addImportNativeStrSet = new HashSet<>();
    colMetaList.forEach(e -> {
      //处理类型
      ProcessedColTypeBo processedColTypeBo = processColType(e);
      // 添加需要的 Import
      if (processedColTypeBo.getAddImportStr() != null) {
        addImportStrSet.add(processedColTypeBo.getAddImportStr());
      }
      if (processedColTypeBo.getAddImportNativeStr() != null) {
        addImportNativeStrSet.add(processedColTypeBo.getAddImportNativeStr());
      }
      //生成列内容
      String data = """
          /** %s */
          private %s %s;
        """.formatted(
        e.getComment(),
        processedColTypeBo.getTypeStr(),
        IkToyString.underlineToCamel("_", e.getName(), false)
      );
      stringBuilder.append(data);
    });
    return processedColBo
      .setColData(stringBuilder.toString())
      .setAddImportStrSet(addImportStrSet)
      .setAddImportNativeStrSet(addImportNativeStrSet);
  }

  /**
   * 处理列类型转换为Java类型
   *
   * @param meta 列元数据
   *
   * @return --
   */
  private static ProcessedColTypeBo processColType(TableColMetaPo meta) {
    // 判断是否为Enum
    int commentIncludeEnum = meta.getComment().indexOf(" ");
    if (commentIncludeEnum != -1) {
      String typeClassName = meta.getComment().substring(commentIncludeEnum + 1);
      return new ProcessedColTypeBo()
        .setAddImportStr("import %s.%s;".formatted(settings.getTypePackageFullName(), typeClassName))
        .setTypeStr(typeClassName + " ");
    }
    // 为基础类型
    ProcessedColTypeBo processedColTypeBo = new ProcessedColTypeBo();
    String type = meta.getType().replaceAll("[\\\\(]\\d{0,10}[\\\\)]", "");
    String colTypeStr = DBConstant.COL_TYPE_TRANSFORM_MAP.get(type);
    processedColTypeBo.setTypeStr(colTypeStr);
    switch (colTypeStr) {
      case "Timestamp":
        processedColTypeBo.setAddImportNativeStr(DBConstant.Import.TIMESTAMP);
        break;
      case "Date":
        processedColTypeBo.setAddImportNativeStr(DBConstant.Import.DATE);
        break;
      case "BigDecimal":
        processedColTypeBo.setAddImportNativeStr(DBConstant.Import.BIG_DECIMAL);
        break;
      default:
        // NOTHING TO DO
    }
    return processedColTypeBo;
  }

  /**
   * 通过模板生成实体类
   *
   * @param genClassBo 用于生成实体类的信息
   */
  private static void genClass(GenClassBo genClassBo) {
    String classContent = String.copyValueOf(classTemplate.toCharArray());
    String lombokImportStr =
      Boolean.TRUE.equals(settings.getEnableLombok()) ? DBConstant.LOMBOK_ENABLED_ANNO : "";
    String className =
      IkToyString.underlineToCamel("_", genClassBo.getTableName(), true) + "Po";
    classContent = classContent
      .replace("#package#\n", settings.getTargetPackageFullName() + ";")
      .replace("#import#\n", genClassBo.getImportStr())
      .replace("#importNative#\n", genClassBo.getImportNative())
      .replace("#classDoc#\n", genClassBo.getClassDoc())
      .replace("#lombokAnno#\n", lombokImportStr)
      .replace("#className#", className)
      .replace("#colData#\n", genClassBo.getColData());
    // 写入文件
    saveToFile(classContent, "%s%s.java".formatted(settings.getOutputPath(), className));
  }

  /**
   * 保存到文件
   *
   * @param content  实体类内容
   * @param filePath 实体类路径
   */
  private static void saveToFile(String content, String filePath) {
    LOGGER.inf("开始处理文件: %s".formatted(filePath));
    File file = new File(filePath);
    // 检测文件是否已存在
    if (file.exists()) {
      if (Objects.equals(settings.getWriteFileMode(), "new-only")) {
        LOGGER.war("写入模式为 only-new! 正在跳过: %s".formatted(filePath));
        return;
      }
      try {
        LOGGER.war("写入模式为 all! 正在删除并重写: %s".formatted(filePath));
        Files.delete(Path.of(filePath));
      } catch (Exception ex) {
        LOGGER.err(new CoderHelperException("删除文件[%s]出错!".formatted(filePath)));
      }
    } else {
      // 如果文件不存在
      if (Objects.equals(settings.getWriteFileMode(), "update-only")) {
        LOGGER.war("写入模式为 update-only! 正在跳过: %s".formatted(filePath));
        return;
      }
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      LOGGER.key("写入文件成功! 位置: %s".formatted(filePath));
      writer.write(content);
    } catch (Exception ex) {
      LOGGER.err(new CoderHelperException("写入文件错误! 错误信息: %s".formatted(ex.getMessage())));
    }
  }

}
