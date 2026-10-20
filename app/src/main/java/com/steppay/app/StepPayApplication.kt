package com.steppay.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepPayApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        // Schedule daily recurring reminder notification
        com.steppay.app.worker.DailyReminderScheduler.scheduleDailyReminder(this)

        // Initialize Mobile Ads SDK asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@StepPayApplication)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(1024 * 1024 * 50) // 50 MB disk cache
                    .build()
            }
            .crossfade(true)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val trackingChannel = NotificationChannel(
                TRACKING_CHANNEL_ID,
                "StepPay Activity Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Active notification while step and GPS route tracking is running"
            }

            val reminderChannel = NotificationChannel(
                DAILY_REMINDER_CHANNEL_ID,
                "StepPay Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to walk, earn coins, and keep your login streak active"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(trackingChannel)
            notificationManager?.createNotificationChannel(reminderChannel)
        }
    }

    companion object {
        const val TRACKING_CHANNEL_ID = "steppay_tracking_channel"
        const val DAILY_REMINDER_CHANNEL_ID = "steppay_daily_reminder_channel"
    }
}
