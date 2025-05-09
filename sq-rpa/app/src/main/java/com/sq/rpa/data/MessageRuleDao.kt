package com.sq.rpa.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sq.rpa.model.MessageRule
import kotlinx.coroutines.flow.Flow

/**
 * 消息规则数据访问对象
 */
@Dao
interface MessageRuleDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: MessageRule): Long
    
    @Update
    suspend fun update(rule: MessageRule)
    
    @Delete
    suspend fun delete(rule: MessageRule)
    
    @Query("DELETE FROM message_rules WHERE id = :ruleId")
    suspend fun deleteById(ruleId: Long)
    
    @Query("SELECT * FROM message_rules WHERE id = :ruleId")
    suspend fun getById(ruleId: Long): MessageRule?
    
    @Query("SELECT * FROM message_rules ORDER BY priority DESC, id ASC")
    fun getAllRules(): Flow<List<MessageRule>>
    
    @Query("SELECT * FROM message_rules WHERE enabled = 1 ORDER BY priority DESC, id ASC")
    fun getEnabledRules(): Flow<List<MessageRule>>
    
    @Query("SELECT * FROM message_rules WHERE enabled = 1 ORDER BY priority DESC, id ASC")
    suspend fun getEnabledRulesList(): List<MessageRule>
    
    @Query("UPDATE message_rules SET enabled = :enabled WHERE id = :ruleId")
    suspend fun updateRuleEnabled(ruleId: Long, enabled: Boolean)
    
    @Query("UPDATE message_rules SET priority = :priority WHERE id = :ruleId")
    suspend fun updateRulePriority(ruleId: Long, priority: Int)
} 