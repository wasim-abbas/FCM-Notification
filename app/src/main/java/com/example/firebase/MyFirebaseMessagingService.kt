package com.example.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.media.RingtoneManager
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val channleID = "channle id"
        const val channleNAme = " channle Name"
    }

    //generate the notification
    // attach notification with custom layout
    // show the notification
    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage!!.notification!!.body!!
            )
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                Log.d("ok", "token successfully retrieved : $token")
            } else {
                Log.w("ok", "token should not be null...")
            }
        }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                Log.v("ok", "This is the token : " + task.result)
            }
    }


    fun getRemoteView(title: String, msg: String): RemoteViews {
        // pakage name notification lay out

        val remoteViews = RemoteViews("com.example.firebase", R.layout.notification)
        remoteViews.setTextViewText(R.id.tvNotification, title)
        remoteViews.setTextViewText(R.id.tvDescription, msg)
        remoteViews.setImageViewResource(R.id.notificationIconID, R.drawable.ic_notifications)
        return remoteViews

    }

    /// Custom Notification
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateNotification(title: String, msg: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)  // clear all activity and put this activity on top
        val pendingActivity = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )  // Flag oneShot meant to use pendng inetent only once

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //channleiD, channelName
        var notification: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channleID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000))  /// 1000ms for sleep and  for sleep
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingActivity)
                .setSound(alarmSound)

        notification = notification.setContent(getRemoteView(title, msg))
        val notificationMAnager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannle =
                NotificationChannel(channleID, channleNAme, NotificationManager.IMPORTANCE_HIGH)
            notificationMAnager.createNotificationChannel(notificationChannle)
        }
        notificationMAnager.notify(0, notification.build())

    }


}