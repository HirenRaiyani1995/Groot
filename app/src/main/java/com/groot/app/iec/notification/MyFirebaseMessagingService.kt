package com.groot.app.iec.notification

import android.app.Notification
import android.app.NotificationManager
import androidx.annotation.RequiresApi
import android.os.Build
import android.app.PendingIntent
import com.groot.app.iec.R
import com.google.firebase.messaging.FirebaseMessagingService
import org.json.JSONObject
import android.content.Intent
import com.groot.app.iec.utils.MySharedPreferences
import com.google.firebase.messaging.RemoteMessage
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var message: String? = null
    var click_action: String? = null
    var jsonObject: JSONObject? = null
    var intent: Intent? = null
    override fun onNewToken(firebaseId: String) {
        val mySharedPreferences = MySharedPreferences.getMySharedPreferences()
        mySharedPreferences!!.firebaseId = firebaseId
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data != null) {
            val notificationId = createID()
            if (remoteMessage.data.size > 0) {
                val data = remoteMessage.data
                val maindata_object = JSONObject(data as Map<*, *>)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showArrivedNotificationAPI26(message, notificationId)
                } else {
                    showArrivedNotification(message, notificationId)
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showArrivedNotificationAPI26(body: String?, notificationId: Int) {
        try {
            val contentIntent =
                PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationHelper = NotificationHelper(baseContext)
            val builder = notificationHelper.getNotification(
                getString(R.string.app_name),
                body,
                contentIntent,
                defaultSound
            )
            notificationHelper.manger?.notify(notificationId, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showArrivedNotification(body: String?, notificationId: Int) {
        val contentIntent =
            PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(
            baseContext
        )
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setAutoCancel(true)
            .setContentText(body)
            .setSound(defaultSound)
            .setContentIntent(contentIntent)
        val manager = baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (manager != null) {
            manager.notify(notificationId, builder.build())
        }
    }

    /*Create Unique ID*/
    fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }
}