# JDK 版本配置指南

## 问题描述

松鼠RPA项目需要特定的JDK版本才能正常构建。项目当前配置为使用 Gradle 7.4.2，该版本与 Java 8 最为兼容。使用较高版本的 JDK 时，可能会出现以下错误：

```
Caused by: org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
General error during conversion: Unsupported class file major version 65

java.lang.IllegalArgumentException: Unsupported class file major version 65
```

## 推荐配置

本项目强烈建议使用以下配置：

- **Java 8** (JDK 1.8)
- **Gradle 7.4.2**
- **Android Gradle Plugin 7.1.3**
- **Kotlin 1.6.10**

## JDK配置步骤

### 1. 检查Java版本

首先确认您的系统中是否安装了Java 8：

```bash
java -version
```

如果输出显示为 `java version "1.8.0_xxx"`，则您已有Java 8。

### 2. 下载安装Java 8（如果需要）

如果您没有安装Java 8，您可以从以下地址下载：

- [AdoptOpenJDK 8](https://adoptium.net/temurin/releases/?version=8)
- [Oracle JDK 8](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)（需要Oracle账号）

### 3. 配置项目使用Java 8

在项目的 `gradle.properties` 文件中，设置 Java 8 的路径：

```properties
# 对于macOS用户
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_xxx.jdk/Contents/Home

# 对于Windows用户
# org.gradle.java.home=C:\\Program Files\\Java\\jdk1.8.0_xxx

# 对于Linux用户
# org.gradle.java.home=/usr/lib/jvm/java-8-openjdk
```

请将路径替换为您实际的Java 8安装位置。

### 4. 使用prepare_env.sh脚本准备环境

项目提供了环境准备脚本，执行以下命令运行：

```bash
chmod +x prepare_env.sh
./prepare_env.sh
```

此脚本将：
- 检查Java版本
- 清理缓存文件
- 预下载Gradle 7.4.2（使用阿里云镜像）

### 5. 验证配置

配置完成后：
1. 重启Android Studio
2. 在Build窗口中查看Gradle输出
3. 确认日志中显示使用的是Java 8版本：
   ```
   当前使用的Java版本: 1.8.0_xxx
   ```

## 常见问题排查

### Java版本错误

如果看到以下警告：

```
警告: 当前项目配置为使用Java 8，但检测到使用的是 xxx
```

请检查：
1. `gradle.properties` 中的 `org.gradle.java.home` 路径是否正确
2. Android Studio的Gradle JDK设置（File > Settings > Build, Execution, Deployment > Build Tools > Gradle）

### Gradle下载失败

如果Gradle下载失败，您可以：
1. 重新运行 `prepare_env.sh` 脚本
2. 手动下载Gradle 7.4.2：
   - 从 [阿里云镜像](https://mirrors.aliyun.com/gradle/gradle-7.4.2-bin.zip) 下载
   - 放置在 `~/.gradle/wrapper/dists/gradle-7.4.2-bin/` 目录下

### 构建缓存问题

如果更新配置后仍遇到错误，可清理缓存：

```bash
rm -rf $HOME/.gradle/caches/
rm -rf .gradle/
rm -rf build/
rm -rf app/build/
```

然后重启Android Studio并选择"File > Invalidate Caches / Restart..."

## 高级用户选项

如果您需要使用不同版本的JDK，请注意：

1. 使用Java 11需要更新Gradle版本至少到7.5+
2. 使用Java 17或更高版本需要Gradle 8.0+和Android Gradle Plugin 8.0+

但请注意，这些变更需要同时修改多个依赖版本，可能导致兼容性问题。强烈建议保持使用推荐的Java 8配置。 