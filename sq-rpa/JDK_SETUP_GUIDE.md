# JDK 版本配置指南

## 问题描述

松鼠RPA项目在使用较新的JDK版本（如Java 21）时可能会遇到两类问题：

### 问题1: Gradle版本兼容性

原版项目使用 Gradle 7.4.2，该版本与 Java 21 不兼容。当你使用较新版本的 JDK (Java 21) 时，会出现以下错误：

```
Caused by: org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
Your build is currently configured to use incompatible Java 21.x.x and Gradle 7.4.2. Cannot sync the project.
```

### 问题2: Java模块系统限制

在Java 9+引入模块系统后，Kotlin注解处理器(KAPT)访问Java内部API受到限制，可能会显示如下错误：

```
java.lang.IllegalAccessError: superclass access check failed: class org.jetbrains.kotlin.kapt3.base.javac.KaptJavaCompiler cannot access class com.sun.tools.javac.main.JavaCompiler because module jdk.compiler does not export com.sun.tools.javac.main to unnamed module
```

## 解决方案

我们提供了以下方案来解决这些问题：

### 方案1：使用Java 21并升级Gradle（推荐）

项目已更新为使用Gradle 8.12和Android Gradle Plugin 8.2.0，这些版本与Java 21兼容。同时，我们添加了必要的JVM参数来解决Java模块系统限制问题。

如果你正在使用Java 21（如Android Studio最新版自带的JDK），这是最简单的解决方案：

1. 确保使用最新的项目代码
2. 让Android Studio使用其内置JDK
3. 正常同步和构建项目

所有必要的Java模块系统配置已经包含在项目配置文件中：
- `gradle.properties`中添加了Gradle守护进程和Kotlin守护进程的JVM参数
- `app/build.gradle`中添加了JavaCompile和KotlinCompile任务的配置

### 方案2：使用Java 8或Java 11并降级Gradle

如果你需要使用旧版本的Java，可以将项目配置为使用Gradle 7.4.2：

1. 修改`gradle/wrapper/gradle-wrapper.properties`：
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-7.4.2-bin.zip
   ```

2. 修改`build.gradle`中的Android Gradle Plugin版本：
   ```gradle
   classpath 'com.android.tools.build:gradle:7.2.2'
   ```

3. 配置Android Studio使用Java 8或11：
   - 打开**File > Settings > Build, Execution, Deployment > Build Tools > Gradle**
   - 在"Gradle JDK"下拉菜单中选择Java 8或11
   - 如果没有这些版本，使用JDK Manager安装

### 方案3：在gradle.properties中配置JDK路径

如果你有特定版本的JDK需要使用，可以在`gradle.properties`中配置：

```properties
# 使用Java 8
org.gradle.java.home=/path/to/java8

# 或使用Java 17
# org.gradle.java.home=/path/to/java17
```

## 验证配置

配置完成后：
1. 重启Android Studio
2. 在Build窗口中查看Gradle输出
3. 确认使用的Java版本和Gradle版本是兼容的

## 缓存清理（如遇问题）

如果更新配置后仍然遇到错误，可能需要清理缓存：

1. 关闭Android Studio
2. 执行项目根目录下的`clean_gradle_cache.sh`脚本：
   ```bash
   ./clean_gradle_cache.sh
   ```
3. 重启Android Studio并选择"File > Invalidate Caches / Restart..."

## 项目兼容性说明

当前项目配置：
- 支持Java 17-21 + Gradle 8.12 + AGP 8.2.0
- 代码使用Java 8兼容的语法特性
- UI使用Jetpack Compose 1.3.0
- 包含解决Java模块系统限制的必要配置

如有任何问题，请提交issue或参考Android Gradle兼容性矩阵和[Kotlin KAPT文档](https://kotlinlang.org/docs/kapt.html)。 