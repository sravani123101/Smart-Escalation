package com.smartescalation.repository.impl;

import com.smartescalation.model.Medicine;
import com.smartescalation.repository.MedicineRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMedicineRepository implements MedicineRepository {

    private final Map<String, Medicine> store = new ConcurrentHashMap<>();

    public InMemoryMedicineRepository() {
        // Seed default demo data
        Medicine m1 = new Medicine(
                "med-1", "Metformin", "500 mg", "Tablet", "Twice Daily",
                Arrays.asList("08:00", "20:00"), "Take with food", "WITH_FOOD",
                45, true, 28, 8, "tablets", System.currentTimeMillis()
        );
        Medicine m2 = new Medicine(
                "med-2", "Atorvastatin", "20 mg", "Tablet", "Daily",
                List.of("21:00"), "Take at bedtime", "AFTER_FOOD",
                60, true, 14, 7, "tablets", System.currentTimeMillis()
        );
        store.put(m1.getId(), m1);
        store.put(m2.getId(), m2);
    }

    @Override
    public List<Medicine> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Medicine> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Medicine save(Medicine medicine) {
        if (medicine.getId() == null || medicine.getId().isBlank()) {
            medicine.setId(UUID.randomUUID().toString());
        }
        if (medicine.getCreatedAt() == 0) {
            medicine.setCreatedAt(System.currentTimeMillis());
        }
        store.put(medicine.getId(), medicine);
        return medicine;
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
