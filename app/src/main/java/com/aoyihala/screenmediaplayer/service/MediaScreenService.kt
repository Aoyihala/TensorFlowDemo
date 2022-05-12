package com.aoyihala.screenmediaplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.aoyihala.screenmediaplayer.R


/**
 * 作者:夏涛
 * 创建日期:2022/5/9 10:38
 * 描述:
 **/
class MediaScreenService:Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("test","服务",NotificationManager.IMPORTANCE_MIN)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
            val notification: Notification =
                Notification.Builder(this, "test")
                    .setTicker("Nature")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("这是一个测试标题")
                    .setContentText("这是一个测试内容")
                    .build()
            startForeground(2,notification)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}