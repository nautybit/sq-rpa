package com.sq.rpa

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sq.rpa.data.AppDatabase
import com.sq.rpa.script.ScriptEngine
import com.sq.rpa.service.RPAForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 松鼠RPA应用主类
 * 负责初始化全局资源和配置
 */
class SQRPAApplication : Application() {
    companion object {
        private const val TAG = "SQRPAApplication"
        
        // 全局应用实例
        private lateinit var instance: SQRPAApplication
        
        // 获取应用实例
        fun getInstance(): SQRPAApplication {
            return instance
        }
    }
    
    // 应用级协程作用域
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // 数据库实例
    lateinit var database: AppDatabase
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化日志
        Log.d(TAG, "应用启动")
        
        // 初始化其他全局资源
        initializeComponents()
    }
    
    /**
     * 初始化应用组件
     */
    private fun initializeComponents() {
        // 初始化数据库（添加错误处理）
        try {
            database = AppDatabase.getInstance(this)
            Log.d(TAG, "数据库初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "数据库初始化失败", e)
            // 考虑使用默认配置或提示用户
        }
        
        // 初始化脚本引擎
        val scriptEngine = ScriptEngine.getInstance()
        
        // 加载示例脚本
        appScope.launch {
            try {
                loadSampleScripts(scriptEngine)
            } catch (e: Exception) {
                Log.e(TAG, "加载示例脚本失败", e)
            }
        }
        
        // 启动前台服务
        startForegroundService()
    }
    
    /**
     * 启动前台服务
     */
    private fun startForegroundService() {
        val serviceIntent = Intent(this, RPAForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        Log.d(TAG, "前台服务已启动")
    }
    
    /**
     * 加载示例脚本
     */
    private suspend fun loadSampleScripts(scriptEngine: ScriptEngine) {
        // 添加示例脚本
        val echoScript = """
            // 简单回声脚本
            function processMessage(message, sender) {
                log.info("处理消息: " + message + " 来自: " + sender);
                return "自动回复: " + message;
            }
        """.trimIndent()
        
        scriptEngine.registerScript("echo", echoScript)
        
        // 可以添加更多示例脚本
        val timeScript = """
            // 返回当前时间的脚本
            function processMessage(message, sender) {
                var date = new Date();
                return "当前时间是: " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
            }
        """.trimIndent()
        
        scriptEngine.registerScript("time", timeScript)
        
        Log.d(TAG, "示例脚本已加载")
    }
} 