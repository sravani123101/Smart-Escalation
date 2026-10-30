package com.smartescalation.service;

import com.smartescalation.dto.ContactRequest;
import com.smartescalation.exception.ResourceNotFoundException;
import com.smartescalation.model.TrustedContact;
import com.smartescalation.repository.TrustedContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustedContactService {

    private final TrustedContactRepository repository;

    public TrustedContactService(TrustedContactRepository repository) {
        this.repository = repository;
    }

    public List<TrustedContact> getAllContacts() {
        return repository.findAll();
    }

    public TrustedContact getContactById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));
    }

    public TrustedContact createContact(ContactRequest req) {
        TrustedContact c = new TrustedContact();
        c.setName(req.getName());
        c.setPhoneNumber(req.getPhoneNumber());
        c.setRelationship(req.getRelationship());
        c.setEscalationPriority(req.getEscalationPriority());
        c.setEscalationEnabled(req.isEscalationEnabled());
        c.setConsentGranted(req.isConsentGranted());
        c.setNotes(req.getNotes());
        return repository.save(c);
    }

    public TrustedContact updateContact(String id, ContactRequest req) {
        TrustedContact c = getContactById(id);
        c.setName(req.getName());
        c.setPhoneNumber(req.getPhoneNumber());
        c.setRelationship(req.getRelationship());
        c.setEscalationPriority(req.getEscalationPriority());
        c.setEscalationEnabled(req.isEscalationEnabled());
        c.setConsentGranted(req.isConsentGranted());
        c.setNotes(req.getNotes());
        return repository.save(c);
    }

    public void deleteContact(String id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Contact not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
