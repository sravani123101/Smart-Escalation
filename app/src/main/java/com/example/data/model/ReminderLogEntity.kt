package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminder_logs",
    indices = [
        Index(value = ["medicineId"]),
        Index(value = ["scheduledDateString"]),
        Index(value = ["status"])
    ]
)
data class ReminderLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,
    val medicineName: String,
    val dosage: String,
    val scheduledTimeMillis: Long,
    val scheduledTimeString: String, // e.g., "08:00 AM"
    val scheduledDateString: String, // e.g., "2026-10-30"
    val deadlineTimeMillis: Long,
    val status: String = STATUS_SCHEDULED, // SCHEDULED, PENDING, CONFIRMED, CONFIRMED_WITH_PHOTO, SKIPPED, UNCONFIRMED, ESCALATION_PENDING, ALERT_SENT, ALERT_FAILED
    val confirmationTimeMillis: Long? = null,
    val photoPath: String? = null, // App-private file storage path
    val skipReason: String? = null,
    val isEscalated: Boolean = false,
    val escalatedAtMillis: Long? = null,
    val escalatedToContactName: String? = null,
    val notes: String? = null
) {
    companion object {
        const val STATUS_SCHEDULED = "SCHEDULED"
        const val STATUS_PENDING = "PENDING"
        const val STATUS_CONFIRMED = "CONFIRMED"
        const val STATUS_CONFIRMED_WITH_PHOTO = "CONFIRMED_WITH_PHOTO"
        const val STATUS_SKIPPED = "SKIPPED"
        const val STATUS_UNCONFIRMED = "UNCONFIRMED"
        const val STATUS_ESCALATION_PENDING = "ESCALATION_PENDING"
        const val STATUS_ALERT_SENT = "ALERT_SENT"
        const val STATUS_ALERT_FAILED = "ALERT_FAILED"
    }

    val isConfirmed: Boolean
        get() = status == STATUS_CONFIRMED || status == STATUS_CONFIRMED_WITH_PHOTO

    val isSkipped: Boolean
        get() = status == STATUS_SKIPPED

    val isMissedOrUnconfirmed: Boolean
        get() = status == STATUS_UNCONFIRMED || status == STATUS_ALERT_SENT || status == STATUS_ALERT_FAILED
}
