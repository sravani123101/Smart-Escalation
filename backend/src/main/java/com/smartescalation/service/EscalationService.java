package com.smartescalation.service;

import com.smartescalation.dto.EscalationCheckResponse;
import com.smartescalation.model.EscalationEvent;
import com.smartescalation.model.Reminder;
import com.smartescalation.model.TrustedContact;
import com.smartescalation.repository.EscalationEventRepository;
import com.smartescalation.repository.ReminderRepository;
import com.smartescalation.repository.TrustedContactRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EscalationService {

    private final EscalationEventRepository escalationRepository;
    private final ReminderRepository reminderRepository;
    private final TrustedContactRepository contactRepository;

    public EscalationService(EscalationEventRepository escalationRepository,
                             ReminderRepository reminderRepository,
                             TrustedContactRepository contactRepository) {
        this.escalationRepository = escalationRepository;
        this.reminderRepository = reminderRepository;
        this.contactRepository = contactRepository;
    }

    public List<EscalationEvent> getAllEscalations() {
        return escalationRepository.findAll();
    }

    public EscalationCheckResponse checkAndTriggerEscalations() {
        long now = System.currentTimeMillis();
        List<Reminder> unconfirmed = reminderRepository.findUnconfirmedPastDeadline(now);
        List<TrustedContact> activeContacts = contactRepository.findActiveEscalationContacts();
        TrustedContact primaryContact = activeContacts.isEmpty() ? null : activeContacts.get(0);

        List<EscalationEvent> newlyEscalated = new ArrayList<>();

        for (Reminder rem : unconfirmed) {
            // Prevent duplicate escalation
            if (rem.isEscalated()) continue;

            String contactName = primaryContact != null ? primaryContact.getName() : "Emergency Caregiver";
            String contactPhone = primaryContact != null ? primaryContact.getPhoneNumber() : "N/A";
            String contactId = primaryContact != null ? primaryContact.getId() : "none";

            rem.setStatus("UNCONFIRMED");
            rem.setEscalated(true);
            rem.setEscalatedAtMillis(now);
            rem.setEscalatedToContactName(contactName);
            reminderRepository.save(rem);

            String alertMessage = "SMART ESCALATION ALERT: Scheduled dose of " + rem.getMedicineName() +
                    " (" + rem.getDosage() + ") at " + rem.getScheduledTimeString() +
                    " remained unconfirmed past the safety deadline. Please check on patient safety.";

            EscalationEvent event = new EscalationEvent(
                    null,
                    rem.getId(),
                    rem.getMedicineName(),
                    rem.getDosage(),
                    rem.getScheduledTimeString(),
                    contactId,
                    contactName,
                    contactPhone,
                    alertMessage,
                    now,
                    "ESCALATION_TRIGGERED",
                    "Safety deadline expired without patient confirmation. Alert generated."
            );

            EscalationEvent saved = escalationRepository.save(event);
            newlyEscalated.add(saved);
        }

        String msg = newlyEscalated.isEmpty()
                ? "All scheduled doses are within safe thresholds or already confirmed."
                : newlyEscalated.size() + " overdue dose(s) escalated to trusted contacts.";

        return new EscalationCheckResponse(
                unconfirmed.size(),
                newlyEscalated.size(),
                newlyEscalated,
                msg
        );
    }
}
