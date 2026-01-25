package dev.nathan.notification.service;

import dev.nathan.notification.message.NotificationMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationListenerService {

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group}")
    @CircuitBreaker(name = "notification-processor", fallbackMethod = "processFallback")
    @Retry(name = "notification-processor")
    public void consumeMessage(ConsumerRecord<String, NotificationMessage> record) {
        NotificationMessage message = record.value();
        
        log.info("[KAFKA] Message received: topic={}, partition={}, offset={}, key={}",
                record.topic(), record.partition(), record.offset(), record.key());
        
        log.info("[NOTIFICATION] Processing notification: orderId={}, event={}, message={}",
                message.getOrderId(), message.getEvent(), message.getMessage());
        
        long startTime = System.currentTimeMillis();
        
        try {
            processNotification(message);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("[NOTIFICATION] Notification processed successfully: orderId={}, event={}, duration={}ms",
                    message.getOrderId(), message.getEvent(), duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[NOTIFICATION] Failed to process notification: orderId={}, event={}, error={}, duration={}ms",
                    message.getOrderId(), message.getEvent(), e.getMessage(), duration);
            throw e;
        }
    }

    private void processNotification(NotificationMessage message) {
        log.debug("[NOTIFICATION] Executing notification logic for orderId={}", message.getOrderId());
        
        switch (message.getEvent()) {
            case CREATE -> {
                log.info("[NOTIFICATION] Order created notification: orderId={}", message.getOrderId());
            }
            case PAY -> {
                log.info("[NOTIFICATION] Payment confirmed notification: orderId={}", message.getOrderId());
            }
            case SHIP -> {
                log.info("[NOTIFICATION] Order shipped notification: orderId={}", message.getOrderId());
            }
            case DELIVER -> {
                log.info("[NOTIFICATION] Order delivered notification: orderId={}", message.getOrderId());
            }
            case CANCEL -> {
                log.warn("[NOTIFICATION] Order cancelled notification: orderId={}", message.getOrderId());
            }
            default -> {
                log.warn("[NOTIFICATION] Unknown event type: orderId={}, event={}", 
                        message.getOrderId(), message.getEvent());
            }
        }
    }

    private void processFallback(ConsumerRecord<String, NotificationMessage> record, Throwable t) {
        NotificationMessage message = record.value();
        log.error("[NOTIFICATION] Circuit breaker OPEN - Failed to process notification: orderId={}, event={}, error={}",
                message.getOrderId(), message.getEvent(), t.getMessage());
    }
}
