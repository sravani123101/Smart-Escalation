package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.SmartEscalationApp
import com.example.data.local.AppDatabase
import com.example.data.model.EscalationLogEntity
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.TrustedContactEntity
import com.example.data.model.UserProfile
import com.example.data.repository.EscalationRepository
import com.example.data.repository.MedicineRepository
import com.example.data.repository.ReminderRepository
import com.example.data.repository.TrustedContactRepository
import com.example.service.NotificationHelper
import com.example.service.PhotoStorageHelper
import com.example.service.ReminderAlarmScheduler
import com.example.service.ReportExportHelper
import com.example.service.VoiceReminderHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as SmartEscalationApp).database
    val medicineRepo = MedicineRepository(db.medicineDao())
    val reminderRepo = ReminderRepository(db.reminderLogDao())
    val contactRepo = TrustedContactRepository(db.trustedContactDao())
    val escalationRepo = EscalationRepository(db.escalationLogDao())

    private val voiceHelper = VoiceReminderHelper(application)

    // User profile state
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Today's date string
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val todayDateString: String = dateFormat.format(Date())

    // All medicines Flow
    val allMedicines: StateFlow<List<MedicineEntity>> = medicineRepo.getAllMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Low stock medicines Flow
    val lowStockMedicines: StateFlow<List<MedicineEntity>> = medicineRepo.getLowStockMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's reminders Flow
    val todayReminders: StateFlow<List<ReminderLogEntity>> = reminderRepo.getRemindersForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All reminder logs for History
    val allReminders: StateFlow<List<ReminderLogEntity>> = reminderRepo.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trusted contacts Flow
    val trustedContacts: StateFlow<List<TrustedContactEntity>> = contactRepo.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Escalation logs Flow
    val escalationLogs: StateFlow<List<EscalationLogEntity>> = escalationRepo.getAllEscalationLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected reminder for active action / photo confirmation
    private val _activeReminder = MutableStateFlow<ReminderLogEntity?>(null)
    val activeReminder: StateFlow<ReminderLogEntity?> = _activeReminder.asStateFlow()

    // Sync state
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Navigation selected screen state if needed
    private val _currentScreen = MutableStateFlow("dashboard")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    init {
        // Automatically ensure today's schedule is generated from medicines
        viewModelScope.launch {
            allMedicines.collect { meds ->
                if (meds.isNotEmpty()) {
                    reminderRepo.generateDailySchedulesIfMissing(meds)
                }
            }
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun setActiveReminder(reminder: ReminderLogEntity?) {
        _activeReminder.value = reminder
    }

    fun updateProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    fun confirmDoseWithoutPhoto(reminderId: Long) {
        viewModelScope.launch {
            val reminder = reminderRepo.getReminderById(reminderId) ?: return@launch
            reminderRepo.confirmDose(reminderId, withPhoto = false, photoPath = null)
            medicineRepo.decrementStock(reminder.medicineId)
            NotificationHelper.dismissNotification(getApplication(), reminderId.toInt())
            _activeReminder.value = null

            // Check if stock is low
            checkStockAlert(reminder.medicineId)
        }
    }

    fun confirmDoseWithPhoto(reminderId: Long, sourceUri: Uri?, bitmap: Bitmap?) {
        viewModelScope.launch {
            val reminder = reminderRepo.getReminderById(reminderId) ?: return@launch
            val context: Context = getApplication()

            val savedPath = when {
                sourceUri != null -> PhotoStorageHelper.savePhotoPrivately(context, sourceUri, reminderId)
                bitmap != null -> PhotoStorageHelper.saveBitmapPrivately(context, bitmap, reminderId)
                else -> null
            }

            reminderRepo.confirmDose(reminderId, withPhoto = true, photoPath = savedPath)
            medicineRepo.decrementStock(reminder.medicineId)
            NotificationHelper.dismissNotification(context, reminderId.toInt())
            _activeReminder.value = null

            checkStockAlert(reminder.medicineId)
        }
    }

    fun skipDose(reminderId: Long, reason: String) {
        viewModelScope.launch {
            reminderRepo.skipDose(reminderId, reason)
            NotificationHelper.dismissNotification(getApplication(), reminderId.toInt())
            _activeReminder.value = null
        }
    }

    fun snoozeDose(reminderId: Long) {
        ReminderAlarmScheduler.scheduleSnoozeAlarm(getApplication(), reminderId, 10)
        NotificationHelper.dismissNotification(getApplication(), reminderId.toInt())
        _activeReminder.value = null
        Toast.makeText(getApplication(), "Reminder snoozed for 10 minutes", Toast.LENGTH_SHORT).show()
    }

    fun addMedicine(medicine: MedicineEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val id = medicineRepo.insertMedicine(medicine)
            val updated = medicine.copy(id = id)
            // Generate reminders for today
            reminderRepo.generateDailySchedulesIfMissing(listOf(updated))
            // Schedule alarms for newly generated reminders
            val todayLogs = reminderRepo.getRemindersForDateDirect(todayDateString)
            for (log in todayLogs.filter { it.medicineId == id }) {
                ReminderAlarmScheduler.scheduleReminderAlarm(getApplication(), log)
            }
            onComplete()
        }
    }

    fun updateMedicine(medicine: MedicineEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            medicineRepo.updateMedicine(medicine)
            onComplete()
        }
    }

    fun deleteMedicine(medicineId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val reminderLogs = reminderRepo.getRemindersForDateDirect(todayDateString).filter { it.medicineId == medicineId }
            for (r in reminderLogs) {
                ReminderAlarmScheduler.cancelReminderAlarm(getApplication(), r.id)
            }
            medicineRepo.deleteMedicineById(medicineId)
            reminderRepo.deleteRemindersForMedicine(medicineId)
            onComplete()
        }
    }

    fun addTrustedContact(contact: TrustedContactEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            contactRepo.insertContact(contact)
            onComplete()
        }
    }

    fun updateTrustedContact(contact: TrustedContactEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            contactRepo.updateContact(contact)
            onComplete()
        }
    }

    fun deleteTrustedContact(contactId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            contactRepo.deleteContactById(contactId)
            onComplete()
        }
    }

    fun toggleContactEscalation(contactId: Long, enabled: Boolean) {
        viewModelScope.launch {
            contactRepo.setEscalationEnabled(contactId, enabled)
        }
    }

    fun refillMedicineStock(medicineId: Long, addCount: Int) {
        viewModelScope.launch {
            val med = medicineRepo.getMedicineById(medicineId) ?: return@launch
            val updatedStock = med.currentStock + addCount
            medicineRepo.updateStock(medicineId, updatedStock)
            Toast.makeText(getApplication(), "Refilled ${med.name}: $updatedStock ${med.unit} total", Toast.LENGTH_SHORT).show()
        }
    }

    fun triggerManualEscalationCheck() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val unconfirmed = reminderRepo.getUnconfirmedPastDeadline(now)
            val contacts = contactRepo.getActiveEscalationContactsDirect()
            val primaryContact = contacts.firstOrNull()

            var count = 0
            for (rem in unconfirmed) {
                val contactName = primaryContact?.name ?: "Emergency Caregiver"
                reminderRepo.markEscalated(rem.id, contactName)
                if (primaryContact != null) {
                    escalationRepo.createEscalation(rem, primaryContact, _userProfile.value.name)
                }
                NotificationHelper.showEscalationAlertNotification(
                    getApplication(),
                    rem,
                    contactName,
                    primaryContact?.phoneNumber ?: ""
                )
                count++
            }

            val msg = if (count > 0) {
                "Escalation check completed: $count overdue reminder(s) escalated."
            } else {
                "Escalation check completed: All active doses are within their safety window."
            }
            Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show()
        }
    }

    fun sendEscalationSmsIntent(context: Context, escalationLog: EscalationLogEntity) {
        viewModelScope.launch {
            escalationRepo.markDeliveredViaSms(escalationLog.id, "Sent via SMS Intent to ${escalationLog.contactPhone}")
        }

        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${escalationLog.contactPhone}")
            putExtra("sms_body", escalationLog.alertMessage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            // Fallback to general share intent if no default SMS app
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, escalationLog.alertMessage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Escalation Alert"))
        }
    }

    fun testReminderNotification(context: Context) {
        val testReminder = ReminderLogEntity(
            id = 9999L,
            medicineId = 1L,
            medicineName = "Metformin (Test)",
            dosage = "500 mg",
            scheduledTimeMillis = System.currentTimeMillis(),
            scheduledTimeString = "Now",
            scheduledDateString = todayDateString,
            deadlineTimeMillis = System.currentTimeMillis() + (30 * 60 * 1000L),
            status = ReminderLogEntity.STATUS_PENDING
        )
        NotificationHelper.showMedicineReminderNotification(
            context,
            testReminder,
            "Take with food and a glass of water"
        )
        if (_userProfile.value.isVoiceReminderEnabled) {
            voiceHelper.speakMedicineReminder("Metformin", "500 milligrams", "Take with food")
        }
        Toast.makeText(context, "Test notification & voice alert triggered!", Toast.LENGTH_SHORT).show()
    }

    fun speakReminder(medicineName: String, dosage: String, instructions: String) {
        if (_userProfile.value.isVoiceReminderEnabled) {
            voiceHelper.speakMedicineReminder(medicineName, dosage, instructions)
        }
    }

    fun exportAdherenceReport(context: Context) {
        viewModelScope.launch {
            val allLogs = allReminders.value
            if (allLogs.isEmpty()) {
                Toast.makeText(context, "No reminder logs to export yet.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val file = ReportExportHelper.generateAdherenceCsv(context, allLogs)
            if (file != null) {
                ReportExportHelper.shareReportFile(context, file)
            } else {
                Toast.makeText(context, "Failed to generate CSV export.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearAllData(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            reminderRepo.insertReminders(emptyList()) // clear
            PhotoStorageHelper.clearAllPrivatePhotos(context)
            // Re-seed default demo
            AppDatabase.getInstance(context).clearAllTables()
            Toast.makeText(context, "Local data reset to default demo.", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    private suspend fun checkStockAlert(medicineId: Long) {
        val med = medicineRepo.getMedicineById(medicineId) ?: return
        if (med.currentStock <= med.lowStockThreshold) {
            NotificationHelper.showLowStockNotification(
                getApplication(),
                med.name,
                med.currentStock,
                med.unit
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceHelper.shutdown()
    }
}
