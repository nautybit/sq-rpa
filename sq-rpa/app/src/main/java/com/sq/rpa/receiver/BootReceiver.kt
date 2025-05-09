package com.sq.rpa.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat
import com.sq.rpa.service.RPAForegroundService

/**
 * 开机启动广播接收器
 * 当设备启动完成时，检查是否启用了自启动功能，并启动前台服务
 */
class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
        private const val PREF_NAME = "sq_rpa_settings"
        private const val KEY_AUTO_START = "auto_start"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "系统启动完成")
            
            // 检查是否启用了自启动
            val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val autoStart = prefs.getBoolean(KEY_AUTO_START, false)
            
            if (autoStart) {
                Log.d(TAG, "自启动已启用，启动服务")
                
                // 启动前台服务
                val serviceIntent = Intent(context, RPAForegroundService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                Log.d(TAG, "自启动未启用")
            }
        }
    }
} 