package com.smartescalation.controller;

import com.smartescalation.dto.EscalationCheckResponse;
import com.smartescalation.model.EscalationEvent;
import com.smartescalation.service.EscalationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/escalations")
public class EscalationController {

    private final EscalationService escalationService;

    public EscalationController(EscalationService escalationService) {
        this.escalationService = escalationService;
    }

    @GetMapping
    public ResponseEntity<List<EscalationEvent>> getAllEscalations() {
        return ResponseEntity.ok(escalationService.getAllEscalations());
    }

    @PostMapping("/check")
    public ResponseEntity<EscalationCheckResponse> checkEscalations() {
        return ResponseEntity.ok(escalationService.checkAndTriggerEscalations());
    }
}
