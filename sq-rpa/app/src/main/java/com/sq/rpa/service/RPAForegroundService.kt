package com.sq.rpa.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sq.rpa.R
import com.sq.rpa.ui.MainActivity

/**
 * 松鼠RPA前台服务
 * 保持应用在后台运行，不被系统杀死
 */
class RPAForegroundService : Service() {
    companion object {
        private const val TAG = "RPAForegroundService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "sq_rpa_foreground_channel"
        private const val CHANNEL_NAME = "松鼠RPA服务"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "服务创建")
        
        // 创建通知渠道（Android 8.0+需要）
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "服务启动")
        
        // 创建前台服务通知
        val notification = createNotification()
        
        // 将服务设为前台
        startForeground(NOTIFICATION_ID, notification)
        
        // 如果服务被杀死，系统将尝试重新创建服务
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "服务销毁")
    }
    
    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "保持松鼠RPA在后台运行"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 创建前台服务通知
     */
    private fun createNotification(): Notification {
        // 创建点击通知后打开应用的意图
        val contentIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // 构建通知
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("松鼠RPA正在运行")
            .setContentText("松鼠RPA正在后台监听微信消息")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
} 