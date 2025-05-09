package com.sq.rpa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 聊天消息模型
 * 用于存储历史消息记录
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 发送者（联系人/群名称）
    val sender: String,
    
    // 消息内容
    val content: String,
    
    // 消息类型：1-接收, 2-发送
    val type: Int,
    
    // 是否已回复
    val replied: Boolean = false,
    
    // 回复内容
    val replyContent: String? = null,
    
    // 触发的规则ID
    val ruleId: Long? = null,
    
    // 接收时间
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_RECEIVED = 1
        const val TYPE_SENT = 2
    }
} 