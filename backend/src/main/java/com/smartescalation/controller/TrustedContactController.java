package com.smartescalation.controller;

import com.smartescalation.dto.ContactRequest;
import com.smartescalation.model.TrustedContact;
import com.smartescalation.service.TrustedContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class TrustedContactController {

    private final TrustedContactService contactService;

    public TrustedContactController(TrustedContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<TrustedContact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @PostMapping
    public ResponseEntity<TrustedContact> createContact(@Valid @RequestBody ContactRequest req) {
        TrustedContact created = contactService.createContact(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrustedContact> updateContact(@PathVariable String id, @Valid @RequestBody ContactRequest req) {
        TrustedContact updated = contactService.updateContact(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
