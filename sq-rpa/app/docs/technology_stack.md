# 松鼠RPA技术栈与架构文档

## 技术栈概览

松鼠RPA项目是一个基于现代Android开发技术的自动回复机器人应用，主要技术栈包括：

1. **编程语言**：Kotlin
2. **UI框架**：Material Design + Jetpack Compose
3. **数据存储**：Room（一种封装了SQLite的持久性库）
4. **异步编程**：Kotlin Coroutines
5. **依赖注入**：遵循单例模式
6. **脚本引擎**：Rhino JavaScript引擎
7. **Android API**：辅助功能服务API (AccessibilityService)

## 核心组件与架构

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

### 核心组件详解

#### 1. 应用入口 - SQRPAApplication

应用的入口点，负责全局资源的初始化和管理：
- 数据库初始化
- 脚本引擎初始化
- 前台服务启动
- 示例脚本加载

#### 2. 辅助功能服务 - RPAAccessibilityService

应用的核心功能组件，基于Android的AccessibilityService实现：
- 监听微信界面变化
- 提取消息内容
- 发送自动回复
- 手势操作（如点击、滑动）

#### 3. 规则引擎 - RuleEngine

处理消息匹配和响应逻辑：
- 规则管理（CRUD操作）
- 消息匹配算法
- 回复生成逻辑
- 脚本执行调度

#### 4. 脚本引擎 - ScriptEngine

基于Rhino的JavaScript执行环境：
- 脚本编译与执行
- 脚本API注入
- 错误处理和日志记录
- 脚本生命周期管理

#### 5. 数据层 - Room数据库

使用Room实现的持久化存储：
- 消息规则表（message_rules）
- 聊天消息记录表（chat_messages）
- 脚本信息表（script_info）
- DAO接口实现CRUD操作

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

### 3. 脚本执行

使用Rhino JavaScript引擎执行用户定义的脚本：

```kotlin
fun processChatMessage(scriptId: String, message: String, sender: String): String? {
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

### 4. 异步处理

使用Kotlin协程处理异步操作，避免阻塞主线程：

```kotlin
private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

suspend fun refreshRulesCache() {
    withContext(Dispatchers.IO) {
        cachedRules = database.messageRuleDao().getEnabledRulesList()
    }
}
```

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

## 关键依赖

| 依赖库 | 版本 | 用途 |
|-------|------|------|
| Kotlin | 1.8.x | 编程语言 |
| AndroidX Core | 1.9.x | Android核心库 |
| Room | 2.5.x | 数据库ORM |
| Rhino | 1.7.14 | JavaScript引擎 |
| Material Components | 1.8.0 | UI组件库 |
| Kotlin Coroutines | 1.6.x | 异步编程 |

## 系统需求

- Android 8.0+（API级别26+）
- 微信 8.0+
- 设备需开启辅助功能权限
- 小米设备需额外配置权限（MIUI优化等）

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

## 性能优化

1. **内存优化**
   - 正确回收AccessibilityNodeInfo对象
   - 使用缓存减少数据库访问

2. **CPU优化**
   - 耗时操作放在IO线程执行
   - 使用惰性初始化延迟加载资源

3. **电池优化**
   - 使用前台服务确保长期运行
   - 减少不必要的UI更新和事件处理

## 未来技术规划

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