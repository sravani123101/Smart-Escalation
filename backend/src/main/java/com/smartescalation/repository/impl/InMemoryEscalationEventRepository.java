package com.smartescalation.repository.impl;

import com.smartescalation.model.EscalationEvent;
import com.smartescalation.repository.EscalationEventRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryEscalationEventRepository implements EscalationEventRepository {

    private final Map<String, EscalationEvent> store = new ConcurrentHashMap<>();

    public InMemoryEscalationEventRepository() {
        EscalationEvent e1 = new EscalationEvent(
                "esc-1", "rem-sample", "Atorvastatin", "20 mg",
                "09:00 PM", "contact-1", "Dr. Robert Vance (Primary Care)",
                "+1 (555) 432-8765",
                "SMART ESCALATION ALERT: Scheduled dose of Atorvastatin (20 mg) remained unconfirmed past the 60-min deadline. Please check on patient safety.",
                System.currentTimeMillis() - (86400 * 1000L),
                "ALERT_PREPARED",
                "Delivered via SMS/Share Intent to primary care contact"
        );
        store.put(e1.getId(), e1);
    }

    @Override
    public List<EscalationEvent> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparingLong(EscalationEvent::getTriggeredAtMillis).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EscalationEvent> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<EscalationEvent> findByReminderId(String reminderId) {
        return store.values().stream()
                .filter(e -> e.getReminderId().equals(reminderId))
                .findFirst();
    }

    @Override
    public EscalationEvent save(EscalationEvent event) {
        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }
        if (event.getTriggeredAtMillis() == 0) {
            event.setTriggeredAtMillis(System.currentTimeMillis());
        }
        store.put(event.getId(), event);
        return event;
    }
}
