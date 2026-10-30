package com.smartescalation.repository;

import com.smartescalation.model.Medicine;
import java.util.List;
import java.util.Optional;

public interface MedicineRepository {
    List<Medicine> findAll();
    Optional<Medicine> findById(String id);
    Medicine save(Medicine medicine);
    void deleteById(String id);
    boolean existsById(String id);
}
