package com.groot.app.iec.notification

import android.app.Notification
import android.content.ContextWrapper
import android.app.NotificationManager
import androidx.annotation.RequiresApi
import android.os.Build
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import com.groot.app.iec.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var manager: NotificationManager? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannels() {
        val notifyChannels = NotificationChannel(
            CHANEL_ID, CHANEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notifyChannels.enableLights(false)
        notifyChannels.enableVibration(true)
        notifyChannels.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manger!!.createNotificationChannel(notifyChannels)
    }

    val manger: NotificationManager?
        get() {
            if (manager == null) manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            return manager
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotification(
        title: String?,
        content: String?,
        contentIntent: PendingIntent?,
        soundUri: Uri?
    ): Notification.Builder {
        return Notification.Builder(applicationContext, CHANEL_ID)
            .setContentText(content)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(500, 500))
            .setContentIntent(contentIntent)
            .setSmallIcon(smallIcon)
            .setLargeIcon(largeIcon)
    }

    private val smallIcon: Int
        private get() = R.mipmap.ic_launcher
    private val largeIcon: Bitmap
        private get() = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

    companion object {
        private const val CHANEL_ID = "com.groot.app"
        private const val CHANEL_NAME = "Groot"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannels()
    }
}