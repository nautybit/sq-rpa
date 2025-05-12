# 松鼠RPA项目完整构建指南

本文档提供松鼠RPA微信自动回复机器人项目的完整构建步骤，从环境配置到应用安装全过程。

## 目录

1. [环境准备](#环境准备)
2. [项目获取](#项目获取)
3. [构建环境设置](#构建环境设置)
4. [编译项目](#编译项目)
5. [安装到设备](#安装到设备)
6. [应用配置](#应用配置)
7. [常见问题排查](#常见问题排查)
8. [附录](#附录)

## 环境准备

### 1. 工具版本要求

本项目需要使用以下特定版本的工具和库：

| 组件 | 版本 | 说明 |
|-----|------|-----|
| JDK | 1.8 (Java 8) | 必须使用Java 8，不能使用更高版本 |
| Android Studio | Electric Eel (2022.1.1)+ | 推荐版本，更高版本也可 |
| Gradle | 7.4.2 | 通过wrapper自动下载 |
| Android Gradle Plugin | 7.1.3 | 适配Java 8 |
| Kotlin | 1.6.10 | 在build.gradle中指定 |
| Compose | 1.1.1 | 在build.gradle中指定 |
| Room | 2.4.2 | 与Java 8兼容 |
| compileSdk | 31 | Android 12 |

### 2. 安装Java 8

#### macOS系统

1. 从[Adoptium](https://adoptium.net/temurin/releases/?version=8)下载JDK 8
2. 安装下载的.pkg文件
3. 确认安装成功：
   ```bash
   /usr/libexec/java_home -v 1.8
   ```
   应该输出类似：`/Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home`

#### Windows系统

1. 从[Adoptium](https://adoptium.net/temurin/releases/?version=8)下载Windows版JDK 8
2. 运行安装程序，按照提示完成安装
3. 安装完成后，通过命令提示符确认：
   ```cmd
   java -version
   ```
   确保输出显示`java version "1.8.0_xxx"`

#### Linux系统

以Ubuntu为例：
```bash
sudo apt-get update
sudo apt-get install openjdk-8-jdk
java -version
```

### 3. 安装Android Studio

1. 从[Android开发者网站](https://developer.android.com/studio)下载Android Studio
2. 安装Android Studio并完成初始化设置
3. 在首次启动向导中，确保安装了以下组件：
   - Android SDK Platform 31
   - Android SDK Build-Tools 31
   - Android SDK Command-line Tools
   - Android SDK Platform-Tools

## 项目获取

### 1. 克隆项目代码

```bash
git clone https://github.com/yourusername/sq-rpa.git
cd sq-rpa
```

### 2. 检查分支

确保使用主分支上的最新代码：

```bash
git checkout main
git pull
```

## 构建环境设置

### 1. 运行环境准备脚本

执行项目根目录下的准备脚本：

```bash
chmod +x prepare_env.sh
./prepare_env.sh
```

此脚本会：
- 检查Java版本
- 清理构建缓存
- 预下载Gradle 7.4.2（使用阿里云镜像加速）

### 2. 配置Java 8路径

编辑`gradle.properties`文件，确保`org.gradle.java.home`指向正确的Java 8安装路径：

#### 对于macOS
```properties
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_xxx.jdk/Contents/Home
```
根据实际路径调整，可通过`/usr/libexec/java_home -v 1.8`命令获取

#### 对于Windows
```properties
org.gradle.java.home=C:\\Program Files\\Java\\jdk1.8.0_xxx
```

#### 对于Linux
```properties
org.gradle.java.home=/usr/lib/jvm/java-8-openjdk-amd64
```

### 3. 检查项目配置文件

确认以下文件内容正确：

1. `gradle/wrapper/gradle-wrapper.properties`
   ```properties
   distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-7.4.2-bin.zip
   ```

2. `build.gradle` (项目根目录)
   ```gradle
   buildscript {
       ext {
           kotlin_version = '1.6.10'
           compose_version = '1.1.1'
           room_version = '2.4.2'
           koin_version = '3.1.5'
       }
       // ...
       dependencies {
           classpath 'com.android.tools.build:gradle:7.1.3'
           // ...
       }
   }
   ```

3. `app/build.gradle`
   ```gradle
   android {
       // ...
       compileOptions {
           sourceCompatibility JavaVersion.VERSION_1_8
           targetCompatibility JavaVersion.VERSION_1_8
       }
       kotlinOptions {
           jvmTarget = '1.8'
       }
       // ...
   }
   ```

## 编译项目

### 1. 使用Android Studio编译

1. 在Android Studio中打开项目
2. 等待Gradle同步完成
3. 检查构建输出，确保使用的是Java 8：
   ```
   当前使用的Java版本: 1.8.0_xxx
   ```
4. 点击"Make Project"按钮(锤子图标)或按`Ctrl+F9`(Windows/Linux)或`Cmd+F9`(macOS)
5. 等待编译完成

### 2. 使用命令行编译

如果您更喜欢使用命令行，可以：

#### macOS/Linux
```bash
cd /path/to/sq-rpa
./gradlew assembleDebug
```

#### Windows
```cmd
cd \path\to\sq-rpa
.\gradlew.bat assembleDebug
```

编译成功后，APK文件将位于`app/build/outputs/apk/debug/app-debug.apk`

## 安装到设备

### 1. 通过Android Studio安装

1. 连接Android设备（确保已启用USB调试）
2. 在Android Studio中点击"Run"按钮(绿色三角形)
3. 在设备选择对话框中选择您的设备
4. 等待应用安装和启动

### 2. 通过命令行安装

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 应用配置

### 1. 辅助功能设置

安装完成后，需要设置辅助功能权限：

1. 打开应用
2. 点击"启用辅助功能"按钮
3. 在系统设置中找到"松鼠RPA"
4. 打开辅助功能开关

### 2. 配置自动回复规则

1. 在应用主界面点击"添加规则"
2. 设置触发条件和回复内容
3. 保存规则

### 3. 高级脚本

如需使用JavaScript脚本功能：

1. 在应用中点击"脚本管理"
2. 点击"添加脚本"
3. 输入脚本名称和内容
4. 保存脚本并启用

## 常见问题排查

### 1. 编译错误：Unsupported class file major version

**问题**：出现类似以下错误信息：
```
Caused by: org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
General error during conversion: Unsupported class file major version 65
```

**解决方法**：
- 确保使用Java 8，而不是更高版本
- 检查`gradle.properties`中的Java路径设置
- 运行`prepare_env.sh`脚本清理缓存

### 2. 依赖下载失败

**问题**：Gradle同步时出现依赖下载错误

**解决方法**：
- 确保网络连接正常
- 检查`gradle-wrapper.properties`中使用的是阿里云镜像
- 增加Gradle网络超时设置：
  ```properties
  org.gradle.internal.http.connectionTimeout=120000
  org.gradle.internal.http.socketTimeout=120000
  ```

### 3. Room数据库兼容性问题

**问题**：出现Room相关的编译错误

**解决方法**：
- 确保使用Room 2.4.2版本，而不是更高版本
- 检查Kotlin版本是1.6.10

### 4. 设备上运行崩溃

**问题**：应用在设备上安装后立即崩溃

**解决方法**：
- 检查Logcat日志确定崩溃原因
- 确保授予了必要的权限
- 验证设备Android版本是否支持（最低需要Android 7.0）

## 附录

### 指定的依赖版本

松鼠RPA项目使用以下精确版本的依赖，已经过测试，确保与Java 8兼容：

```gradle
// Kotlin相关
org.jetbrains.kotlin:kotlin-stdlib:1.6.10
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1

// AndroidX核心
androidx.core:core-ktx:1.7.0
androidx.appcompat:appcompat:1.4.1
androidx.lifecycle:lifecycle-runtime-ktx:2.4.1
androidx.activity:activity-compose:1.4.0

// Compose
androidx.compose.ui:ui:1.1.1
androidx.compose.material:material:1.1.1
androidx.compose.ui:ui-tooling-preview:1.1.1

// Room
androidx.room:room-runtime:2.4.2
androidx.room:room-ktx:2.4.2
androidx.room:room-compiler:2.4.2

// 网络和解析
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.squareup.okhttp3:okhttp:4.9.0
com.squareup.okhttp3:logging-interceptor:4.9.0
com.google.code.gson:gson:2.9.0

// JavaScript引擎
org.mozilla:rhino:1.7.13

// 测试
junit:junit:4.13.2
androidx.test.ext:junit:1.1.3
androidx.test.espresso:espresso-core:3.4.0
```

### 有用的命令

#### 查看Java版本
```bash
java -version
```

#### 列出已安装的JDK（macOS）
```bash
/usr/libexec/java_home -V
```

#### 清理项目
```bash
./gradlew clean
```

#### 生成release版本
```bash
./gradlew assembleRelease
```

#### 检查依赖树
```bash
./gradlew app:dependencies
```

### 更多资源

- [Java 8下载](https://adoptium.net/temurin/releases/?version=8)
- [Android Gradle插件文档](https://developer.android.com/studio/releases/gradle-plugin)
- [Gradle兼容性矩阵](https://docs.gradle.org/current/userguide/compatibility.html)
- [Kotlin版本兼容性](https://kotlinlang.org/docs/gradle.html#gradle-kotlin-dsl)
- [Room数据库文档](https://developer.android.com/training/data-storage/room) 