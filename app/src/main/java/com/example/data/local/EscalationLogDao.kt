package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.EscalationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EscalationLogDao {
    @Query("SELECT * FROM escalation_logs ORDER BY triggeredAtMillis DESC")
    fun getAllEscalationLogs(): Flow<List<EscalationLogEntity>>

    @Query("SELECT * FROM escalation_logs WHERE reminderId = :reminderId LIMIT 1")
    suspend fun getLogForReminder(reminderId: Long): EscalationLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EscalationLogEntity): Long

    @Query("UPDATE escalation_logs SET status = :status, deliveryNotes = :notes WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Long, status: String, notes: String)

    @Query("DELETE FROM escalation_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM escalation_logs")
    suspend fun clearAllLogs()
}
