package com.smartescalation.service;

import com.smartescalation.dto.ConfirmDoseRequest;
import com.smartescalation.dto.ReminderRequest;
import com.smartescalation.dto.SkipDoseRequest;
import com.smartescalation.exception.ResourceNotFoundException;
import com.smartescalation.model.Medicine;
import com.smartescalation.model.Reminder;
import com.smartescalation.repository.MedicineRepository;
import com.smartescalation.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final MedicineRepository medicineRepository;

    public ReminderService(ReminderRepository reminderRepository, MedicineRepository medicineRepository) {
        this.reminderRepository = reminderRepository;
        this.medicineRepository = medicineRepository;
    }

    public List<Reminder> getAllReminders() {
        return reminderRepository.findAll();
    }

    public Reminder getReminderById(String id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }

    public Reminder createReminder(ReminderRequest req) {
        Reminder r = new Reminder(
                null,
                req.getMedicineId(),
                req.getMedicineName(),
                req.getDosage(),
                req.getScheduledTimeMillis(),
                req.getScheduledTimeString(),
                req.getScheduledDateString(),
                req.getDeadlineTimeMillis(),
                "SCHEDULED"
        );
        return reminderRepository.save(r);
    }

    public Reminder confirmDose(String id, ConfirmDoseRequest req) {
        Reminder r = getReminderById(id);
        long now = System.currentTimeMillis();

        if (req != null && req.isWithPhoto() && req.getPhotoPath() != null) {
            r.setStatus("CONFIRMED_WITH_PHOTO");
            r.setPhotoPath(req.getPhotoPath());
        } else {
            r.setStatus("CONFIRMED");
        }
        r.setConfirmationTimeMillis(now);

        // Decrement medicine stock
        medicineRepository.findById(r.getMedicineId()).ifPresent(med -> {
            med.setCurrentStock(Math.max(0, med.getCurrentStock() - 1));
            medicineRepository.save(med);
        });

        return reminderRepository.save(r);
    }

    public Reminder skipDose(String id, SkipDoseRequest req) {
        Reminder r = getReminderById(id);
        r.setStatus("SKIPPED");
        r.setConfirmationTimeMillis(System.currentTimeMillis());
        r.setSkipReason(req != null && req.getReason() != null ? req.getReason() : "Skipped by patient");
        return reminderRepository.save(r);
    }

    public List<Reminder> getReminderHistory() {
        return reminderRepository.findAll();
    }
}
