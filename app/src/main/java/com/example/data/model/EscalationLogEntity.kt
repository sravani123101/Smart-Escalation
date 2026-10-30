package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "escalation_logs")
data class EscalationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reminderId: Long,
    val medicineName: String,
    val dosage: String,
    val scheduledTime: String,
    val contactId: Long,
    val contactName: String,
    val contactPhone: String,
    val alertMessage: String,
    val triggeredAtMillis: Long = System.currentTimeMillis(),
    val status: String = "ESCALATION_TRIGGERED", // ESCALATION_TRIGGERED, ALERT_PREPARED, SHARED_VIA_SMS, DISMISSED
    val channel: String = "SMS_INTENT",
    val deliveryNotes: String = "Escalation generated locally. User-approved intent ready for delivery."
)
