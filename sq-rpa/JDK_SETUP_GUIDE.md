# JDK 版本配置指南

## 问题描述

松鼠RPA项目使用 Gradle 7.4.2，该版本与 Java 21 不兼容。当你使用较新版本的 JDK (Java 21) 时，会出现以下错误：

```
Caused by: org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
Your build is currently configured to use incompatible Java 21.x.x and Gradle 7.4.2. Cannot sync the project.
```

## 解决方案

### 方案1：配置项目使用 Java 8 或 Java 11 (推荐)

1. 查找系统中已安装的 JDK 版本：

   **Mac/Linux**:
   ```bash
   /usr/libexec/java_home -V
   ```

   **Windows**:
   ```
   dir "C:\Program Files\Java"
   # 或者
   dir "C:\Program Files (x86)\Java"
   ```

2. 在 `gradle.properties` 文件中添加以下配置，指向 Java 8 或 Java 11：

   ```properties
   # Mac 路径示例
   org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
   
   # Windows 路径示例
   # org.gradle.java.home=C:\\Program Files\\Java\\jdk1.8.0_251
   ```

### 方案2：安装 Java 8 或 Java 11

如果你的系统中没有 Java 8 或 Java 11，可以下载并安装：

- [Amazon Corretto (OpenJDK)](https://aws.amazon.com/corretto/)
- [AdoptOpenJDK](https://adoptopenjdk.net/)
- [Oracle JDK](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

安装后，按照方案1的步骤配置 `gradle.properties`。

### 方案3：Android Studio 中配置 Gradle JDK

1. 打开 Android Studio
2. 进入 File > Settings > Build, Execution, Deployment > Build Tools > Gradle
3. 在 "Gradle JDK" 下拉菜单中，选择 Java 8 或 Java 11
4. 点击 "Apply" 和 "OK"

## 验证配置

配置完成后，重启 Android Studio 并尝试同步项目。如果仍然有问题，检查 Gradle 输出，确认它使用的是正确版本的 JDK。

## 注意事项

- 请勿升级 Gradle 版本，因为项目已经针对 Gradle 7.4.2 和 Java 8 进行了优化
- 项目已配置为使用 Java 8 特性，确保编译和运行环境都兼容 