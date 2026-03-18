package com.wanderaTech.product_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId; // The Product ID

    private String eventType;   // Useful for the Scheduler to know which Producer to use

    @Column(columnDefinition = "TEXT") // Use TEXT to ensure large JSONs fit
    private String payload;

    private LocalDateTime createdAt;

    private boolean processed = false;
}
