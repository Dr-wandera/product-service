package com.wanderaTech.product_service.RetryScheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderaTech.common_events.productEvent.ProductCreatedEvent;
import com.wanderaTech.product_service.KafkaConfig.StockProducer;
import com.wanderaTech.product_service.Model.OutboxEvent;
import com.wanderaTech.product_service.Repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRetryScheduler {

    private final OutboxRepository outboxRepository;
    private final StockProducer stockProducer;
    private final ObjectMapper objectMapper;

    //this method is for retries  of the failed event
    @Scheduled(fixedRate = 10000) // Retry every 10 seconds
    @Transactional
    public void retryFailedEvents() {
        List<OutboxEvent> failedEvents = outboxRepository.findByProcessedFalse();

        if (failedEvents.isEmpty()) return;

        log.info("Found {} failed events in outbox. Retrying...", failedEvents.size());

        for (OutboxEvent record : failedEvents) {
            try {
                // Deserialize JSON back to Object
                ProductCreatedEvent event = objectMapper.readValue(
                        record.getPayload(),
                        ProductCreatedEvent.class
                );

                // Try sending to Kafka again
                stockProducer.sendInitialStock(event);

                // If successful, deletes the record to keep the DB clean
                outboxRepository.delete(record);
                log.info("Successfully processed outbox event for product: {}", record.getAggregateId());

            } catch (Exception e) {
                log.error("Retry failed for event {}. Will try again next cycle.", record.getId());
            }
        }
    }
}

