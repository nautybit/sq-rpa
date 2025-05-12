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
- **compileSdk/targetSdk**: 31
- **Android Studio**: Electric Eel (2022.1.1) 或更高版本

## 首次构建

在首次构建项目前，请运行以下步骤：

1. 确保已安装 Java 8 (JDK 1.8)
2. 执行项目根目录下的环境准备脚本:
   ```bash
   chmod +x prepare_env.sh
   ./prepare_env.sh
   ```
3. 在 Android Studio 中打开项目，等待 Gradle 同步完成
4. 如果遇到问题，请选择 `File > Invalidate Caches / Restart...`

## 技术栈

- Kotlin
- Jetpack Compose (UI)
- Room (数据库)
- Koin (依赖注入)
- Android 辅助功能服务 (AccessibilityService)
- Rhino (JavaScript 引擎)

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

## 贡献指南

欢迎提交 Pull Request 或提出 Issue。

## 许可证

[MIT License](LICENSE)

## 故障排除

如果遇到构建问题：

1. 确保使用正确的 Java 版本:
   - 项目需要 Java 8
   - 确认 gradle.properties 中的 `org.gradle.java.home` 路径正确

2. 清理缓存:
   ```bash
   rm -rf ~/.gradle/caches/
   rm -rf .gradle/ build/ app/build/
   ```

3. 检查 Gradle 下载:
   - Gradle 7.4.2 应该从阿里云镜像下载
   - 如果下载失败，运行 prepare_env.sh 脚本

4. 如遇 Android Studio 问题:
   - 选择 `File > Invalidate Caches / Restart...`
   - 确保 Android Studio 中 Gradle JDK 设置指向 Java 8 