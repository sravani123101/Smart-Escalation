package com.example.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.SmartEscalationApp
import com.example.data.model.ReminderLogEntity
import com.example.receiver.ReminderAlarmReceiver

object NotificationHelper {

    fun showMedicineReminderNotification(
        context: Context,
        reminder: ReminderLogEntity,
        instructions: String = ""
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Content intent to open MainActivity with reminder ID
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_REMINDER_ID", reminder.id)
            putExtra("EXTRA_NAV_TARGET", "reminder_dialog")
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Action 1: Confirm Dose (Quick 1-tap confirmation directly from notification)
        val confirmIntent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_CONFIRM_DIRECT
            putExtra("EXTRA_REMINDER_ID", reminder.id)
        }
        val confirmPendingIntent = PendingIntent.getBroadcast(
            context,
            (reminder.id * 10 + 1).toInt(),
            confirmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Action 2: Snooze 10 minutes
        val snoozeIntent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_SNOOZE_REMINDER
            putExtra("EXTRA_REMINDER_ID", reminder.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminder.id * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val instructionText = if (instructions.isNotBlank()) "\nInstructions: $instructions" else ""

        val notification = NotificationCompat.Builder(context, SmartEscalationApp.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Medicine Reminder: ${reminder.medicineName}")
            .setContentText("Scheduled for ${reminder.scheduledTimeString} • ${reminder.dosage}")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Time to take your scheduled dose of ${reminder.medicineName} (${reminder.dosage}).$instructionText\n\nSafety Deadline: Please confirm within the next safety window to prevent trusted contact escalation."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Confirm Dose", confirmPendingIntent)
            .addAction(android.R.drawable.ic_popup_sync, "Snooze 10m", snoozePendingIntent)
            .build()

        notificationManager.notify(reminder.id.toInt(), notification)
    }

    fun showEscalationAlertNotification(
        context: Context,
        reminder: ReminderLogEntity,
        contactName: String,
        contactPhone: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAV_TARGET", "escalations")
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            (reminder.id * 100).toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SmartEscalationApp.CHANNEL_ESCALATIONS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Safety Escalation Triggered!")
            .setContentText("${reminder.medicineName} dose unconfirmed. Alert prepared for $contactName.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "URGENT: Your dose of ${reminder.medicineName} (${reminder.dosage}) scheduled for ${reminder.scheduledTimeString} was not confirmed before its safety deadline.\n\nAn escalation alert has been prepared for your trusted contact: $contactName ($contactPhone). Tap to review or send."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        notificationManager.notify((reminder.id + 10000).toInt(), notification)
    }

    fun showLowStockNotification(
        context: Context,
        medicineName: String,
        remainingStock: Int,
        unit: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAV_TARGET", "stock")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicineName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SmartEscalationApp.CHANNEL_STOCK)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Low Medicine Stock: $medicineName")
            .setContentText("Only $remainingStock $unit remaining. Please refill soon.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(medicineName.hashCode(), notification)
    }

    fun dismissNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
