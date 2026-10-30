package com.smartescalation.model;

public class EscalationEvent {
    private String id;
    private String reminderId;
    private String medicineName;
    private String dosage;
    private String scheduledTime;
    private String contactId;
    private String contactName;
    private String contactPhone;
    private String alertMessage;
    private long triggeredAtMillis;
    private String status; // ESCALATION_TRIGGERED, ALERT_PREPARED, SHARED_VIA_SMS
    private String deliveryNotes;

    public EscalationEvent() {}

    public EscalationEvent(String id, String reminderId, String medicineName, String dosage,
                           String scheduledTime, String contactId, String contactName,
                           String contactPhone, String alertMessage, long triggeredAtMillis,
                           String status, String deliveryNotes) {
        this.id = id;
        this.reminderId = reminderId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.scheduledTime = scheduledTime;
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.alertMessage = alertMessage;
        this.triggeredAtMillis = triggeredAtMillis;
        this.status = status;
        this.deliveryNotes = deliveryNotes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReminderId() { return reminderId; }
    public void setReminderId(String reminderId) { this.reminderId = reminderId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public long getTriggeredAtMillis() { return triggeredAtMillis; }
    public void setTriggeredAtMillis(long triggeredAtMillis) { this.triggeredAtMillis = triggeredAtMillis; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryNotes() { return deliveryNotes; }
    public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }
}
