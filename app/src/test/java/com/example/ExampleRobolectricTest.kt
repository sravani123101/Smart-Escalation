package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.TrustedContactEntity
import com.example.data.repository.EscalationRepository
import com.example.data.repository.MedicineRepository
import com.example.data.repository.ReminderRepository
import com.example.data.repository.TrustedContactRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var medicineRepo: MedicineRepository
    private lateinit var reminderRepo: ReminderRepository
    private lateinit var contactRepo: TrustedContactRepository
    private lateinit var escalationRepo: EscalationRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = AppDatabase.getInstance(context)
        medicineRepo = MedicineRepository(database.medicineDao())
        reminderRepo = ReminderRepository(database.reminderLogDao())
        contactRepo = TrustedContactRepository(database.trustedContactDao())
        escalationRepo = EscalationRepository(database.escalationLogDao())
    }

    @Test
    fun `app name matches Smart Escalation`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Smart Escalation", appName)
    }

    @Test
    fun `test add and retrieve medicine in Room database`() = runBlocking {
        val medicine = MedicineEntity(
            name = "Amoxicillin",
            dosage = "250 mg",
            medicineType = "Capsule",
            frequency = "Three times a day",
            instructions = "Take every 8 hours with water",
            currentStock = 21,
            lowStockThreshold = 6
        )

        val id = medicineRepo.insertMedicine(medicine)
        assertTrue(id > 0)

        val retrieved = medicineRepo.getMedicineById(id)
        assertNotNull(retrieved)
        assertEquals("Amoxicillin", retrieved?.name)
        assertEquals(21, retrieved?.currentStock)
    }

    @Test
    fun `test confirm dose without photo updates status and stock`() = runBlocking {
        val medicineId = medicineRepo.insertMedicine(
            MedicineEntity(
                name = "Lisinopril",
                dosage = "10 mg",
                currentStock = 15,
                lowStockThreshold = 5
            )
        )

        val scheduledTime = System.currentTimeMillis()
        val reminderId = reminderRepo.insertReminder(
            ReminderLogEntity(
                medicineId = medicineId,
                medicineName = "Lisinopril",
                dosage = "10 mg",
                scheduledTimeMillis = scheduledTime,
                scheduledTimeString = "09:00 AM",
                scheduledDateString = "2026-10-30",
                deadlineTimeMillis = scheduledTime + (45 * 60 * 1000L),
                status = ReminderLogEntity.STATUS_PENDING
            )
        )

        // Confirm dose without photo
        reminderRepo.confirmDose(reminderId, withPhoto = false, photoPath = null)
        medicineRepo.decrementStock(medicineId)

        val updatedReminder = reminderRepo.getReminderById(reminderId)
        assertNotNull(updatedReminder)
        assertEquals(ReminderLogEntity.STATUS_CONFIRMED, updatedReminder?.status)
        assertTrue(updatedReminder?.isConfirmed == true)

        val updatedMedicine = medicineRepo.getMedicineById(medicineId)
        assertEquals(14, updatedMedicine?.currentStock)
    }

    @Test
    fun `test confirm dose with photo records photo path`() = runBlocking {
        val scheduledTime = System.currentTimeMillis()
        val reminderId = reminderRepo.insertReminder(
            ReminderLogEntity(
                medicineId = 1L,
                medicineName = "Metformin",
                dosage = "500 mg",
                scheduledTimeMillis = scheduledTime,
                scheduledTimeString = "08:00 AM",
                scheduledDateString = "2026-10-30",
                deadlineTimeMillis = scheduledTime + (45 * 60 * 1000L),
                status = ReminderLogEntity.STATUS_PENDING
            )
        )

        val mockPhotoPath = "/data/user/0/com.example/files/dose_photos/dose_1.jpg"
        reminderRepo.confirmDose(reminderId, withPhoto = true, photoPath = mockPhotoPath)

        val updatedReminder = reminderRepo.getReminderById(reminderId)
        assertNotNull(updatedReminder)
        assertEquals(ReminderLogEntity.STATUS_CONFIRMED_WITH_PHOTO, updatedReminder?.status)
        assertEquals(mockPhotoPath, updatedReminder?.photoPath)
    }

    @Test
    fun `test escalation creation when deadline expires unconfirmed`() = runBlocking {
        val now = System.currentTimeMillis()
        val pastTime = now - (60 * 60 * 1000L) // 1 hour ago
        val pastDeadline = pastTime + (30 * 60 * 1000L) // Deadline was 30 mins ago

        val reminderId = reminderRepo.insertReminder(
            ReminderLogEntity(
                medicineId = 2L,
                medicineName = "Atorvastatin",
                dosage = "20 mg",
                scheduledTimeMillis = pastTime,
                scheduledTimeString = "07:00 PM",
                scheduledDateString = "2026-10-30",
                deadlineTimeMillis = pastDeadline,
                status = ReminderLogEntity.STATUS_PENDING
            )
        )

        val contact = TrustedContactEntity(
            name = "Jane Doe",
            phoneNumber = "+1 (555) 777-8888",
            relationship = "Caregiver",
            escalationPriority = 1,
            isEscalationEnabled = true
        )
        val contactId = contactRepo.insertContact(contact)

        // Find overdue reminders
        val overdue = reminderRepo.getUnconfirmedPastDeadline(now)
        val targetOverdue = overdue.find { it.id == reminderId }
        assertNotNull("Overdue reminder should be detected by deadline check", targetOverdue)

        // Mark escalated
        reminderRepo.markEscalated(reminderId, contact.name)
        val escalationEvent = escalationRepo.createEscalation(targetOverdue!!, contact.copy(id = contactId), "Alex")

        assertEquals("Atorvastatin", escalationEvent.medicineName)
        assertEquals("Jane Doe", escalationEvent.contactName)
        assertTrue(escalationEvent.alertMessage.contains("Atorvastatin"))
        assertTrue(escalationEvent.alertMessage.contains("safety deadline"))

        val refreshed = reminderRepo.getReminderById(reminderId)
        assertTrue(refreshed?.isEscalated == true)
        assertEquals(ReminderLogEntity.STATUS_UNCONFIRMED, refreshed?.status)
    }
}
