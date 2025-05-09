# 松鼠RPA项目入门教程

## 一、项目概述

松鼠RPA是一款基于Android辅助功能服务开发的微信自动回复机器人。它能够监听微信消息，并根据预设的规则或JavaScript脚本自动回复。无需Root权限或微信插件，就能实现微信消息的自动化处理。

### 主要功能

- 实时监听微信消息
- 支持多种匹配规则：精确匹配、包含匹配、正则表达式匹配
- 支持JavaScript脚本自定义回复逻辑
- 消息历史记录和管理
- 低资源占用的后台服务

## 二、技术基础

如果您有Web开发经验，但不熟悉Android开发，以下是需要了解的基础知识：

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

### Android Jetpack组件

1. **Room**：数据持久化库，类似于Web中的ORM
2. **ViewModel**：管理UI相关数据
3. **LiveData**：可观察的数据持有者类
4. **Lifecycle**：管理Activity和Fragment的生命周期

## 三、项目结构详解

### 1. 辅助功能服务 - 核心功能实现

`RPAAccessibilityService` 是项目的核心，它利用Android的辅助功能API监听屏幕变化：

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

### 2. 规则引擎 - 消息处理逻辑

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

### 3. 脚本引擎 - 自定义逻辑

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

### 4. 数据存储 - Room数据库

项目使用Room数据库存储规则、脚本和消息历史：

```kotlin
// 消息规则模型
@Entity(tableName = "message_rules")
data class MessageRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 规则名称
    val name: String,
    
    // 匹配模式：1-精确匹配, 2-包含匹配, 3-正则匹配, 4-脚本匹配
    val matchType: Int,
    
    // 匹配内容（关键词/正则表达式）
    val matchPattern: String,
    
    // 响应类型：1-固定回复, 2-随机回复, 3-脚本回复
    val responseType: Int,
    
    // 响应内容（固定回复文本/随机回复文本，以|分隔/脚本ID）
    val responseContent: String,
    
    // 其他字段...
)
```

## 四、从Web开发者角度理解Android开发

### 对应关系

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

## 五、开始开发

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

## 六、常见开发任务

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

### 4. 调试辅助功能服务

1. 使用Logcat查看日志
2. 使用Layout Inspector分析微信界面结构
3. 使用UIAutomatorViewer工具辅助分析

## 七、开发技巧

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

4. **性能优化**：减少不必要的UI操作和数据库访问
   ```kotlin
   // 缓存规则，避免频繁查询数据库
   private var cachedRules: List<MessageRule> = emptyList()
   ```

## 八、常见问题与解决方案

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

## 九、拓展阅读

1. [Android开发者文档](https://developer.android.com/docs)
2. [Kotlin官方文档](https://kotlinlang.org/docs/home.html)
3. [Android辅助功能开发指南](https://developer.android.com/guide/topics/ui/accessibility/apps)
4. [Room持久化库](https://developer.android.com/training/data-storage/room)

---

希望这个入门教程能帮助您快速了解松鼠RPA项目的架构和开发方式。虽然Android开发与Web开发有所不同，但许多概念是相通的。随着您逐渐熟悉Android开发环境和工具，您会发现Android应用开发也可以很直观和高效。 