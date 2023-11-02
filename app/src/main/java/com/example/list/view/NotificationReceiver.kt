package com.example.list.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.list.R

class NotificationReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Log.d("NotificationReceiver", "Received notification intent")
    val title = intent.getStringExtra("title")
    Log.d("NotificationReceiver", "Notification title: $title")
    val content = intent.getStringExtra("content") ?: "Your item is due soon!"
    showNotification(context, title ?: "Item", content)
    Log.d("NotificationReceiver", "Notification content: $content")
  }

  private fun showNotification(context: Context, title: String, content: String) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channelId = "default_channel"
      val channel = NotificationChannel(
        channelId,
        "Default Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      )
      notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "default_channel")
      .setContentTitle(title)
      .setContentText(content)
      .setSmallIcon(R.drawable.ic_launcher)
      .build()

    notificationManager.notify(1, notification)
  }
}
