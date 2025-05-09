package com.sq.rpa.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.sq.rpa.core.RuleEngine
import com.sq.rpa.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.regex.Pattern

/**
 * 松鼠RPA辅助功能服务
 * 用于监听微信消息和自动回复
 */
class RPAAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "RPAAccessibilityService"
        
        // 系统中可能存在的当前服务实例
        private var instance: RPAAccessibilityService? = null
        
        // 服务状态
        var isRunning = false
            private set
        
        // 获取服务实例
        fun getInstance(): RPAAccessibilityService? = instance
        
        // 任务队列
        private val taskQueue = ConcurrentLinkedQueue<() -> Unit>()
        
        // 微信包名
        private const val WECHAT_PACKAGE = "com.tencent.mm"
        
        // 正则表达式：提取微信聊天界面标题
        private val CHAT_TITLE_PATTERN = Pattern.compile("^(.*) - 聊天$")
    }
    
    // 协程作用域
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // 主线程Handler
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // 规则引擎
    private lateinit var ruleEngine: RuleEngine
    
    // 当前聊天对象
    private var currentChatTarget: String = ""
    
    // 上次处理的消息内容，用于去重
    private var lastProcessedMessage: String = ""
    
    // 初始化
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "服务创建")
    }
    
    // 连接成功
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isRunning = true
        Log.d(TAG, "服务已连接")
        
        // 初始化规则引擎
        val database = AppDatabase.getInstance(applicationContext)
        ruleEngine = RuleEngine.getInstance(database)
        
        // 启动任务处理循环
        processTaskQueue()
    }
    
    // 断开连接
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        isRunning = false
        Log.d(TAG, "服务已销毁")
    }
    
    // 接收辅助功能事件
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName != WECHAT_PACKAGE) return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                processWechatWindowChanged(event)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (currentChatTarget.isNotEmpty()) {
                    processWechatContentChanged(event)
                }
            }
        }
    }
    
    // 处理微信窗口变化事件
    private fun processWechatWindowChanged(event: AccessibilityEvent) {
        val className = event.className?.toString() ?: return
        
        // 检测聊天窗口
        if (className.contains("LauncherUI") || className.contains("ChattingUI")) {
            val title = event.text.firstOrNull()?.toString() ?: ""
            val matcher = CHAT_TITLE_PATTERN.matcher(title)
            
            if (matcher.find()) {
                currentChatTarget = matcher.group(1) ?: ""
                Log.d(TAG, "进入聊天界面: $currentChatTarget")
            } else {
                currentChatTarget = ""
            }
        } else {
            currentChatTarget = ""
        }
    }
    
    // 处理微信内容变化事件
    private fun processWechatContentChanged(event: AccessibilityEvent) {
        if (currentChatTarget.isEmpty()) return
        
        val rootNode = rootInActiveWindow ?: return
        
        serviceScope.launch {
            try {
                // 检测聊天界面
                val chatMessages = extractChatMessages(rootNode)
                if (chatMessages.isNotEmpty()) {
                    val lastMessage = chatMessages.last()
                    
                    // 去重处理
                    if (lastMessage != lastProcessedMessage) {
                        lastProcessedMessage = lastMessage
                        
                        // 处理消息
                        Log.d(TAG, "收到消息: $lastMessage 来自: $currentChatTarget")
                        processIncomingMessage(lastMessage, currentChatTarget)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理微信事件异常", e)
            } finally {
                rootNode.recycle()
            }
        }
    }
    
    // 处理接收到的消息
    private suspend fun processIncomingMessage(message: String, sender: String) {
        // 使用规则引擎处理消息
        val result = ruleEngine.processMessage(message, sender)
        if (result != null) {
            // 延迟处理
            if (result.delay > 0) {
                delay(result.delay)
            }
            
            // 发送回复
            val success = sendTextMessage(result.reply)
            Log.d(TAG, "回复消息${if (success) "成功" else "失败"}: ${result.reply}")
        }
    }
    
    // 提取聊天消息
    private fun extractChatMessages(rootNode: AccessibilityNodeInfo): List<String> {
        val messages = mutableListOf<String>()
        
        try {
            // 尝试多种可能的ID模式（适配不同微信版本）
            val possibleIds = listOf(
                "com.tencent.mm:id/b5q",  // 当前代码中使用的ID
                "com.tencent.mm:id/ij",   // 其他版本可能的ID
                "com.tencent.mm:id/kl"    // 其他版本可能的ID
            )
            
            var messageNodes: List<AccessibilityNodeInfo> = emptyList()
            
            // 尝试不同的ID直到找到一个有效的
            for (id in possibleIds) {
                messageNodes = rootNode.findAccessibilityNodeInfosByViewId(id)
                if (messageNodes.isNotEmpty()) {
                    Log.d(TAG, "找到消息节点，使用ID: $id, 节点数: ${messageNodes.size}")
                    break
                }
            }
            
            // 如果通过ID未找到，尝试通过类型查找（备用方案）
            if (messageNodes.isEmpty()) {
                Log.d(TAG, "通过ID未找到消息节点，尝试备用方法")
                // 这里可以添加备用逻辑
            }
            
            // 处理找到的节点
            for (i in 0 until messageNodes.size) {
                val messageNode = messageNodes[i]
                
                try {
                    // 尝试多种可能的文本节点ID
                    val possibleTextIds = listOf(
                        "com.tencent.mm:id/b5r",  // 当前使用的ID
                        "com.tencent.mm:id/ik",   // 其他版本可能的ID
                        "com.tencent.mm:id/km"    // 其他版本可能的ID
                    )
                    
                    var textNodes: List<AccessibilityNodeInfo> = emptyList()
                    
                    // 尝试不同的ID
                    for (textId in possibleTextIds) {
                        textNodes = messageNode.findAccessibilityNodeInfosByViewId(textId)
                        if (textNodes.isNotEmpty()) break
                    }
                    
                    // 如果找到文本节点
                    if (textNodes.isNotEmpty()) {
                        val text = textNodes[0].text?.toString()
                        if (!text.isNullOrEmpty()) {
                            messages.add(text)
                        }
                    }
                } finally {
                    messageNode.recycle()  // 确保节点被回收
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "提取聊天消息时发生异常", e)
        }
        
        return messages
    }
    
    // 发送文本消息
    fun sendTextMessage(text: String): Boolean {
        if (!isRunning) return false
        
        // 添加到任务队列
        val success = taskQueue.offer {
            try {
                // 查找输入框
                val rootNode = rootInActiveWindow ?: return@offer
                
                // 尝试多种可能的输入框ID
                val possibleInputIds = listOf(
                    "com.tencent.mm:id/b4a",  // 当前使用的ID
                    "com.tencent.mm:id/kg",   // 其他版本可能的ID
                    "com.tencent.mm:id/ib"    // 其他版本可能的ID
                )
                
                var inputNodes: List<AccessibilityNodeInfo> = emptyList()
                
                // 尝试不同的ID
                for (id in possibleInputIds) {
                    inputNodes = rootNode.findAccessibilityNodeInfosByViewId(id)
                    if (inputNodes.isNotEmpty()) {
                        Log.d(TAG, "找到输入框节点，使用ID: $id")
                        break
                    }
                }
                
                if (inputNodes.isNotEmpty()) {
                    val inputNode = inputNodes[0]
                    
                    // 设置文本
                    val bundle = android.os.Bundle()
                    bundle.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, 
                        text
                    )
                    val textSet = inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                    
                    if (!textSet) {
                        Log.w(TAG, "无法设置文本，尝试备用方法")
                        // 可以添加备用设置文本的方法
                    }
                    
                    // 查找发送按钮
                    val possibleSendIds = listOf(
                        "com.tencent.mm:id/b4b",  // 当前使用的ID
                        "com.tencent.mm:id/kh",   // 其他版本可能的ID
                        "com.tencent.mm:id/ic"    // 其他版本可能的ID
                    )
                    
                    var sendNodes: List<AccessibilityNodeInfo> = emptyList()
                    
                    // 尝试不同的ID
                    for (id in possibleSendIds) {
                        sendNodes = rootNode.findAccessibilityNodeInfosByViewId(id)
                        if (sendNodes.isNotEmpty()) {
                            Log.d(TAG, "找到发送按钮，使用ID: $id")
                            break
                        }
                    }
                    
                    if (sendNodes.isNotEmpty()) {
                        // 点击发送
                        val clicked = sendNodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        if (clicked) {
                            Log.d(TAG, "消息已发送: $text")
                        } else {
                            Log.w(TAG, "点击发送按钮失败")
                        }
                    } else {
                        Log.w(TAG, "未找到发送按钮")
                    }
                } else {
                    Log.w(TAG, "未找到输入框")
                }
                
                rootNode.recycle()
            } catch (e: Exception) {
                Log.e(TAG, "发送消息异常", e)
            }
        }
        
        return success
    }
    
    // 点击屏幕位置
    fun clickOnScreen(x: Float, y: Float): Boolean {
        if (!isRunning) return false
        
        val path = Path()
        path.moveTo(x, y)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 50))
        
        return dispatchGesture(gestureBuilder.build(), null, null)
    }
    
    // 处理任务队列
    private fun processTaskQueue() {
        mainHandler.post(object : Runnable {
            override fun run() {
                val task = taskQueue.poll()
                if (task != null) {
                    task()
                }
                
                // 继续处理队列
                mainHandler.postDelayed(this, 100)
            }
        })
    }
    
    // 中断反馈信息
    override fun onInterrupt() {
        Log.d(TAG, "服务中断")
    }
} 