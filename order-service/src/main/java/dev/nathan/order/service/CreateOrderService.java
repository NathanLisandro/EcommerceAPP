package dev.nathan.order.service;

import dev.nathan.order.entity.Order;
import dev.nathan.order.entity.OrderEvent;
import dev.nathan.order.entity.OrderStatus;
import dev.nathan.order.message.NotificationMessage;
import dev.nathan.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CreateOrderService {

    private final OrderRepository repository;
    private final NotificationProducerService notificationProducerService;

    public CreateOrderService(OrderRepository repository, NotificationProducerService notificationProducerService) {
        this.repository = repository;
        this.notificationProducerService = notificationProducerService;
    }

    @Transactional
    @CircuitBreaker(name = "mongodb", fallbackMethod = "executeFallback")
    @Retry(name = "mongodb")
    public Order execute(Order order) {
        log.info("[ORDER] Creating new order: customerId={}, basketId={}, amount={}",
                order.getCustomerId(), order.getBasketId(), order.getAmount());
        
        long startTime = System.currentTimeMillis();
        
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = repository.save(order);
        
        long dbDuration = System.currentTimeMillis() - startTime;
        log.info("[ORDER] Order persisted to MongoDB: orderId={}, status={}, duration={}ms",
                savedOrder.getId(), savedOrder.getStatus(), dbDuration);

        log.debug("[ORDER] Sending notification for order creation: orderId={}", savedOrder.getId());
        notificationProducerService.send(NotificationMessage.builder()
                .orderId(savedOrder.getId())
                .message("Order created")
                .event(OrderEvent.CREATE)
                .build());

        long totalDuration = System.currentTimeMillis() - startTime;
        log.info("[ORDER] Order creation completed: orderId={}, totalDuration={}ms", 
                savedOrder.getId(), totalDuration);

        return savedOrder;
    }

    private Order executeFallback(Order order, Throwable t) {
        log.error("[ORDER] Circuit breaker OPEN - Failed to create order: customerId={}, error={}",
                order.getCustomerId(), t.getMessage());
        throw new RuntimeException("Service temporarily unavailable. Please try again later.", t);
    }
}
