package com.smartescalation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class MedicineRequest {
    @NotBlank(message = "Medicine name is required")
    private String name;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    private String medicineType = "Tablet";
    private String frequency = "Daily";

    @NotEmpty(message = "At least one reminder time is required")
    private List<String> reminderTimes;

    private String instructions = "";
    private String foodRelation = "AFTER_FOOD";
    private int deadlineMinutes = 45;
    private boolean remindersEnabled = true;
    private int currentStock = 30;
    private int lowStockThreshold = 5;
    private String unit = "tablets";

    public MedicineRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getMedicineType() { return medicineType; }
    public void setMedicineType(String medicineType) { this.medicineType = medicineType; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<String> getReminderTimes() { return reminderTimes; }
    public void setReminderTimes(List<String> reminderTimes) { this.reminderTimes = reminderTimes; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getFoodRelation() { return foodRelation; }
    public void setFoodRelation(String foodRelation) { this.foodRelation = foodRelation; }

    public int getDeadlineMinutes() { return deadlineMinutes; }
    public void setDeadlineMinutes(int deadlineMinutes) { this.deadlineMinutes = deadlineMinutes; }

    public boolean isRemindersEnabled() { return remindersEnabled; }
    public void setRemindersEnabled(boolean remindersEnabled) { this.remindersEnabled = remindersEnabled; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
