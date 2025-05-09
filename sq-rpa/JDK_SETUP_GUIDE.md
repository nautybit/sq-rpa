# JDK 版本配置指南

## 问题描述

松鼠RPA项目在使用不同JDK版本时可能会遇到兼容性问题。原版项目使用 Gradle 7.4.2，该版本与 Java 21 不兼容。当你使用较新版本的 JDK (Java 21) 时，会出现以下错误：

```
Caused by: org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
General error during conversion: Unsupported class file major version 65

java.lang.IllegalArgumentException: Unsupported class file major version 65
```

## 解决方案

我们提供了以下方案来解决这些问题（按推荐顺序排列）：

### 方案1：升级到Gradle 8.5（推荐）

我们已经将项目配置升级为支持Java 21：
- Gradle 8.5
- Android Gradle Plugin 8.0.0
- 使用Android Studio内置的JDK

操作步骤：
1. 确保使用最新的项目代码（已更新gradle-wrapper.properties和build.gradle）
2. 在Android Studio中，确保使用内置JDK（不要手动指定JDK路径）
3. 如果遇到Gradle下载问题，运行项目根目录下的`fix_gradle_download.sh`脚本：
   ```bash
   chmod +x fix_gradle_download.sh
   ./fix_gradle_download.sh
   ```

### 方案2：降级到Java 8或Java 11

如果你希望使用原始的Gradle 7.4.2版本，可以：
1. 修改`gradle/wrapper/gradle-wrapper.properties`文件，将distributionUrl改为：
   ```
   distributionUrl=https\://services.gradle.org/distributions/gradle-7.4.2-bin.zip
   ```
2. 修改`build.gradle`文件，将Android Gradle Plugin版本改为7.2.2：
   ```
   classpath 'com.android.tools.build:gradle:7.2.2'
   ```
3. 在`gradle.properties`中指定Java 8或Java 11的路径：
   ```
   org.gradle.java.home=/path/to/your/java8or11
   ```

### 方案3：配置JDK路径

如果你需要指定特定的JDK版本，可以在`gradle.properties`中设置：

对于Java 8（与Gradle 7.4.2一起使用）：
```properties
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_xxx.jdk/Contents/Home
```

对于Java 17（与Gradle 8.5一起使用）：
```properties
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

## 验证配置

配置完成后：
1. 重启Android Studio
2. 在Build窗口中查看Gradle输出
3. 确认使用的是正确的Java版本

## 缓存清理（如遇问题）

如果更新配置后仍然遇到错误，请尝试清理缓存：

1. 关闭Android Studio
2. 删除Gradle缓存：
   ```bash
   rm -rf $HOME/.gradle/caches/
   rm -rf $HOME/.gradle/wrapper/dists/
   ```
3. 删除项目构建文件：
   ```bash
   rm -rf .gradle/
   rm -rf build/
   rm -rf app/build/
   ```
4. 重启Android Studio并选择"File > Invalidate Caches / Restart..."

## 项目兼容性说明

当前项目配置：
- 支持Java 17-21 + Gradle 8.5 + AGP 8.0.0
- 代码使用Java 8兼容的语法特性
- UI使用Jetpack Compose 1.3.0

如有任何问题，请提交issue或参考[Android Gradle兼容性矩阵](https://developer.android.com/studio/releases/gradle-plugin#compatibility)。 