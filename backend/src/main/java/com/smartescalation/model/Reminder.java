package com.smartescalation.model;

public class Reminder {
    private String id;
    private String medicineId;
    private String medicineName;
    private String dosage;
    private long scheduledTimeMillis;
    private String scheduledTimeString;
    private String scheduledDateString;
    private long deadlineTimeMillis;
    private String status; // SCHEDULED, PENDING, CONFIRMED, CONFIRMED_WITH_PHOTO, SKIPPED, UNCONFIRMED, ALERT_SENT
    private Long confirmationTimeMillis;
    private String photoPath;
    private String skipReason;
    private boolean isEscalated;
    private Long escalatedAtMillis;
    private String escalatedToContactName;

    public Reminder() {}

    public Reminder(String id, String medicineId, String medicineName, String dosage,
                    long scheduledTimeMillis, String scheduledTimeString, String scheduledDateString,
                    long deadlineTimeMillis, String status) {
        this.id = id;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.scheduledTimeMillis = scheduledTimeMillis;
        this.scheduledTimeString = scheduledTimeString;
        this.scheduledDateString = scheduledDateString;
        this.deadlineTimeMillis = deadlineTimeMillis;
        this.status = status;
        this.isEscalated = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getConfirmationTimeMillis() { return confirmationTimeMillis; }
    public void setConfirmationTimeMillis(Long confirmationTimeMillis) { this.confirmationTimeMillis = confirmationTimeMillis; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getSkipReason() { return skipReason; }
    public void setSkipReason(String skipReason) { this.skipReason = skipReason; }

    public boolean isEscalated() { return isEscalated; }
    public void setEscalated(boolean escalated) { isEscalated = escalated; }

    public Long getEscalatedAtMillis() { return escalatedAtMillis; }
    public void setEscalatedAtMillis(Long escalatedAtMillis) { this.escalatedAtMillis = escalatedAtMillis; }

    public String getEscalatedToContactName() { return escalatedToContactName; }
    public void setEscalatedToContactName(String escalatedToContactName) { this.escalatedToContactName = escalatedToContactName; }
}
