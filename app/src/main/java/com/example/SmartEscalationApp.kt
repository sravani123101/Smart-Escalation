package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.local.AppDatabase

class SmartEscalationApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel 1: Scheduled Medicine Reminders
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for scheduled medicine doses"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
            }

            // Channel 2: Urgent Escalation Alerts (when dose is unconfirmed past deadline)
            val escalationChannel = NotificationChannel(
                CHANNEL_ESCALATIONS,
                "Escalation & Safety Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts when scheduled doses remain unconfirmed"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            }

            // Channel 3: Stock & Refill alerts
            val stockChannel = NotificationChannel(
                CHANNEL_STOCK,
                "Medicine Stock & Refills",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders when medicine inventory is low"
            }

            notificationManager.createNotificationChannels(
                listOf(reminderChannel, escalationChannel, stockChannel)
            )
        }
    }

    companion object {
        const val CHANNEL_REMINDERS = "channel_medicine_reminders"
        const val CHANNEL_ESCALATIONS = "channel_escalation_alerts"
        const val CHANNEL_STOCK = "channel_medicine_stock"

        lateinit var instance: SmartEscalationApp
            private set
    }
}
