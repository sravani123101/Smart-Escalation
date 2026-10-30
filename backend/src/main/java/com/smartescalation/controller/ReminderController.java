package com.smartescalation.controller;

import com.smartescalation.dto.ConfirmDoseRequest;
import com.smartescalation.dto.ReminderRequest;
import com.smartescalation.dto.SkipDoseRequest;
import com.smartescalation.model.Reminder;
import com.smartescalation.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    public ResponseEntity<List<Reminder>> getAllReminders() {
        return ResponseEntity.ok(reminderService.getAllReminders());
    }

    @PostMapping
    public ResponseEntity<Reminder> createReminder(@Valid @RequestBody ReminderRequest req) {
        Reminder created = reminderService.createReminder(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Reminder> confirmDose(
            @PathVariable String id,
            @RequestBody(required = false) ConfirmDoseRequest req) {
        Reminder confirmed = reminderService.confirmDose(id, req);
        return ResponseEntity.ok(confirmed);
    }

    @PostMapping("/{id}/skip")
    public ResponseEntity<Reminder> skipDose(
            @PathVariable String id,
            @RequestBody(required = false) SkipDoseRequest req) {
        Reminder skipped = reminderService.skipDose(id, req);
        return ResponseEntity.ok(skipped);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Reminder>> getReminderHistory() {
        return ResponseEntity.ok(reminderService.getReminderHistory());
    }
}
