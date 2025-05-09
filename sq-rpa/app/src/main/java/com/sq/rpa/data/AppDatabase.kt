package com.sq.rpa.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sq.rpa.model.ChatMessage
import com.sq.rpa.model.MessageRule
import com.sq.rpa.model.ScriptInfo

/**
 * 应用数据库
 * 使用Room持久化存储
 */
@Database(
    entities = [
        MessageRule::class,
        ChatMessage::class,
        ScriptInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    // 数据访问对象
    abstract fun messageRuleDao(): MessageRuleDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun scriptInfoDao(): ScriptInfoDao
    
    companion object {
        private const val DATABASE_NAME = "sq_rpa_database"
        
        @Volatile
        private var instance: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
} 