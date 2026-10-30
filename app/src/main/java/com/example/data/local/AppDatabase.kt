package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EscalationLogEntity
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.TrustedContactEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        MedicineEntity::class,
        ReminderLogEntity::class,
        TrustedContactEntity::class,
        EscalationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun reminderLogDao(): ReminderLogDao
    abstract fun trustedContactDao(): TrustedContactDao
    abstract fun escalationLogDao(): EscalationLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_escalation.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Populate sample data in background
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                seedDatabase(database)
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            // Seed Medicines
            val med1Id = db.medicineDao().insertMedicine(
                MedicineEntity(
                    name = "Metformin",
                    dosage = "500 mg",
                    medicineType = "Tablet",
                    frequency = "Twice Daily",
                    reminderTimesJson = "[\"08:00\", \"20:00\"]",
                    instructions = "Take with or right after meal with water",
                    foodRelation = "WITH_FOOD",
                    deadlineMinutes = 45,
                    remindersEnabled = true,
                    currentStock = 28,
                    lowStockThreshold = 8,
                    colorHex = "#0F2C59"
                )
            )

            val med2Id = db.medicineDao().insertMedicine(
                MedicineEntity(
                    name = "Atorvastatin",
                    dosage = "20 mg",
                    medicineType = "Tablet",
                    frequency = "Daily",
                    reminderTimesJson = "[\"21:00\"]",
                    instructions = "Take at bedtime",
                    foodRelation = "AFTER_FOOD",
                    deadlineMinutes = 60,
                    remindersEnabled = true,
                    currentStock = 14,
                    lowStockThreshold = 7,
                    colorHex = "#387478"
                )
            )

            val med3Id = db.medicineDao().insertMedicine(
                MedicineEntity(
                    name = "Vitamin D3",
                    dosage = "1000 IU",
                    medicineType = "Capsule",
                    frequency = "Daily",
                    reminderTimesJson = "[\"09:00\"]",
                    instructions = "Take in the morning with breakfast",
                    foodRelation = "WITH_FOOD",
                    deadlineMinutes = 30,
                    remindersEnabled = true,
                    currentStock = 4, // Below low stock threshold to showcase stock alert!
                    lowStockThreshold = 5,
                    colorHex = "#0284C7"
                )
            )

            // Seed Trusted Contacts
            db.trustedContactDao().insertContact(
                TrustedContactEntity(
                    name = "Dr. Robert Vance (Primary Care)",
                    phoneNumber = "+1 (555) 432-8765",
                    relationship = "Doctor",
                    escalationPriority = 1,
                    isEscalationEnabled = true,
                    consentGranted = true,
                    notes = "Family clinic on Maple Street"
                )
            )

            db.trustedContactDao().insertContact(
                TrustedContactEntity(
                    name = "Elena Vance (Daughter)",
                    phoneNumber = "+1 (555) 987-6543",
                    relationship = "Family",
                    escalationPriority = 2,
                    isEscalationEnabled = true,
                    consentGranted = true,
                    notes = "Emergency contact & caregiver"
                )
            )

            // Seed today's and past reminder logs for realistic demonstration
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayStr = dateFormat.format(Date())
            val cal = Calendar.getInstance()

            // Today 08:00 AM Metformin - Confirmed with Photo
            cal.set(Calendar.HOUR_OF_DAY, 8)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val dose1Time = cal.timeInMillis
            db.reminderLogDao().insertReminder(
                ReminderLogEntity(
                    medicineId = med1Id,
                    medicineName = "Metformin",
                    dosage = "500 mg",
                    scheduledTimeMillis = dose1Time,
                    scheduledTimeString = "08:00 AM",
                    scheduledDateString = todayStr,
                    deadlineTimeMillis = dose1Time + (45 * 60 * 1000),
                    status = ReminderLogEntity.STATUS_CONFIRMED_WITH_PHOTO,
                    confirmationTimeMillis = dose1Time + (12 * 60 * 1000),
                    notes = "Confirmed on time with strip photo."
                )
            )

            // Today 09:00 AM Vitamin D3 - Confirmed
            cal.set(Calendar.HOUR_OF_DAY, 9)
            val dose2Time = cal.timeInMillis
            db.reminderLogDao().insertReminder(
                ReminderLogEntity(
                    medicineId = med3Id,
                    medicineName = "Vitamin D3",
                    dosage = "1000 IU",
                    scheduledTimeMillis = dose2Time,
                    scheduledTimeString = "09:00 AM",
                    scheduledDateString = todayStr,
                    deadlineTimeMillis = dose2Time + (30 * 60 * 1000),
                    status = ReminderLogEntity.STATUS_CONFIRMED,
                    confirmationTimeMillis = dose2Time + (5 * 60 * 1000)
                )
            )

            // Today 20:00 (8:00 PM) Metformin - Pending / Upcoming
            cal.set(Calendar.HOUR_OF_DAY, 20)
            val dose3Time = cal.timeInMillis
            db.reminderLogDao().insertReminder(
                ReminderLogEntity(
                    medicineId = med1Id,
                    medicineName = "Metformin",
                    dosage = "500 mg",
                    scheduledTimeMillis = dose3Time,
                    scheduledTimeString = "08:00 PM",
                    scheduledDateString = todayStr,
                    deadlineTimeMillis = dose3Time + (45 * 60 * 1000),
                    status = ReminderLogEntity.STATUS_PENDING
                )
            )

            // Today 21:00 (9:00 PM) Atorvastatin - Scheduled
            cal.set(Calendar.HOUR_OF_DAY, 21)
            val dose4Time = cal.timeInMillis
            db.reminderLogDao().insertReminder(
                ReminderLogEntity(
                    medicineId = med2Id,
                    medicineName = "Atorvastatin",
                    dosage = "20 mg",
                    scheduledTimeMillis = dose4Time,
                    scheduledTimeString = "09:00 PM",
                    scheduledDateString = todayStr,
                    deadlineTimeMillis = dose4Time + (60 * 60 * 1000),
                    status = ReminderLogEntity.STATUS_SCHEDULED
                )
            )

            // Also seed one unconfirmed escalated sample from yesterday so Escalation History and Adherence analytics display real data immediately
            val calYesterday = Calendar.getInstance()
            calYesterday.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateFormat.format(calYesterday.time)
            calYesterday.set(Calendar.HOUR_OF_DAY, 21)
            val pastTime = calYesterday.timeInMillis

            val pastReminderId = db.reminderLogDao().insertReminder(
                ReminderLogEntity(
                    medicineId = med2Id,
                    medicineName = "Atorvastatin",
                    dosage = "20 mg",
                    scheduledTimeMillis = pastTime,
                    scheduledTimeString = "09:00 PM",
                    scheduledDateString = yesterdayStr,
                    deadlineTimeMillis = pastTime + (60 * 60 * 1000),
                    status = ReminderLogEntity.STATUS_UNCONFIRMED,
                    isEscalated = true,
                    escalatedAtMillis = pastTime + (60 * 60 * 1000),
                    escalatedToContactName = "Dr. Robert Vance"
                )
            )

            db.escalationLogDao().insertLog(
                EscalationLogEntity(
                    reminderId = pastReminderId,
                    medicineName = "Atorvastatin",
                    dosage = "20 mg",
                    scheduledTime = "09:00 PM",
                    contactId = 1,
                    contactName = "Dr. Robert Vance",
                    contactPhone = "+1 (555) 432-8765",
                    alertMessage = "SMART ESCALATION ALERT: Scheduled dose of Atorvastatin (20 mg) for Alex Chen at 09:00 PM remained unconfirmed past the 60-min deadline. Please check on patient safety.",
                    triggeredAtMillis = pastTime + (60 * 60 * 1000),
                    status = "ALERT_PREPARED",
                    channel = "SMS_INTENT"
                )
            )
        }
    }
}
