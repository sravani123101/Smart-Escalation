package com.smartescalation.repository;

import com.smartescalation.model.EscalationEvent;
import java.util.List;
import java.util.Optional;

public interface EscalationEventRepository {
    List<EscalationEvent> findAll();
    Optional<EscalationEvent> findById(String id);
    Optional<EscalationEvent> findByReminderId(String reminderId);
    EscalationEvent save(EscalationEvent event);
}
