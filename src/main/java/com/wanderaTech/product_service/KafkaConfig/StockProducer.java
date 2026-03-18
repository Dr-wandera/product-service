package com.wanderaTech.product_service.KafkaConfig;

import com.wanderaTech.common_events.productEvent.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockProducer {
    //this publishes kafka event  to  initialize stock of the product created
    private final KafkaTemplate<String,ProductCreatedEvent> kafkaTemplate;

    public void  sendInitialStock(ProductCreatedEvent productCreatedEvent){
        log.info("Start sending initial stock event to the inventory");

        Message<ProductCreatedEvent> message= MessageBuilder
                .withPayload(productCreatedEvent)
                .setHeader(KafkaHeaders.TOPIC,"initialStock-topic")
                .build();
        kafkaTemplate.send(message);

        log.info("sent initial stock event to the inventory of product Id {}", productCreatedEvent.getProductId());
        kafkaTemplate.flush();


    }
}
