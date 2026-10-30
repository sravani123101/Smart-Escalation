package com.smartescalation.model;

public class TrustedContact {
    private String id;
    private String name;
    private String phoneNumber;
    private String relationship;
    private int escalationPriority;
    private boolean isEscalationEnabled;
    private boolean consentGranted;
    private String notes;
    private long createdAt;

    public TrustedContact() {}

    public TrustedContact(String id, String name, String phoneNumber, String relationship,
                          int escalationPriority, boolean isEscalationEnabled,
                          boolean consentGranted, String notes, long createdAt) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
        this.escalationPriority = escalationPriority;
        this.isEscalationEnabled = isEscalationEnabled;
        this.consentGranted = consentGranted;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public int getEscalationPriority() { return escalationPriority; }
    public void setEscalationPriority(int escalationPriority) { this.escalationPriority = escalationPriority; }

    public boolean isEscalationEnabled() { return isEscalationEnabled; }
    public void setEscalationEnabled(boolean escalationEnabled) { isEscalationEnabled = escalationEnabled; }

    public boolean isConsentGranted() { return consentGranted; }
    public void setConsentGranted(boolean consentGranted) { this.consentGranted = consentGranted; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
