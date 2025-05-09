# 松鼠RPA - 软件模块架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户界面层 (UI Layer)                     │
├─────────┬─────────────────┬───────────────────┬─────────────────┤
│         │                 │                   │                 │
│MainActivity│    ScriptActivity   │  SettingsActivity  │    ···   │
│         │                 │                   │                 │
└─────────┴─────────────────┴───────────────────┴─────────────────┘
                           ▲
                           │
                           │
┌─────────────────────────────────────────────────────────────────┐
│                      业务逻辑层 (Business Layer)                  │
├─────────────────┬──────────────────┬─────────────────────────────┤
│                 │                  │                             │
│   ScriptEngine  │    RuleEngine    │       AccessibilityService  │
│                 │                  │                             │
└─────────────────┴──────────────────┴─────────────────────────────┘
                           ▲
                           │
                           │
┌─────────────────────────────────────────────────────────────────┐
│                      数据访问层 (Data Layer)                      │
├─────────────────┬──────────────────┬─────────────────────────────┤
│                 │                  │                             │
│  MessageRuleDao │  ScriptInfoDao   │      ChatMessageDao         │
│                 │                  │                             │
└─────────────────┴──────────────────┴─────────────────────────────┘
                           ▲
                           │
                           │
┌─────────────────────────────────────────────────────────────────┐
│                      数据模型层 (Model Layer)                     │
├─────────────────┬──────────────────┬─────────────────────────────┤
│                 │                  │                             │
│   MessageRule   │    ScriptInfo    │      ChatMessage            │
│                 │                  │                             │
└─────────────────┴──────────────────┴─────────────────────────────┘
```

## 模块详细说明

### 1. 用户界面层 (UI Layer)
负责用户交互和视觉展示，基于Jetpack Compose构建。

- **MainActivity**: 应用主界面，显示服务状态和功能入口
- **ScriptActivity**: 脚本管理界面，提供脚本的增删改查功能
- **SettingsActivity**: 设置界面，配置应用基本功能

### 2. 业务逻辑层 (Business Layer)
实现核心业务逻辑，处理消息匹配和自动回复功能。

- **ScriptEngine**: 脚本引擎，基于Rhino JavaScript引擎，执行用户定义的脚本
- **RuleEngine**: 规则引擎，处理消息匹配和响应生成
- **RPAAccessibilityService**: 辅助功能服务，监听微信消息并触发自动回复

### 3. 数据访问层 (Data Layer)
负责数据持久化和访问，基于Room数据库实现。

- **MessageRuleDao**: 消息规则数据访问对象
- **ScriptInfoDao**: 脚本信息数据访问对象
- **ChatMessageDao**: 聊天消息数据访问对象

### 4. 数据模型层 (Model Layer)
定义应用的核心数据结构。

- **MessageRule**: 消息规则模型，定义消息匹配条件和响应行为
- **ScriptInfo**: 脚本信息模型，存储脚本元数据和内容
- **ChatMessage**: 聊天消息模型，记录消息内容和处理状态

## 数据流向

1. **消息监听流程**：
   ```
   微信应用 → RPAAccessibilityService → RuleEngine → ScriptEngine → 自动回复
   ```

2. **规则管理流程**：
   ```
   用户界面 → MessageRuleDao → 数据库存储 → RuleEngine加载使用
   ```

3. **脚本管理流程**：
   ```
   ScriptActivity → ScriptInfoDao → 数据库存储 → ScriptEngine加载执行
   ```

## 组件依赖关系

- **SQRPAApplication**: 应用入口，负责初始化各组件
- **AppDatabase**: 数据库实例，提供DAO对象访问
- **RPAForegroundService**: 前台服务，保持应用在后台运行

## 技术栈

- **UI**: Jetpack Compose
- **数据库**: Room
- **并发**: Kotlin Coroutines
- **脚本引擎**: Mozilla Rhino
- **辅助功能**: Android Accessibility Service 