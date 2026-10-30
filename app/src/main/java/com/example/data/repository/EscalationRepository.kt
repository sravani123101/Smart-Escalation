package com.example.data.repository

import com.example.data.local.EscalationLogDao
import com.example.data.model.EscalationLogEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.TrustedContactEntity
import kotlinx.coroutines.flow.Flow

interface IEscalationRepository {
    fun getAllEscalationLogs(): Flow<List<EscalationLogEntity>>
    suspend fun getLogForReminder(reminderId: Long): EscalationLogEntity?
    suspend fun createEscalation(
        reminder: ReminderLogEntity,
        contact: TrustedContactEntity,
        patientName: String = "Patient"
    ): EscalationLogEntity
    suspend fun markDeliveredViaSms(logId: Long, notes: String = "Delivered via SMS/Share Intent")
    suspend fun deleteLog(id: Long)
    suspend fun clearAll()
}

class EscalationRepository(
    private val dao: EscalationLogDao
) : IEscalationRepository {

    override fun getAllEscalationLogs(): Flow<List<EscalationLogEntity>> = dao.getAllEscalationLogs()

    override suspend fun getLogForReminder(reminderId: Long): EscalationLogEntity? =
        dao.getLogForReminder(reminderId)

    override suspend fun createEscalation(
        reminder: ReminderLogEntity,
        contact: TrustedContactEntity,
        patientName: String
    ): EscalationLogEntity {
        val alertMessage = "SMART ESCALATION ALERT: $patientName scheduled dose of ${reminder.medicineName} (${reminder.dosage}) at ${reminder.scheduledTimeString} was not confirmed before its safety deadline. Please check in with them."

        val log = EscalationLogEntity(
            reminderId = reminder.id,
            medicineName = reminder.medicineName,
            dosage = reminder.dosage,
            scheduledTime = reminder.scheduledTimeString,
            contactId = contact.id,
            contactName = contact.name,
            contactPhone = contact.phoneNumber,
            alertMessage = alertMessage,
            triggeredAtMillis = System.currentTimeMillis(),
            status = "ESCALATION_TRIGGERED",
            channel = "SMS_INTENT",
            deliveryNotes = "Escalation generated locally. User-approved intent ready for delivery."
        )

        val id = dao.insertLog(log)
        return log.copy(id = id)
    }

    override suspend fun markDeliveredViaSms(logId: Long, notes: String) {
        dao.updateDeliveryStatus(logId, "SHARED_VIA_SMS", notes)
    }

    override suspend fun deleteLog(id: Long) = dao.deleteLogById(id)
    override suspend fun clearAll() = dao.clearAllLogs()
}
