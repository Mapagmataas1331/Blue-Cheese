package com.example.list.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.list.R

class NotificationReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val title = intent.getStringExtra("title")
    val date = intent.getStringExtra("date")
    title?.let { showNotification(context, it, "Your item will due $date!", "description") }
  }

  companion object {
    fun showNotification(context: Context, title: String, content: String, description: String) {
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val channelId = "default_channel"

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
          channelId,
          description,
          NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
      }

      val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.ic_launcher)
        .setLargeIcon(
          BitmapFactory.decodeResource(
            context.resources, R.drawable
              .ic_launcher
          )
        )
      notificationManager.notify(1, notification.build())
    }
  }
}
