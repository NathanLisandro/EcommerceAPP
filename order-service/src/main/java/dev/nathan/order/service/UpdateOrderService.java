package dev.nathan.order.service;

import dev.nathan.order.entity.Order;
import dev.nathan.order.entity.OrderEvent;
import dev.nathan.order.entity.OrderStatus;
import dev.nathan.order.message.NotificationMessage;
import dev.nathan.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UpdateOrderService {

    private final OrderRepository repository;
    private final OrderStateService orderStateService;
    private final NotificationProducerService notificationProducerService;

    public UpdateOrderService(OrderRepository repository, OrderStateService orderStateService, NotificationProducerService notificationProducerService) {
        this.repository = repository;
        this.orderStateService = orderStateService;
        this.notificationProducerService = notificationProducerService;
    }

    @Transactional
    @CircuitBreaker(name = "mongodb", fallbackMethod = "executeFallback")
    @Retry(name = "mongodb")
    public Order execute(String orderId, OrderEvent event) {
        log.info("[ORDER] Updating order: orderId={}, event={}", orderId, event);
        
        long startTime = System.currentTimeMillis();
        
        Order order = repository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[ORDER] Order not found: orderId={}", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        log.debug("[ORDER] Current order state: orderId={}, status={}", orderId, order.getStatus());
        
        OrderStatus previousStatus = order.getStatus();
        OrderStatus newStatus = orderStateService.processEvent(order.getStatus(), event);

        log.info("[ORDER] State transition: orderId={}, from={}, to={}, event={}",
                orderId, previousStatus, newStatus, event);

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = repository.save(order);
        
        long dbDuration = System.currentTimeMillis() - startTime;
        log.info("[ORDER] Order updated in MongoDB: orderId={}, duration={}ms", orderId, dbDuration);

        log.debug("[ORDER] Sending notification for order update: orderId={}, newStatus={}", orderId, newStatus);
        notificationProducerService.send(NotificationMessage.builder()
                .orderId(order.getId())
                .message("Order status updated to " + newStatus)
                .event(event)
                .build());

        long totalDuration = System.currentTimeMillis() - startTime;
        log.info("[ORDER] Order update completed: orderId={}, newStatus={}, totalDuration={}ms",
                orderId, newStatus, totalDuration);

        return updatedOrder;
    }

    private Order executeFallback(String orderId, OrderEvent event, Throwable t) {
        log.error("[ORDER] Circuit breaker OPEN - Failed to update order: orderId={}, event={}, error={}",
                orderId, event, t.getMessage());
        throw new RuntimeException("Service temporarily unavailable. Please try again later.", t);
    }
}
