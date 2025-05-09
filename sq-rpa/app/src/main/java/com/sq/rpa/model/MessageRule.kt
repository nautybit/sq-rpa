package com.sq.rpa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 消息规则模型
 * 用于定义消息触发条件和响应动作
 */
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
    
    // 是否启用
    val enabled: Boolean = true,
    
    // 优先级（数字越大优先级越高）
    val priority: Int = 0,
    
    // 触发延迟（毫秒）
    val delayMs: Long = 0,
    
    // 规则描述
    val description: String = "",
    
    // 创建时间
    val createTime: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updateTime: Long = System.currentTimeMillis()
) {
    companion object {
        // 匹配模式
        const val MATCH_TYPE_EXACT = 1
        const val MATCH_TYPE_CONTAINS = 2
        const val MATCH_TYPE_REGEX = 3
        const val MATCH_TYPE_SCRIPT = 4
        
        // 响应类型
        const val RESPONSE_TYPE_FIXED = 1
        const val RESPONSE_TYPE_RANDOM = 2
        const val RESPONSE_TYPE_SCRIPT = 3
    }
} 