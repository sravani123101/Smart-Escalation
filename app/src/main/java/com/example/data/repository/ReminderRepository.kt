package com.example.data.repository

import com.example.data.local.ReminderLogDao
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderLogEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface IReminderRepository {
    fun getAllReminders(): Flow<List<ReminderLogEntity>>
    suspend fun getReminderById(id: Long): ReminderLogEntity?
    fun getRemindersForDate(dateString: String): Flow<List<ReminderLogEntity>>
    suspend fun getRemindersForDateDirect(dateString: String): List<ReminderLogEntity>
    fun getRemindersInRange(start: Long, end: Long): Flow<List<ReminderLogEntity>>
    suspend fun getRemindersInRangeDirect(start: Long, end: Long): List<ReminderLogEntity>
    fun getRemindersForMedicine(medicineId: Long): Flow<List<ReminderLogEntity>>
    fun getDueReminders(nowMillis: Long): Flow<List<ReminderLogEntity>>
    suspend fun getUnconfirmedPastDeadline(nowMillis: Long): List<ReminderLogEntity>
    suspend fun insertReminder(reminder: ReminderLogEntity): Long
    suspend fun insertReminders(reminders: List<ReminderLogEntity>): List<Long>
    suspend fun confirmDose(id: Long, withPhoto: Boolean, photoPath: String?): Boolean
    suspend fun skipDose(id: Long, reason: String): Boolean
    suspend fun markEscalated(id: Long, contactName: String)
    suspend fun deleteReminderById(id: Long)
    suspend fun deleteRemindersForMedicine(medicineId: Long)
    suspend fun generateDailySchedulesIfMissing(medicines: List<MedicineEntity>, date: Date = Date())
}

class ReminderRepository(
    private val dao: ReminderLogDao
) : IReminderRepository {

    override fun getAllReminders(): Flow<List<ReminderLogEntity>> = dao.getAllReminders()
    override suspend fun getReminderById(id: Long): ReminderLogEntity? = dao.getReminderById(id)
    override fun getRemindersForDate(dateString: String): Flow<List<ReminderLogEntity>> = dao.getRemindersForDate(dateString)
    override suspend fun getRemindersForDateDirect(dateString: String): List<ReminderLogEntity> = dao.getRemindersForDateDirect(dateString)
    override fun getRemindersInRange(start: Long, end: Long): Flow<List<ReminderLogEntity>> = dao.getRemindersInRange(start, end)
    override suspend fun getRemindersInRangeDirect(start: Long, end: Long): List<ReminderLogEntity> = dao.getRemindersInRangeDirect(start, end)
    override fun getRemindersForMedicine(medicineId: Long): Flow<List<ReminderLogEntity>> = dao.getRemindersForMedicine(medicineId)
    override fun getDueReminders(nowMillis: Long): Flow<List<ReminderLogEntity>> = dao.getDueReminders(nowMillis)
    override suspend fun getUnconfirmedPastDeadline(nowMillis: Long): List<ReminderLogEntity> = dao.getUnconfirmedPastDeadline(nowMillis)
    override suspend fun insertReminder(reminder: ReminderLogEntity): Long = dao.insertReminder(reminder)
    override suspend fun insertReminders(reminders: List<ReminderLogEntity>): List<Long> = dao.insertReminders(reminders)

    override suspend fun confirmDose(id: Long, withPhoto: Boolean, photoPath: String?): Boolean {
        val status = if (withPhoto && !photoPath.isNullOrEmpty()) {
            ReminderLogEntity.STATUS_CONFIRMED_WITH_PHOTO
        } else {
            ReminderLogEntity.STATUS_CONFIRMED
        }
        val timestamp = System.currentTimeMillis()
        dao.confirmDose(id, status, timestamp, photoPath)
        return true
    }

    override suspend fun skipDose(id: Long, reason: String): Boolean {
        dao.skipDose(id, System.currentTimeMillis(), reason.ifBlank { "Skipped by user" })
        return true
    }

    override suspend fun markEscalated(id: Long, contactName: String) {
        dao.markEscalated(id, System.currentTimeMillis(), contactName)
    }

    override suspend fun deleteReminderById(id: Long) = dao.deleteReminderById(id)
    override suspend fun deleteRemindersForMedicine(medicineId: Long) = dao.deleteRemindersForMedicine(medicineId)

    override suspend fun generateDailySchedulesIfMissing(medicines: List<MedicineEntity>, date: Date) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val timeDisplayFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val dateString = dateFormat.format(date)

        val existingForDate = dao.getRemindersForDateDirect(dateString)
        val existingMedicineTimeKeys = existingForDate.map { "${it.medicineId}_${it.scheduledTimeString}" }.toSet()

        val cal = Calendar.getInstance()
        cal.time = date

        val toInsert = mutableListOf<ReminderLogEntity>()

        for (med in medicines) {
            if (!med.remindersEnabled) continue

            // Parse reminder times JSON array, e.g. ["08:00", "20:00"]
            val times = try {
                val jsonArr = JSONArray(med.reminderTimesJson)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArr.length()) {
                    list.add(jsonArr.getString(i))
                }
                list
            } catch (e: Exception) {
                listOf("08:00")
            }

            for (timeStr in times) {
                val parts = timeStr.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                val scheduledMillis = cal.timeInMillis
                val displayTime = timeDisplayFormat.format(cal.time)
                val key = "${med.id}_$displayTime"

                if (!existingMedicineTimeKeys.contains(key)) {
                    val deadlineMillis = scheduledMillis + (med.deadlineMinutes * 60 * 1000L)
                    toInsert.add(
                        ReminderLogEntity(
                            medicineId = med.id,
                            medicineName = med.name,
                            dosage = med.dosage,
                            scheduledTimeMillis = scheduledMillis,
                            scheduledTimeString = displayTime,
                            scheduledDateString = dateString,
                            deadlineTimeMillis = deadlineMillis,
                            status = ReminderLogEntity.STATUS_SCHEDULED
                        )
                    )
                }
            }
        }

        if (toInsert.isNotEmpty()) {
            dao.insertReminders(toInsert)
        }
    }
}
