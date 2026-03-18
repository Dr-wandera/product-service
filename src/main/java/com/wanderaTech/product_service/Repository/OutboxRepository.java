package com.wanderaTech.product_service.Repository;

import com.wanderaTech.product_service.Model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    // Find events that haven't been sent yet
    List<OutboxEvent> findByProcessedFalse();
}

