package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReminderLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderLogDao {
    @Query("SELECT * FROM reminder_logs ORDER BY scheduledTimeMillis ASC")
    fun getAllReminders(): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Long): ReminderLogEntity?

    @Query("SELECT * FROM reminder_logs WHERE scheduledDateString = :dateString ORDER BY scheduledTimeMillis ASC")
    fun getRemindersForDate(dateString: String): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE scheduledDateString = :dateString ORDER BY scheduledTimeMillis ASC")
    suspend fun getRemindersForDateDirect(dateString: String): List<ReminderLogEntity>

    @Query("SELECT * FROM reminder_logs WHERE scheduledTimeMillis >= :startTimeMillis AND scheduledTimeMillis <= :endTimeMillis ORDER BY scheduledTimeMillis ASC")
    fun getRemindersInRange(startTimeMillis: Long, endTimeMillis: Long): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE scheduledTimeMillis >= :startTimeMillis AND scheduledTimeMillis <= :endTimeMillis ORDER BY scheduledTimeMillis ASC")
    suspend fun getRemindersInRangeDirect(startTimeMillis: Long, endTimeMillis: Long): List<ReminderLogEntity>

    @Query("SELECT * FROM reminder_logs WHERE medicineId = :medicineId ORDER BY scheduledTimeMillis DESC")
    fun getRemindersForMedicine(medicineId: Long): Flow<List<ReminderLogEntity>>

    @Query("""
        SELECT * FROM reminder_logs 
        WHERE status IN ('SCHEDULED', 'PENDING') 
        AND scheduledTimeMillis <= :nowMillis 
        ORDER BY scheduledTimeMillis ASC
    """)
    fun getDueReminders(nowMillis: Long): Flow<List<ReminderLogEntity>>

    @Query("""
        SELECT * FROM reminder_logs 
        WHERE status IN ('SCHEDULED', 'PENDING') 
        AND deadlineTimeMillis <= :nowMillis 
        AND isEscalated = 0
        ORDER BY deadlineTimeMillis ASC
    """)
    suspend fun getUnconfirmedPastDeadline(nowMillis: Long): List<ReminderLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderLogEntity>): List<Long>

    @Update
    suspend fun updateReminder(reminder: ReminderLogEntity)

    @Query("""
        UPDATE reminder_logs 
        SET status = :status, 
            confirmationTimeMillis = :confirmationTime, 
            photoPath = :photoPath 
        WHERE id = :id
    """)
    suspend fun confirmDose(id: Long, status: String, confirmationTime: Long, photoPath: String?)

    @Query("""
        UPDATE reminder_logs 
        SET status = 'SKIPPED', 
            confirmationTimeMillis = :timestamp, 
            skipReason = :reason 
        WHERE id = :id
    """)
    suspend fun skipDose(id: Long, timestamp: Long, reason: String)

    @Query("""
        UPDATE reminder_logs 
        SET status = 'UNCONFIRMED', 
            isEscalated = 1, 
            escalatedAtMillis = :escalatedAt, 
            escalatedToContactName = :contactName 
        WHERE id = :id
    """)
    suspend fun markEscalated(id: Long, escalatedAt: Long, contactName: String)

    @Query("DELETE FROM reminder_logs WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("DELETE FROM reminder_logs WHERE medicineId = :medicineId")
    suspend fun deleteRemindersForMedicine(medicineId: Long)

    @Query("DELETE FROM reminder_logs")
    suspend fun clearAllReminders()
}
