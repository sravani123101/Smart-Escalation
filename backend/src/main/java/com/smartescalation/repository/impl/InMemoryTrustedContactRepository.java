package com.smartescalation.repository.impl;

import com.smartescalation.model.TrustedContact;
import com.smartescalation.repository.TrustedContactRepository;
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
public class InMemoryTrustedContactRepository implements TrustedContactRepository {

    private final Map<String, TrustedContact> store = new ConcurrentHashMap<>();

    public InMemoryTrustedContactRepository() {
        TrustedContact c1 = new TrustedContact(
                "contact-1", "Dr. Robert Vance (Primary Care)", "+1 (555) 432-8765",
                "Doctor", 1, true, true, "Primary clinic", System.currentTimeMillis()
        );
        TrustedContact c2 = new TrustedContact(
                "contact-2", "Elena Vance (Daughter)", "+1 (555) 987-6543",
                "Family", 2, true, true, "Caregiver", System.currentTimeMillis()
        );
        store.put(c1.getId(), c1);
        store.put(c2.getId(), c2);
    }

    @Override
    public List<TrustedContact> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparingInt(TrustedContact::getEscalationPriority))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TrustedContact> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<TrustedContact> findActiveEscalationContacts() {
        return store.values().stream()
                .filter(TrustedContact::isEscalationEnabled)
                .sorted(Comparator.comparingInt(TrustedContact::getEscalationPriority))
                .collect(Collectors.toList());
    }

    @Override
    public TrustedContact save(TrustedContact contact) {
        if (contact.getId() == null || contact.getId().isBlank()) {
            contact.setId(UUID.randomUUID().toString());
        }
        if (contact.getCreatedAt() == 0) {
            contact.setCreatedAt(System.currentTimeMillis());
        }
        store.put(contact.getId(), contact);
        return contact;
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
