package com.smartescalation.model;

import java.util.List;

public class Medicine {
    private String id;
    private String name;
    private String dosage;
    private String medicineType; // Tablet, Capsule, Syrup, etc.
    private String frequency;    // Daily, Twice Daily, etc.
    private List<String> reminderTimes; // ["08:00", "20:00"]
    private String instructions;
    private String foodRelation; // BEFORE_FOOD, AFTER_FOOD, WITH_FOOD, NO_RESTRICTION
    private int deadlineMinutes;
    private boolean remindersEnabled;
    private int currentStock;
    private int lowStockThreshold;
    private String unit;
    private long createdAt;

    public Medicine() {}

    public Medicine(String id, String name, String dosage, String medicineType, String frequency,
                    List<String> reminderTimes, String instructions, String foodRelation,
                    int deadlineMinutes, boolean remindersEnabled, int currentStock,
                    int lowStockThreshold, String unit, long createdAt) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.medicineType = medicineType;
        this.frequency = frequency;
        this.reminderTimes = reminderTimes;
        this.instructions = instructions;
        this.foodRelation = foodRelation;
        this.deadlineMinutes = deadlineMinutes;
        this.remindersEnabled = remindersEnabled;
        this.currentStock = currentStock;
        this.lowStockThreshold = lowStockThreshold;
        this.unit = unit;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
