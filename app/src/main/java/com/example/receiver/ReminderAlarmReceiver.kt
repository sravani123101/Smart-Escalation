package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.model.ReminderLogEntity
import com.example.data.repository.EscalationRepository
import com.example.service.NotificationHelper
import com.example.service.ReminderAlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val action = intent.action ?: return
        val reminderId = intent.getLongExtra("EXTRA_REMINDER_ID", -1L)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val reminderDao = db.reminderLogDao()
                val medicineDao = db.medicineDao()
                val contactDao = db.trustedContactDao()
                val escalationDao = db.escalationLogDao()

                when (action) {
                    ACTION_MEDICINE_REMINDER -> {
                        if (reminderId > 0) {
                            val reminder = reminderDao.getReminderById(reminderId)
                            if (reminder != null && !reminder.isConfirmed && !reminder.isSkipped) {
                                val medicine = medicineDao.getMedicineById(reminder.medicineId)
                                val instructions = medicine?.instructions ?: ""

                                // Update status to PENDING if was SCHEDULED
                                if (reminder.status == ReminderLogEntity.STATUS_SCHEDULED) {
                                    reminderDao.updateReminder(reminder.copy(status = ReminderLogEntity.STATUS_PENDING))
                                }

                                NotificationHelper.showMedicineReminderNotification(
                                    context,
                                    reminder,
                                    instructions
                                )
                            }
                        }
                    }

                    ACTION_MEDICINE_DEADLINE -> {
                        if (reminderId > 0) {
                            val reminder = reminderDao.getReminderById(reminderId)
                            // If still not confirmed and not skipped, and not yet escalated
                            if (reminder != null && !reminder.isConfirmed && !reminder.isSkipped && !reminder.isEscalated) {
                                val activeContacts = contactDao.getActiveEscalationContactsDirect()
                                val primaryContact = activeContacts.firstOrNull()

                                val contactName = primaryContact?.name ?: "Emergency Caregiver"
                                val contactPhone = primaryContact?.phoneNumber ?: ""

                                // Mark reminder as UNCONFIRMED and escalated
                                reminderDao.markEscalated(
                                    reminder.id,
                                    System.currentTimeMillis(),
                                    contactName
                                )

                                // Insert Escalation Log
                                val escalationRepo = EscalationRepository(escalationDao)
                                if (primaryContact != null) {
                                    escalationRepo.createEscalation(
                                        reminder = reminder,
                                        contact = primaryContact,
                                        patientName = "Alex Chen"
                                    )
                                }

                                // Show Critical Escalation Notification
                                NotificationHelper.showEscalationAlertNotification(
                                    context,
                                    reminder,
                                    contactName,
                                    contactPhone
                                )
                            }
                        }
                    }

                    ACTION_CONFIRM_DIRECT -> {
                        if (reminderId > 0) {
                            val reminder = reminderDao.getReminderById(reminderId)
                            if (reminder != null && !reminder.isConfirmed) {
                                reminderDao.confirmDose(
                                    reminderId,
                                    ReminderLogEntity.STATUS_CONFIRMED,
                                    System.currentTimeMillis(),
                                    null
                                )
                                medicineDao.decrementStock(reminder.medicineId)
                                NotificationHelper.dismissNotification(context, reminderId.toInt())

                                // Check remaining stock
                                val updatedMed = medicineDao.getMedicineById(reminder.medicineId)
                                if (updatedMed != null && updatedMed.currentStock <= updatedMed.lowStockThreshold) {
                                    NotificationHelper.showLowStockNotification(
                                        context,
                                        updatedMed.name,
                                        updatedMed.currentStock,
                                        updatedMed.unit
                                    )
                                }
                            }
                        }
                    }

                    ACTION_SNOOZE_REMINDER -> {
                        if (reminderId > 0) {
                            ReminderAlarmScheduler.scheduleSnoozeAlarm(context, reminderId, 10)
                            NotificationHelper.dismissNotification(context, reminderId.toInt())
                        }
                    }

                    Intent.ACTION_BOOT_COMPLETED -> {
                        // Reschedule all upcoming reminders
                        val now = System.currentTimeMillis()
                        val activeReminders = reminderDao.getUnconfirmedPastDeadline(now + 24 * 60 * 60 * 1000L)
                        for (r in activeReminders) {
                            ReminderAlarmScheduler.scheduleReminderAlarm(context, r)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_MEDICINE_REMINDER = "com.example.ACTION_MEDICINE_REMINDER"
        const val ACTION_MEDICINE_DEADLINE = "com.example.ACTION_MEDICINE_DEADLINE"
        const val ACTION_CONFIRM_DIRECT = "com.example.ACTION_CONFIRM_DIRECT"
        const val ACTION_SNOOZE_REMINDER = "com.example.ACTION_SNOOZE_REMINDER"
    }
}
