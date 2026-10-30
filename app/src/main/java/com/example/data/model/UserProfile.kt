package com.example.data.model

data class UserProfile(
    val name: String = "Alex Chen",
    val age: Int = 42,
    val gender: String = "Male",
    val bloodGroup: String = "O+",
    val emergencyNotes: String = "Penicillin allergy. Diabetic care regimen.",
    val doctorName: String = "Dr. Sarah Mitchell",
    val doctorPhone: String = "+1 (555) 234-5678",
    val isVoiceReminderEnabled: Boolean = true,
    val isDarkTheme: Boolean = false,
    val backendApiUrl: String = "http://10.0.2.2:8080/api/",
    val hasAcceptedPrivacyConsent: Boolean = true
)
