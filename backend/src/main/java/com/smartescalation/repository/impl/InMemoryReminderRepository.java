package com.smartescalation.repository.impl;

import com.smartescalation.model.Reminder;
import com.smartescalation.repository.ReminderRepository;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryReminderRepository implements ReminderRepository {

    private final Map<String, Reminder> store = new ConcurrentHashMap<>();

    public InMemoryReminderRepository() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date.from(java.time.Instant.now()));
        long now = System.currentTimeMillis();

        Reminder r1 = new Reminder(
                "rem-1", "med-1", "Metformin", "500 mg",
                now - (3600 * 1000L), "08:00 AM", today,
                now - (3600 * 1000L) + (45 * 60 * 1000L), "CONFIRMED"
        );
        r1.setConfirmationTimeMillis(now - (3000 * 1000L));

        Reminder r2 = new Reminder(
                "rem-2", "med-1", "Metformin", "500 mg",
                now + (3600 * 1000L), "08:00 PM", today,
                now + (3600 * 1000L) + (45 * 60 * 1000L), "PENDING"
        );

        store.put(r1.getId(), r1);
        store.put(r2.getId(), r2);
    }

    @Override
    public List<Reminder> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Reminder> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Reminder> findByMedicineId(String medicineId) {
        return store.values().stream()
                .filter(r -> r.getMedicineId().equals(medicineId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reminder> findByStatus(String status) {
        return store.values().stream()
                .filter(r -> r.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reminder> findUnconfirmedPastDeadline(long nowMillis) {
        return store.values().stream()
                .filter(r -> ("SCHEDULED".equals(r.getStatus()) || "PENDING".equals(r.getStatus()))
                        && r.getDeadlineTimeMillis() <= nowMillis
                        && !r.isEscalated())
                .collect(Collectors.toList());
    }

    @Override
    public Reminder save(Reminder reminder) {
        if (reminder.getId() == null || reminder.getId().isBlank()) {
            reminder.setId(UUID.randomUUID().toString());
        }
        store.put(reminder.getId(), reminder);
        return reminder;
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public void deleteByMedicineId(String medicineId) {
        store.values().removeIf(r -> r.getMedicineId().equals(medicineId));
    }
}
