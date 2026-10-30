package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val medicineType: String = "Tablet", // Tablet, Capsule, Syrup, Injection, Drops, Inhaler, Topical
    val frequency: String = "Daily", // Daily, Twice Daily, Three Times, Weekly, As Needed
    val reminderTimesJson: String = "[\"08:00\"]", // JSON array of "HH:mm" strings
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val instructions: String = "",
    val foodRelation: String = "AFTER_FOOD", // BEFORE_FOOD, AFTER_FOOD, WITH_FOOD, NO_RESTRICTION
    val deadlineMinutes: Int = 45, // Time window to confirm before escalation
    val remindersEnabled: Boolean = true,
    val currentStock: Int = 30,
    val lowStockThreshold: Int = 5,
    val unit: String = "tablets",
    val colorHex: String = "#0F2C59",
    val createdAt: Long = System.currentTimeMillis()
)
