package com.sq.rpa.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sq.rpa.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * 聊天消息数据访问对象
 */
@Dao
interface ChatMessageDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessage): Long
    
    @Query("SELECT * FROM chat_messages WHERE id = :messageId")
    suspend fun getById(messageId: Long): ChatMessage?
    
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessages(limit: Int, offset: Int): List<ChatMessage>
    
    @Query("SELECT * FROM chat_messages WHERE sender = :sender ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesBySender(sender: String, limit: Int, offset: Int): List<ChatMessage>
    
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 100")
    fun getRecentMessages(): Flow<List<ChatMessage>>
    
    @Query("SELECT * FROM chat_messages WHERE type = 1 AND replied = 0 ORDER BY timestamp ASC")
    suspend fun getUnrepliedMessages(): List<ChatMessage>
    
    @Query("UPDATE chat_messages SET replied = 1, replyContent = :replyContent, ruleId = :ruleId WHERE id = :messageId")
    suspend fun markAsReplied(messageId: Long, replyContent: String, ruleId: Long?)
    
    @Query("DELETE FROM chat_messages WHERE timestamp < :timestamp")
    suspend fun deleteMessagesBefore(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getMessageCount(): Int
} 