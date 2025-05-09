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

- Android Studio
- Java 17-21（推荐）或 Java 8（向下兼容）
- Kotlin 1.7.10
- Gradle 8.5（与Java 21兼容）
- Android Gradle Plugin 8.0.0

> **注意**：如果在构建过程中遇到 JDK 版本兼容性问题，请参考 [JDK 版本配置指南](JDK_SETUP_GUIDE.md)。
> 如果遇到Gradle下载问题，请运行项目根目录下的`fix_gradle_download.sh`脚本。

## 技术栈

- Kotlin
- Jetpack Compose 1.3.0 (UI)
- Room 2.5.2 (数据库)
- Koin 3.2.0 (依赖注入)
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

## 贡献指南

欢迎提交 Pull Request 或提出 Issue。

## 许可证

[MIT License](LICENSE) 