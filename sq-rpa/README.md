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
- Java 11 或更高版本（参见 [Java 8 兼容性说明](JAVA8_COMPATIBILITY_NOTE.md)）
- Kotlin 1.9.10
- Gradle 8.12

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