package com.sq.rpa.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sq.rpa.model.ScriptInfo
import kotlinx.coroutines.flow.Flow

/**
 * 脚本信息数据访问对象
 */
@Dao
interface ScriptInfoDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scriptInfo: ScriptInfo)
    
    @Update
    suspend fun update(scriptInfo: ScriptInfo)
    
    @Delete
    suspend fun delete(scriptInfo: ScriptInfo)
    
    @Query("DELETE FROM scripts WHERE id = :scriptId")
    suspend fun deleteById(scriptId: String)
    
    @Query("SELECT * FROM scripts WHERE id = :scriptId")
    suspend fun getById(scriptId: String): ScriptInfo?
    
    @Query("SELECT * FROM scripts ORDER BY name ASC")
    fun getAllScripts(): Flow<List<ScriptInfo>>
    
    @Query("SELECT * FROM scripts WHERE enabled = 1 ORDER BY name ASC")
    fun getEnabledScripts(): Flow<List<ScriptInfo>>
    
    @Query("SELECT * FROM scripts WHERE enabled = 1 ORDER BY name ASC")
    suspend fun getEnabledScriptsList(): List<ScriptInfo>
    
    @Query("UPDATE scripts SET enabled = :enabled WHERE id = :scriptId")
    suspend fun updateScriptEnabled(scriptId: String, enabled: Boolean)
} 