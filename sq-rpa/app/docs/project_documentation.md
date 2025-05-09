# 松鼠RPA项目文档

## 目录

1. [项目概述](#项目概述)
2. [软件架构](#软件架构)
3. [技术栈](#技术栈)
4. [核心组件详解](#核心组件详解)
5. [数据模型设计](#数据模型设计)
6. [关键技术实现](#关键技术实现)
7. [开发入门指南](#开发入门指南)
8. [常见开发任务](#常见开发任务)
9. [开发技巧](#开发技巧)
10. [常见问题与解决方案](#常见问题与解决方案)
11. [性能优化](#性能优化)
12. [安全考虑](#安全考虑)
13. [未来规划](#未来规划)
14. [拓展阅读](#拓展阅读)

## 项目概述

松鼠RPA是一款基于Android辅助功能服务开发的微信自动回复机器人。它能够监听微信消息，并根据预设的规则或JavaScript脚本自动回复。无需Root权限或微信插件，就能实现微信消息的自动化处理。

### 主要功能

- 实时监听微信消息
- 支持多种匹配规则：精确匹配、包含匹配、正则表达式匹配
- 支持JavaScript脚本自定义回复逻辑
- 消息历史记录和管理
- 低资源占用的后台服务

### 系统需求

- Android 8.0+（API级别26+）
- 微信 8.0+
- 设备需开启辅助功能权限
- 小米设备需额外配置权限（MIUI优化等）

## 软件架构

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

### 应用层次结构

```
com.sq.rpa/
├── SQRPAApplication.kt          # 应用入口类
├── accessibility/               # 辅助功能服务实现
├── api/                        # 应用API接口
├── core/                       # 核心业务逻辑
├── data/                       # 数据访问层
├── model/                      # 数据模型
├── receiver/                   # 广播接收器
├── script/                     # 脚本引擎
├── service/                    # 后台服务
└── ui/                         # 用户界面
```

### 数据流向

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

## 技术栈

松鼠RPA项目基于现代Android开发技术，主要技术栈包括：

1. **编程语言**：Kotlin
2. **UI框架**：Material Design + Jetpack Compose
3. **数据存储**：Room（一种封装了SQLite的持久性库）
4. **异步编程**：Kotlin Coroutines
5. **依赖注入**：遵循单例模式
6. **脚本引擎**：Rhino JavaScript引擎
7. **Android API**：辅助功能服务API (AccessibilityService)

### 关键依赖

| 依赖库 | 版本 | 用途 |
|-------|------|------|
| Kotlin | 1.7.10 | 编程语言 |
| AndroidX Core | 1.8.0 | Android核心库 |
| Room | 2.5.2 | 数据库ORM |
| Compose | 1.3.0 | UI框架 |
| Rhino | 1.7.13 | JavaScript引擎 |
| Material Components | 1.6.0 | UI组件库 |
| Kotlin Coroutines | 1.6.1 | 异步编程 |
| Koin | 3.2.0 | 依赖注入 |
| Retrofit | 2.9.0 | 网络请求库 |
| OkHttp | 4.9.0 | HTTP客户端 |
| Gson | 2.9.0 | JSON解析库 |

## 核心组件详解

### 1. 应用入口 - SQRPAApplication

应用的入口点，负责全局资源的初始化和管理：
- 数据库初始化
- 脚本引擎初始化
- 前台服务启动
- 示例脚本加载

### 2. 辅助功能服务 - RPAAccessibilityService

应用的核心功能组件，基于Android的AccessibilityService实现：
- 监听微信界面变化
- 提取消息内容
- 发送自动回复
- 手势操作（如点击、滑动）

`RPAAccessibilityService` 利用Android的辅助功能API监听屏幕变化：

```kotlin
// 接收辅助功能事件
override fun onAccessibilityEvent(event: AccessibilityEvent) {
    // 只处理微信应用的事件
    if (event.packageName != WECHAT_PACKAGE) return
    
    when (event.eventType) {
        // 窗口状态变化（如打开新页面）
        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
            processWechatWindowChanged(event)
        }
        // 窗口内容变化（如收到新消息）
        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
            if (currentChatTarget.isNotEmpty()) {
                processWechatContentChanged(event)
            }
        }
    }
}
```

核心技术是通过定位UI元素来读取和操作微信界面：

```kotlin
// 尝试多种可能的ID模式（适配不同微信版本）
val possibleIds = listOf(
    "com.tencent.mm:id/b5q",  // 当前代码中使用的ID
    "com.tencent.mm:id/ij",   // 其他版本可能的ID
    "com.tencent.mm:id/kl"    // 其他版本可能的ID
)
```

### 3. 规则引擎 - RuleEngine

处理消息匹配和响应逻辑：
- 规则管理（CRUD操作）
- 消息匹配算法
- 回复生成逻辑
- 脚本执行调度

`RuleEngine` 负责匹配接收到的消息并生成回复：

```kotlin
// 判断消息是否匹配规则
private fun matchRule(rule: MessageRule, message: String): Boolean {
    return when (rule.matchType) {
        // 精确匹配
        MessageRule.MATCH_TYPE_EXACT -> {
            message == rule.matchPattern
        }
        // 包含匹配
        MessageRule.MATCH_TYPE_CONTAINS -> {
            message.contains(rule.matchPattern)
        }
        // 正则匹配
        MessageRule.MATCH_TYPE_REGEX -> {
            try {
                val pattern = Pattern.compile(rule.matchPattern)
                pattern.matcher(message).find()
            } catch (e: Exception) {
                Log.e(TAG, "正则表达式匹配异常: ${rule.id}", e)
                false
            }
        }
        // 其他匹配类型
        else -> false
    }
}
```

### 4. 脚本引擎 - ScriptEngine

基于Rhino的JavaScript执行环境：
- 脚本编译与执行
- 脚本API注入
- 错误处理和日志记录
- 脚本生命周期管理

`ScriptEngine` 使用Rhino引擎执行JavaScript脚本：

```kotlin
// 执行消息处理脚本
fun processChatMessage(scriptId: String, message: String, sender: String): String? {
    // 检查脚本是否存在
    if (!scripts.containsKey(scriptId)) {
        Log.w(TAG, "脚本未找到: $scriptId")
        return null
    }
    
    // 执行脚本处理消息
    return try {
        val result = executeScript(scriptId, mapOf(
            "message" to message,
            "sender" to sender,
            "timestamp" to System.currentTimeMillis()
        )) as? String
        
        result
    } catch (e: Exception) {
        Log.e(TAG, "脚本执行异常: $scriptId", e)
        null
    }
}
```

### 5. 数据层 - Room数据库

使用Room实现的持久化存储：
- 消息规则表（message_rules）
- 聊天消息记录表（chat_messages）
- 脚本信息表（script_info）
- DAO接口实现CRUD操作

## 数据模型设计

### 消息规则（MessageRule）

```kotlin
@Entity(tableName = "message_rules")
data class MessageRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                // 规则名称
    val matchType: Int,              // 匹配类型
    val matchPattern: String,        // 匹配模式
    val responseType: Int,           // 响应类型
    val responseContent: String,     // 响应内容
    val enabled: Boolean = true,     // 是否启用
    val priority: Int = 0,           // 优先级
    val delayMs: Long = 0,           // 延迟时间
    val description: String = "",    // 描述
    val createTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis()
)
```

### 聊天消息（ChatMessage）

```kotlin
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,              // 发送者
    val content: String,             // 消息内容
    val type: Int,                   // 消息类型
    val replied: Boolean = false,    // 是否已回复
    val replyContent: String? = null,// 回复内容
    val ruleId: Long? = null,        // 触发规则ID
    val timestamp: Long = System.currentTimeMillis()
)
```

## 关键技术实现

### 1. 消息监听与提取

通过AccessibilityService API监听微信界面变化，并提取消息内容：

```kotlin
// 处理微信内容变化事件
private fun processWechatContentChanged(event: AccessibilityEvent) {
    val rootNode = rootInActiveWindow ?: return
    
    serviceScope.launch {
        try {
            // 提取聊天消息
            val chatMessages = extractChatMessages(rootNode)
            if (chatMessages.isNotEmpty()) {
                val lastMessage = chatMessages.last()
                
                // 处理消息...
            }
        } finally {
            rootNode.recycle()
        }
    }
}
```

### 2. 规则匹配与处理

根据预定义的规则匹配消息内容，支持多种匹配模式：

```kotlin
private fun matchRule(rule: MessageRule, message: String): Boolean {
    return when (rule.matchType) {
        MessageRule.MATCH_TYPE_EXACT -> message == rule.matchPattern
        MessageRule.MATCH_TYPE_CONTAINS -> message.contains(rule.matchPattern)
        MessageRule.MATCH_TYPE_REGEX -> {
            val pattern = Pattern.compile(rule.matchPattern)
            pattern.matcher(message).find()
        }
        else -> false
    }
}
```

### 3. 异步处理

使用Kotlin协程处理异步操作，避免阻塞主线程：

```kotlin
private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

suspend fun refreshRulesCache() {
    withContext(Dispatchers.IO) {
        cachedRules = database.messageRuleDao().getEnabledRulesList()
    }
}
```

## 开发入门指南

### Android基础概念

1. **Activity**：类似于Web中的页面，负责UI的展示和用户交互
2. **Service**：后台运行的组件，不需要用户界面
3. **Application**：应用程序全局状态的管理类
4. **Manifest**：应用的配置文件，声明组件和权限

### Kotlin语言特性

1. **空安全**：使用 `?` 和 `!!` 操作符处理空值
2. **扩展函数**：为已有类添加新功能
3. **协程**：替代传统回调的异步编程方式
4. **单例模式**：使用 `object` 或 `companion object` 实现

### 从Web开发者角度理解Android开发

| Web开发 | Android开发 | 松鼠RPA中的示例 |
|---------|------------|----------------|
| 页面 | Activity | MainActivity.kt |
| 路由 | Intent | 页面跳转逻辑 |
| DOM操作 | View操作 | AccessibilityNodeInfo操作 |
| localStorage | SharedPreferences | 配置存储 |
| 数据库 | Room/SQLite | AppDatabase.kt |
| 异步请求 | 协程 | `launch` 和 `withContext` |
| 前端框架 | Android Jetpack | Room, ViewModel等 |
| 事件处理 | 监听器 | 辅助功能事件处理 |

### 主要差异

1. **生命周期管理**：Android应用有明确的生命周期，需要妥善处理
2. **权限系统**：需要声明和请求权限，如辅助功能服务权限
3. **资源管理**：XML布局文件、字符串资源、图片等需要特殊管理
4. **后台处理**：Android对后台任务有严格限制

### 设置开发环境

1. 安装Android Studio
2. 克隆松鼠RPA项目
3. 打开项目并等待Gradle同步

### 理解项目配置

1. **app/build.gradle**：项目依赖和配置
2. **AndroidManifest.xml**：应用组件声明
3. **res/xml/accessibility_service_config.xml**：辅助功能服务配置

### 运行项目

1. 连接Android设备（确保开启USB调试）
2. 在Android Studio中点击运行按钮
3. 手动开启辅助功能服务（权限限制，无法自动开启）

## 常见开发任务

### 1. 添加新规则类型

```kotlin
// 在MessageRule.kt中添加新的匹配类型常量
const val MATCH_TYPE_NEW = 5

// 在RuleEngine.kt中实现新的匹配逻辑
private fun matchRule(rule: MessageRule, message: String): Boolean {
    return when (rule.matchType) {
        // 现有类型...
        
        MATCH_TYPE_NEW -> {
            // 新的匹配逻辑
            true
        }
        
        else -> false
    }
}
```

### 2. 创建新的UI界面

1. 创建新的Activity类
2. 在res/layout中创建布局文件
3. 在AndroidManifest.xml中注册新Activity

### 3. 添加新的脚本API

```kotlin
// 在RhinoEvaluator类中的injectAPIs方法添加新API
private fun injectAPIs(cx: Context, scope: ScriptableObject) {
    // 现有API...
    
    // 添加新API
    val newApiObj = cx.newObject(scope)
    ScriptableObject.putProperty(newApiObj, "someFunction", Context.javaToJS({ param: String -> 
        // 实现功能
    }, scope))
    ScriptableObject.putProperty(scope, "newApi", newApiObj)
}
```

## 开发技巧

1. **辅助功能服务调试**：使用Android Debug Bridge (ADB) 查看日志
   ```bash
   adb logcat -s RPAAccessibilityService
   ```

2. **微信元素定位**：微信更新可能会改变UI元素ID，需要适配
   ```kotlin
   // 使用多种ID尝试定位元素
   val possibleIds = listOf("id1", "id2", "id3")
   for (id in possibleIds) {
       val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
       if (nodes.isNotEmpty()) break
   }
   ```

3. **适配不同微信版本**：使用回退策略
   ```kotlin
   // 主要策略失败时，使用备用策略
   if (messageNodes.isEmpty()) {
       // 尝试备用方法，如通过类名查找
   }
   ```

4. **调试辅助功能服务**
   1. 使用Logcat查看日志
   2. 使用Layout Inspector分析微信界面结构
   3. 使用UIAutomatorViewer工具辅助分析

## 常见问题与解决方案

1. **无法监听到微信消息**
   - 检查辅助功能服务是否正确启用
   - 检查微信版本是否匹配当前适配的元素ID

2. **自动回复失败**
   - 检查是否有匹配的规则
   - 检查输入框和发送按钮的元素ID是否正确

3. **应用崩溃问题**
   - 查看日志确定异常原因
   - 添加适当的异常处理，特别是UI元素操作时

4. **后台运行问题**
   - 使用前台服务保持应用活跃
   - 配置正确的防电池优化设置

## 性能优化

1. **内存优化**
   - 正确回收AccessibilityNodeInfo对象
   - 使用缓存减少数据库访问
   ```kotlin
   // 缓存规则，避免频繁查询数据库
   private var cachedRules: List<MessageRule> = emptyList()
   ```

2. **CPU优化**
   - 耗时操作放在IO线程执行
   - 使用惰性初始化延迟加载资源

3. **电池优化**
   - 使用前台服务确保长期运行
   - 减少不必要的UI更新和事件处理

## 安全考虑

1. **数据安全**
   - 所有数据本地存储，不上传服务器
   - 消息内容仅用于规则匹配，不用于其他目的

2. **权限管理**
   - 仅申请必要的权限
   - 明确说明每个权限的用途

3. **代码安全**
   - 脚本沙盒执行，限制API访问
   - 防止恶意脚本注入

## 未来规划

1. **功能扩展**
   - 支持更多消息类型（图片、语音等）
   - 添加定时任务支持
   - 规则导入导出功能

2. **架构改进**
   - 迁移到MVVM架构
   - 引入依赖注入框架
   - 增强单元测试覆盖率

3. **性能提升**
   - 优化元素定位算法
   - 改进脚本执行效率
   - 减少内存占用

## 拓展阅读

1. [Android开发者文档](https://developer.android.com/docs)
2. [Kotlin官方文档](https://kotlinlang.org/docs/home.html)
3. [Android辅助功能开发指南](https://developer.android.com/guide/topics/ui/accessibility/apps)
4. [Room持久化库](https://developer.android.com/training/data-storage/room) 