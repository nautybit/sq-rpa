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

我们提供了以下方案来解决这些问题（按推荐顺序排列）：

### 方案1：使用Java 8（当前推荐）

由于Java 9+引入的模块系统导致与KAPT（Kotlin注解处理器）的兼容性问题难以完全解决，我们已将项目配置为使用Java 8。这是目前最稳定、最可靠的解决方案。

项目当前配置：
- Gradle 7.4.2
- Android Gradle Plugin 7.2.2
- 直接在gradle.properties中设置Java 8路径

如果你使用的是Java 21的环境，项目会自动使用指定的Java 8来构建，你不需要自己安装或切换Java版本。

### 方案2：手动配置Java 8

如果方案1不起作用，你可以手动配置Android Studio使用Java 8：

1. 打开**File > Settings > Build, Execution, Deployment > Build Tools > Gradle**
2. 在"Gradle JDK"下拉菜单中选择Java 8
3. 如果没有这个选项，点击"Download JDK..."，选择版本8并安装

### 方案3：在不同的系统上使用不同的Java 8路径

如果你的系统上Java 8安装路径与项目默认配置不同，请修改`gradle.properties`：

```properties
# 根据实际Java 8安装路径修改
org.gradle.java.home=/path/to/your/java8
```

常见的Java 8安装路径：
- macOS: `/Library/Java/JavaVirtualMachines/jdk1.8.0_xxx.jdk/Contents/Home`
- Windows: `C:\Program Files\Java\jdk1.8.0_xxx`
- Linux: `/usr/lib/jvm/java-8-openjdk` 或 `/usr/lib/jvm/java-8-oracle`

## 验证配置

配置完成后：
1. 重启Android Studio
2. 在Build窗口中查看Gradle输出
3. 确认使用的是Java 8版本（应该看到类似"Java version: 1.8.0_xxx"的信息）

## 缓存清理（如遇问题）

如果更新配置后仍然遇到错误，请尝试清理缓存：

1. 关闭Android Studio
2. 执行项目根目录下的`clean_gradle_cache.sh`脚本：
   ```bash
   ./clean_gradle_cache.sh
   ```
3. 重启Android Studio并选择"File > Invalidate Caches / Restart..."

## 项目兼容性说明

当前项目配置：
- 使用Java 8 + Gradle 7.4.2 + AGP 7.2.2
- 代码使用Java 8兼容的语法特性
- UI使用Jetpack Compose 1.3.0

如有任何问题，请提交issue或参考[Android Gradle兼容性矩阵](https://developer.android.com/studio/releases/gradle-plugin#compatibility)。 