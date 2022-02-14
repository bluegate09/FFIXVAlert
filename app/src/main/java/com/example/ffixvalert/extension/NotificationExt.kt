package com.example.ffixvalert.extension

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.ffixvalert.MainActivity
import com.example.ffixvalert.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val ffixvImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ffixv_big_icon
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.ffxiv_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ffixv_small_icon)
        .setContentTitle("FF14 Alert")
        .setContentText(messageBody)
        .setLargeIcon(ffixvImage)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    notify(NOTIFICATION_ID, builder.build())

}

/**
 * Cancels all notifications.
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
