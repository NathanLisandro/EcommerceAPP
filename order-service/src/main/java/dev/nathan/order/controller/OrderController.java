package dev.nathan.order.controller;

import dev.nathan.order.controller.request.CreateOrderRequest;
import dev.nathan.order.controller.request.UpdateOrderRequest;
import dev.nathan.order.controller.response.OrderResponse;
import dev.nathan.order.entity.Order;
import dev.nathan.order.mapper.OrderMapper;
import dev.nathan.order.service.CreateOrderService;
import dev.nathan.order.service.UpdateOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final UpdateOrderService updateOrderService;

    public OrderController(CreateOrderService createOrderService, UpdateOrderService updateOrderService) {
        this.createOrderService = createOrderService;
        this.updateOrderService = updateOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody CreateOrderRequest request) {
        log.info("[API] POST /order - Creating order: customerId={}, basketId={}, amount={}",
                request.getCustomerId(), request.getBasketId(), request.getAmount());
        
        long startTime = System.currentTimeMillis();
        Order savedOrder = createOrderService.execute(OrderMapper.toOrder(request));
        OrderResponse response = OrderMapper.toOrderResponse(savedOrder);
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[API] POST /order - Response: orderId={}, status={}, duration={}ms",
                response.getId(), response.getStatus(), duration);
        
        return response;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateOrder(@RequestBody UpdateOrderRequest request) {
        log.info("[API] PUT /order - Updating order: orderId={}, event={}",
                request.getOrderId(), request.getOrderEvent());
        
        long startTime = System.currentTimeMillis();
        Order savedOrder = updateOrderService.execute(request.getOrderId(), request.getOrderEvent());
        OrderResponse response = OrderMapper.toOrderResponse(savedOrder);
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[API] PUT /order - Response: orderId={}, newStatus={}, duration={}ms",
                response.getId(), response.getStatus(), duration);
        
        return response;
    }
}
