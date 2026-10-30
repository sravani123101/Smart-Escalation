package com.smartescalation.service;

import com.smartescalation.dto.MedicineRequest;
import com.smartescalation.exception.ResourceNotFoundException;
import com.smartescalation.model.Medicine;
import com.smartescalation.repository.MedicineRepository;
import com.smartescalation.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final ReminderRepository reminderRepository;

    public MedicineService(MedicineRepository medicineRepository, ReminderRepository reminderRepository) {
        this.medicineRepository = medicineRepository;
        this.reminderRepository = reminderRepository;
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(String id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));
    }

    public Medicine createMedicine(MedicineRequest req) {
        Medicine m = new Medicine();
        m.setName(req.getName());
        m.setDosage(req.getDosage());
        m.setMedicineType(req.getMedicineType());
        m.setFrequency(req.getFrequency());
        m.setReminderTimes(req.getReminderTimes());
        m.setInstructions(req.getInstructions());
        m.setFoodRelation(req.getFoodRelation());
        m.setDeadlineMinutes(req.getDeadlineMinutes());
        m.setRemindersEnabled(req.isRemindersEnabled());
        m.setCurrentStock(req.getCurrentStock());
        m.setLowStockThreshold(req.getLowStockThreshold());
        m.setUnit(req.getUnit());
        return medicineRepository.save(m);
    }

    public Medicine updateMedicine(String id, MedicineRequest req) {
        Medicine m = getMedicineById(id);
        m.setName(req.getName());
        m.setDosage(req.getDosage());
        m.setMedicineType(req.getMedicineType());
        m.setFrequency(req.getFrequency());
        m.setReminderTimes(req.getReminderTimes());
        m.setInstructions(req.getInstructions());
        m.setFoodRelation(req.getFoodRelation());
        m.setDeadlineMinutes(req.getDeadlineMinutes());
        m.setRemindersEnabled(req.isRemindersEnabled());
        m.setCurrentStock(req.getCurrentStock());
        m.setLowStockThreshold(req.getLowStockThreshold());
        m.setUnit(req.getUnit());
        return medicineRepository.save(m);
    }

    public void deleteMedicine(String id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine not found with id: " + id);
        }
        medicineRepository.deleteById(id);
        reminderRepository.deleteByMedicineId(id);
    }
}
