package com.smartescalation.repository;

import com.smartescalation.model.TrustedContact;
import java.util.List;
import java.util.Optional;

public interface TrustedContactRepository {
    List<TrustedContact> findAll();
    Optional<TrustedContact> findById(String id);
    List<TrustedContact> findActiveEscalationContacts();
    TrustedContact save(TrustedContact contact);
    void deleteById(String id);
}
