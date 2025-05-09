package com.sq.rpa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 脚本信息模型
 * 用于存储脚本元数据
 */
@Entity(tableName = "scripts")
data class ScriptInfo(
    @PrimaryKey
    val id: String, // 脚本唯一标识符
    
    // 脚本名称
    val name: String,
    
    // 脚本内容
    val content: String,
    
    // 脚本描述
    val description: String = "",
    
    // 脚本作者
    val author: String = "",
    
    // 创建时间
    val createTime: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updateTime: Long = System.currentTimeMillis(),
    
    // 是否启用
    val enabled: Boolean = true
) 