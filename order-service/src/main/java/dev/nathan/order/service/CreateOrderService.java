package dev.nathan.order.service;

import dev.nathan.order.entity.Order;
import dev.nathan.order.entity.OrderEvent;
import dev.nathan.order.entity.OrderStatus;
import dev.nathan.order.message.NotificationMessage;
import dev.nathan.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateOrderService {

    private final OrderRepository repository;
    private final NotificationProducerService notificationProducerService;

    public CreateOrderService(OrderRepository repository, NotificationProducerService notificationProducerService) {
        this.repository = repository;
        this.notificationProducerService = notificationProducerService;
    }

    @Transactional
    public Order execute(Order order) {
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = repository.save(order);

        notificationProducerService.send(NotificationMessage.builder()
                .orderId(order.getId())
                .message("Order created")
                .event(OrderEvent.CREATE)
                .build());

        return savedOrder;


    }
}
