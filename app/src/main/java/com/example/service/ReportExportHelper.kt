package com.example.service

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.ReminderLogEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExportHelper {

    fun generateAdherenceCsv(context: Context, logs: List<ReminderLogEntity>): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            val file = File(exportDir, "adherence_report_$timestamp.csv")

            val writer = FileWriter(file)
            writer.append("ID,Date,Medicine,Dosage,Scheduled Time,Status,Confirmation Time,Photo Verified,Escalated,Notes\n")

            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

            for (log in logs) {
                val confTime = log.confirmationTimeMillis?.let { timeFormat.format(Date(it)) } ?: "N/A"
                val hasPhoto = if (!log.photoPath.isNullOrBlank()) "YES" else "NO"
                val isEsc = if (log.isEscalated) "YES (${log.escalatedToContactName ?: "Contact"})" else "NO"
                val notes = (log.notes ?: "").replace(",", ";")

                writer.append("${log.id},${log.scheduledDateString},\"${log.medicineName}\",\"${log.dosage}\",${log.scheduledTimeString},${log.status},$confTime,$hasPhoto,$isEsc,\"$notes\"\n")
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareReportFile(context: Context, file: File) {
        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Medicine Adherence & Escalation Report")
            putExtra(Intent.EXTRA_TEXT, "Attached is the Smart Escalation medicine adherence and safety report.")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Adherence Report"))
    }
}
