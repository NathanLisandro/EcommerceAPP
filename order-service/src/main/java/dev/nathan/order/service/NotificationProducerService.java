package dev.nathan.order.service;

import dev.nathan.order.message.NotificationMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class NotificationProducerService {

    @Value("${kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public NotificationProducerService(KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @CircuitBreaker(name = "kafka-producer", fallbackMethod = "sendFallback")
    @Retry(name = "kafka-producer")
    public void send(NotificationMessage message) {
        log.info("[KAFKA] Sending message to topic '{}': orderId={}, event={}", 
                topic, message.getOrderId(), message.getEvent());
        
        long startTime = System.currentTimeMillis();
        
        CompletableFuture<SendResult<String, NotificationMessage>> future = 
                kafkaTemplate.send(topic, message.getOrderId(), message);
        
        future.whenComplete((result, ex) -> {
            long duration = System.currentTimeMillis() - startTime;
            if (ex == null) {
                log.info("[KAFKA] Message sent successfully: topic={}, partition={}, offset={}, duration={}ms",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        duration);
            } else {
                log.error("[KAFKA] Failed to send message: orderId={}, error={}, duration={}ms",
                        message.getOrderId(), ex.getMessage(), duration);
            }
        });
    }

    private void sendFallback(NotificationMessage message, Throwable t) {
        log.error("[KAFKA] Circuit breaker OPEN - Message queued for retry: orderId={}, event={}, error={}",
                message.getOrderId(), message.getEvent(), t.getMessage());
    }
}
