package com.smartescalation.dto;

import jakarta.validation.constraints.NotBlank;

public class ContactRequest {
    @NotBlank(message = "Contact name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String relationship = "Family";
    private int escalationPriority = 1;
    private boolean escalationEnabled = true;
    private boolean consentGranted = true;
    private String notes = "";

    public ContactRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public int getEscalationPriority() { return escalationPriority; }
    public void setEscalationPriority(int escalationPriority) { this.escalationPriority = escalationPriority; }

    public boolean isEscalationEnabled() { return escalationEnabled; }
    public void setEscalationEnabled(boolean escalationEnabled) { this.escalationEnabled = escalationEnabled; }

    public boolean isConsentGranted() { return consentGranted; }
    public void setConsentGranted(boolean consentGranted) { this.consentGranted = consentGranted; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
