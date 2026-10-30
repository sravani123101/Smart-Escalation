package com.smartescalation.dto;

import jakarta.validation.constraints.NotBlank;

public class ReminderRequest {
    @NotBlank(message = "Medicine ID is required")
    private String medicineId;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    private long scheduledTimeMillis;
    private String scheduledTimeString;
    private String scheduledDateString;
    private long deadlineTimeMillis;

    public ReminderRequest() {}

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public long getScheduledTimeMillis() { return scheduledTimeMillis; }
    public void setScheduledTimeMillis(long scheduledTimeMillis) { this.scheduledTimeMillis = scheduledTimeMillis; }

    public String getScheduledTimeString() { return scheduledTimeString; }
    public void setScheduledTimeString(String scheduledTimeString) { this.scheduledTimeString = scheduledTimeString; }

    public String getScheduledDateString() { return scheduledDateString; }
    public void setScheduledDateString(String scheduledDateString) { this.scheduledDateString = scheduledDateString; }

    public long getDeadlineTimeMillis() { return deadlineTimeMillis; }
    public void setDeadlineTimeMillis(long deadlineTimeMillis) { this.deadlineTimeMillis = deadlineTimeMillis; }
}
