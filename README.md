# private-coder-helper

## 数据库转Po实体类工具

把数据库的表转换为对应的实体类, 同时包含注释和私有枚举类的支持。

**注意事项:**
> 如果要使用枚举解析 则需要把列 **注释** 写成以下格式  
> ```type VARCHAR(20) NOT NULL COMMENT '类型 XxxxType'```
>
> 请勿在注释中添加其他空格本工具使用空格解析枚举定义 (TODO: 修改枚举标识符)

主入口:

```
  src/main/java/com/ik/central/coderhelper/db/MysqlTableTransformToEntity.java
```

关联文件以及目录结构:

```
  root-package
    |- db (工具主目录)
    |  |- DBConstant.java                  (存放静态变量)
    |  |- MysqlTableTransformToEntity.java (主逻辑和入口)
    |
    |- entity
    |  |- db
    |  |  |- GenClassBo.java               (生成实体类文件的数据传递实体)
    |  |  |- ProcessedColBo.java           (处理后的所有列数据传递实体)
    |  |  |- ProcessedColTypeBo.java       (处理后的列类型数据传递实体)
    |  |  |- SettingDBPojo.java            (本工具设置实体)
    |  |  |- TableColMetaPo.java           (数据表列元数据实体)
    |  |  |- TableMetaPo.java              (数据表元数据实体)
    |  |
    |  |-SettingPojo.java                  (根设置实体)

  resource
    |- config-m24y.yml                     (配置文件)
    |- m24y-template                       (实体类模板文件)
```

配置文件:

转载位置: src/main/resources/config-m24y.yml

```yaml
db:
  # 数据库地址(指定到数据库名) 例:  jdbc:mysql://10.0.0.69:9000/demo
  url:
  # 数据库用户名(最好使用ROOT, 使用其他用户需要有访问 information_schema 的权限)
  user:
  # 数据库密码
  password:
  # 数据库名
  databaseName:
  # 输出路径 (绝对路径) 例: D:\workspace\project\src\main\java\com\ex\group\project1\entity\po
  outputPath:
  # 目标包全名 例: com.ex.group.project1.entity.po
  targetPackageFullName:
  # 枚举类包全名 例: com.ex.group.project1.entity.types
  typePackageFullName:
  # 是否启用lombok (true/false)
  enableLombok:
  # 写入模式 all(重写所有实体类) only-new(只写入新增的表【不包括字段变化】) only-update(只更新已存在的实体类)
  writeFileMode:

```

