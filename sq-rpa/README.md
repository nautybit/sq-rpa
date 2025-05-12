# 松鼠RPA - 微信自动回复机器人

这是一个基于 Android 辅助功能服务的微信自动回复机器人应用，支持自定义规则和脚本。

## 项目概述

松鼠RPA 是一个强大的微信自动回复工具，具有以下特点：

- 基于 Android 辅助功能服务，无需 Root 权限
- 实时监听微信消息
- 支持 JavaScript 脚本控制
- 灵活的规则配置
- 完整的数据记录
- 基于 Jetpack Compose 的现代 UI
- 适应不同微信版本的 UI 元素识别

## 开发环境要求

⚠️ **重要**: 本项目使用以下固定版本组合，以确保兼容性：

- **Java**: 必须使用 Java 8 (JDK 1.8)
- **Gradle**: 7.4.2 (自动下载)
- **Android Gradle Plugin**: 7.1.3
- **Kotlin**: 1.6.10
- **Compose**: 1.1.1
- **Room**: 2.4.2
- **compileSdk/targetSdk**: 31
- **Android Studio**: 推荐使用 Electric Eel (2022.1.1) 或更高版本

## 首次构建

在首次构建项目前，请执行以下步骤：

1. 确保已安装 Java 8 (JDK 1.8)，可使用以下命令验证：
   ```bash
   java -version
   ```
   输出应该显示 `java version "1.8.0_xxx"`

2. 执行项目根目录下的环境准备脚本:
   ```bash
   chmod +x prepare_env.sh
   ./prepare_env.sh
   ```
   此脚本将清理缓存并预下载 Gradle 7.4.2

3. 检查 `gradle.properties` 中的 JDK 路径是否正确：
   ```properties
   org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_xxx.jdk/Contents/Home
   ```
   根据您的 Java 8 安装路径修改此值

4. 在 Android Studio 中打开项目，等待 Gradle 同步完成

5. 如果遇到同步问题，请尝试：
   - 选择 `File > Invalidate Caches / Restart...`
   - 确保项目使用 Java 8 构建（查看 Build 输出日志）

## 技术栈

- Kotlin 1.6.10
- Jetpack Compose 1.1.1 (UI)
- Room 2.4.2 (数据库)
- Koin 3.1.5 (依赖注入)
- Android 辅助功能服务 (AccessibilityService)
- Rhino 1.7.13 (JavaScript 引擎)

## 项目结构

- `app/src/main/java/com/sq/rpa/`
  - `ui/`: 用户界面组件
  - `core/`: 核心业务逻辑
  - `data/`: 数据处理和存储
  - `model/`: 数据模型
  - `script/`: 脚本引擎
  - `service/`: 服务组件
  - `accessibility/`: 辅助功能服务
  - `util/`: 工具类

## 如何使用

1. 安装应用
2. 启用辅助功能服务
3. 配置自动回复规则
4. 打开微信，应用将自动监听并回复消息

## 故障排除

如果遇到构建问题：

1. **确保使用正确的 Java 版本**：
   - 项目必须使用 Java 8
   - 检查并修改 `gradle.properties` 中的 `org.gradle.java.home` 路径

2. **清理缓存**:
   ```bash
   ./prepare_env.sh
   ```
   或手动执行：
   ```bash
   rm -rf ~/.gradle/caches/
   rm -rf .gradle/ build/ app/build/
   ```

3. **Gradle 下载问题**：
   - 脚本使用阿里云镜像下载 Gradle 7.4.2
   - 如果仍有网络问题，可手动下载并放置在:
     `~/.gradle/wrapper/dists/gradle-7.4.2-bin/`

4. **Android Studio 问题**:
   - 选择 `File > Invalidate Caches / Restart...`
   - 确保 Android Studio 中 Gradle JDK 设置指向 Java 8

5. **检查构建日志**：
   - 注意 Gradle 和 Java 版本信息
   - 查找 "当前使用的Java版本" 日志行

## 高级配置

如需更多信息，请参考:
- `JAVA8_COMPATIBILITY_NOTE.md`: Java 8 兼容性详情
- `JDK_SETUP_GUIDE.md`: JDK 配置指南
- `app/docs/`: 项目文档目录

## 贡献指南

欢迎提交 Pull Request 或提出 Issue。

## 许可证

[MIT License](LICENSE) 