package com.sq.rpa.core

import android.util.Log
import com.sq.rpa.data.AppDatabase
import com.sq.rpa.model.ChatMessage
import com.sq.rpa.model.MessageRule
import com.sq.rpa.script.ScriptEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Random
import java.util.regex.Pattern

/**
 * 规则引擎
 * 负责消息匹配和响应处理
 */
class RuleEngine private constructor(private val database: AppDatabase) {
    companion object {
        private const val TAG = "RuleEngine"
        
        @Volatile
        private var instance: RuleEngine? = null
        
        fun getInstance(database: AppDatabase): RuleEngine {
            return instance ?: synchronized(this) {
                instance ?: RuleEngine(database).also { instance = it }
            }
        }
    }
    
    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val scriptEngine = ScriptEngine.getInstance()
    private val random = Random()
    
    // 缓存已启用的规则
    private var cachedRules: List<MessageRule> = emptyList()
    
    init {
        // 初始化时加载规则缓存
        engineScope.launch {
            refreshRulesCache()
        }
    }
    
    /**
     * 刷新规则缓存
     */
    suspend fun refreshRulesCache() {
        withContext(Dispatchers.IO) {
            try {
                cachedRules = database.messageRuleDao().getEnabledRulesList()
                Log.d(TAG, "规则缓存已刷新，共 ${cachedRules.size} 条规则")
            } catch (e: Exception) {
                Log.e(TAG, "刷新规则缓存失败", e)
            }
        }
    }
    
    /**
     * 处理收到的消息
     * @param message 消息内容
     * @param sender 发送者
     * @return 回复内容，null表示不需要回复
     */
    suspend fun processMessage(message: String, sender: String): ProcessResult? {
        return withContext(Dispatchers.IO) {
            try {
                if (cachedRules.isEmpty()) {
                    Log.w(TAG, "没有启用的规则，刷新规则缓存")
                    refreshRulesCache()
                    if (cachedRules.isEmpty()) {
                        Log.i(TAG, "规则列表为空，无法处理消息")
                        return@withContext null
                    }
                }
                
                Log.d(TAG, "处理消息: '$message' 来自: '$sender'")
                
                // 记录接收到的消息
                val chatMessage = ChatMessage(
                    sender = sender,
                    content = message,
                    type = ChatMessage.TYPE_RECEIVED
                )
                val messageId = database.chatMessageDao().insert(chatMessage)
                Log.d(TAG, "消息已保存，ID: $messageId")
                
                // 匹配规则
                val matchedRule = findMatchingRule(message)
                if (matchedRule != null) {
                    Log.d(TAG, "匹配到规则: ${matchedRule.id} - ${matchedRule.name}")
                    
                    // 生成回复
                    val reply = generateReply(matchedRule, message, sender)
                    if (reply != null) {
                        Log.d(TAG, "生成回复: $reply")
                        
                        // 更新消息状态为已回复
                        database.chatMessageDao().markAsReplied(
                            messageId = messageId,
                            replyContent = reply,
                            ruleId = matchedRule.id
                        )
                        
                        return@withContext ProcessResult(
                            reply = reply,
                            rule = matchedRule,
                            delay = matchedRule.delayMs
                        )
                    } else {
                        Log.d(TAG, "规则未生成回复")
                    }
                } else {
                    Log.d(TAG, "没有匹配的规则")
                }
                
                // 没有匹配的规则或不需要回复
                null
            } catch (e: Exception) {
                Log.e(TAG, "处理消息异常: ${e.message}", e)
                
                // 尝试恢复性处理
                try {
                    // 刷新规则缓存，可能是缓存不一致导致的问题
                    refreshRulesCache()
                } catch (refreshEx: Exception) {
                    Log.e(TAG, "刷新规则缓存失败", refreshEx)
                }
                
                null
            }
        }
    }
    
    /**
     * 查找匹配的规则
     */
    private fun findMatchingRule(message: String): MessageRule? {
        if (message.isBlank()) {
            Log.w(TAG, "消息内容为空，跳过规则匹配")
            return null
        }
        
        // 按优先级排序的规则已经在缓存中排好序
        Log.d(TAG, "开始匹配规则，共 ${cachedRules.size} 条规则")
        
        for (rule in cachedRules) {
            try {
                if (matchRule(rule, message)) {
                    Log.d(TAG, "规则匹配成功: ${rule.id} - ${rule.name}")
                    return rule
                }
            } catch (e: Exception) {
                Log.e(TAG, "规则匹配异常: ${rule.id} - ${rule.name}", e)
                // 继续检查下一个规则
            }
        }
        
        Log.d(TAG, "没有匹配的规则")
        return null
    }
    
    /**
     * 判断消息是否匹配规则
     */
    private fun matchRule(rule: MessageRule, message: String): Boolean {
        return when (rule.matchType) {
            MessageRule.MATCH_TYPE_EXACT -> {
                message == rule.matchPattern
            }
            MessageRule.MATCH_TYPE_CONTAINS -> {
                message.contains(rule.matchPattern)
            }
            MessageRule.MATCH_TYPE_REGEX -> {
                try {
                    val pattern = Pattern.compile(rule.matchPattern)
                    pattern.matcher(message).find()
                } catch (e: Exception) {
                    Log.e(TAG, "正则表达式匹配异常: ${rule.id}", e)
                    false
                }
            }
            MessageRule.MATCH_TYPE_SCRIPT -> {
                // 脚本匹配暂不支持
                false
            }
            else -> false
        }
    }
    
    /**
     * 根据规则生成回复
     */
    private suspend fun generateReply(rule: MessageRule, message: String, sender: String): String? {
        return when (rule.responseType) {
            MessageRule.RESPONSE_TYPE_FIXED -> {
                rule.responseContent
            }
            MessageRule.RESPONSE_TYPE_RANDOM -> {
                val options = rule.responseContent.split("|")
                if (options.isNotEmpty()) {
                    options[random.nextInt(options.size)]
                } else {
                    null
                }
            }
            MessageRule.RESPONSE_TYPE_SCRIPT -> {
                // 执行脚本生成回复
                val scriptId = rule.responseContent
                try {
                    scriptEngine.processChatMessage(scriptId, message, sender)
                } catch (e: Exception) {
                    Log.e(TAG, "执行脚本异常: $scriptId", e)
                    null
                }
            }
            else -> null
        }
    }
    
    /**
     * 处理结果
     */
    data class ProcessResult(
        val reply: String,
        val rule: MessageRule,
        val delay: Long
    )
} 