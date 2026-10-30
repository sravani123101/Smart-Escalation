package com.smartescalation.repository;

import com.smartescalation.model.Reminder;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository {
    List<Reminder> findAll();
    Optional<Reminder> findById(String id);
    List<Reminder> findByMedicineId(String medicineId);
    List<Reminder> findByStatus(String status);
    List<Reminder> findUnconfirmedPastDeadline(long nowMillis);
    Reminder save(Reminder reminder);
    void deleteById(String id);
    void deleteByMedicineId(String medicineId);
}
