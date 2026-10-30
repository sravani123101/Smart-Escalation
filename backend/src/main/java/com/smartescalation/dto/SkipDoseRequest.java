package com.smartescalation.dto;

public class SkipDoseRequest {
    private String reason;

    public SkipDoseRequest() {}

    public SkipDoseRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
