package com.example.companionek.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.companionek.MainActivity
import com.example.companionek.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM:  ", "Token: $token")
        // Send the token to your server or save it in the database to send targeted notifications
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM:  ", "Message: "+remoteMessage.notification?.body)

        // If the message contains a data payload, process it as a notification
        remoteMessage.data.isNotEmpty().let {
            sendNotification(remoteMessage)
        }
    }

//    private fun sendNotification(remoteMessage: RemoteMessage) {
//        val title = remoteMessage.data["title"]
//        val message = remoteMessage.data["message"]
//
//        // Create an intent for opening the app when the notification is clicked
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        // Define a Notification Channel for Android 8.0 and above
//        val channelId = "CompanionEK_Channel"
//        val channelName = "CompanionEK Notifications"
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Create and display the notification
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.logo) // replace with your notification icon
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        with(NotificationManagerCompat.from(this)) {
//            if (ActivityCompat.checkSelfPermission(
//                    this@MyFirebaseMessaging,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return
//            }
//            notify(System.currentTimeMillis().toInt(), notification)
//        }
//    }
private fun sendNotification(remoteMessage: RemoteMessage) {
    val senderName = remoteMessage.data["senderName"] ?: "New Message"
    val message = remoteMessage.data["message"]

    // Create an intent for opening the app when the notification is clicked
    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Define a Notification Channel for Android 8.0 and above
    val channelId = "CompanionEK_Channel"
    val channelName = "CompanionEK Notifications"
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    // Create and display the notification
    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.logo) // replace with your notification icon
        .setContentTitle(senderName) // Display sender's name as the title
        .setContentText(message) // Display message text as the content
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    with(NotificationManagerCompat.from(this)) {
        if (ActivityCompat.checkSelfPermission(
                this@MyFirebaseMessaging,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider requesting permissions here
            return
        }
        notify(System.currentTimeMillis().toInt(), notification)
    }
}


}

