package com.smartescalation.dto;

import com.smartescalation.model.EscalationEvent;
import java.util.List;

public class EscalationCheckResponse {
    private int checkedCount;
    private int escalatedCount;
    private List<EscalationEvent> newlyEscalatedEvents;
    private String statusMessage;

    public EscalationCheckResponse() {}

    public EscalationCheckResponse(int checkedCount, int escalatedCount,
                                   List<EscalationEvent> newlyEscalatedEvents, String statusMessage) {
        this.checkedCount = checkedCount;
        this.escalatedCount = escalatedCount;
        this.newlyEscalatedEvents = newlyEscalatedEvents;
        this.statusMessage = statusMessage;
    }

    public int getCheckedCount() { return checkedCount; }
    public void setCheckedCount(int checkedCount) { this.checkedCount = checkedCount; }

    public int getEscalatedCount() { return escalatedCount; }
    public void setEscalatedCount(int escalatedCount) { this.escalatedCount = escalatedCount; }

    public List<EscalationEvent> getNewlyEscalatedEvents() { return newlyEscalatedEvents; }
    public void setNewlyEscalatedEvents(List<EscalationEvent> newlyEscalatedEvents) { this.newlyEscalatedEvents = newlyEscalatedEvents; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
