package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_contacts")
data class TrustedContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String = "Family", // Family, Caregiver, Doctor, Nurse, Friend, Other
    val escalationPriority: Int = 1, // 1 = Primary, 2 = Secondary, etc.
    val isEscalationEnabled: Boolean = true,
    val consentGranted: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
