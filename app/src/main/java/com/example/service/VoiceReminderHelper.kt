package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceReminderHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true
            }
        }
    }

    fun speakMedicineReminder(medicineName: String, dosage: String, instructions: String = "") {
        if (!isInitialized) return
        val text = buildString {
            append("Reminder for your medicine. Please take $dosage of $medicineName.")
            if (instructions.isNotBlank()) {
                append(" Instructions: $instructions.")
            }
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MED_REMINDER_${System.currentTimeMillis()}")
    }

    fun speakAnnouncement(text: String) {
        if (!isInitialized) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ANNOUNCE_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
