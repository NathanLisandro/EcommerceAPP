package dev.nathan.order.controller;

import dev.nathan.order.controller.request.CreateOrderRequest;
import dev.nathan.order.controller.request.UpdateOrderRequest;
import dev.nathan.order.controller.response.OrderResponse;
import dev.nathan.order.entity.Order;
import dev.nathan.order.mapper.OrderMapper;
import dev.nathan.order.service.CreateOrderService;
import dev.nathan.order.service.UpdateOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management API")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final UpdateOrderService updateOrderService;

    public OrderController(CreateOrderService createOrderService, UpdateOrderService updateOrderService) {
        this.createOrderService = createOrderService;
        this.updateOrderService = updateOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order", description = "Creates a new order with CREATED status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        log.info("[API] POST /api/v1/orders - Creating order: customerId={}, basketId={}, amount={}",
                request.getCustomerId(), request.getBasketId(), request.getAmount());
        
        long startTime = System.currentTimeMillis();
        Order savedOrder = createOrderService.execute(OrderMapper.toOrder(request));
        OrderResponse response = OrderMapper.toOrderResponse(savedOrder);
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[API] POST /api/v1/orders - Response: orderId={}, status={}, duration={}ms",
                response.getId(), response.getStatus(), duration);
        
        return response;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update order status", description = "Updates order status based on the event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or state transition"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public OrderResponse updateOrder(@Valid @RequestBody UpdateOrderRequest request) {
        log.info("[API] PUT /api/v1/orders - Updating order: orderId={}, event={}",
                request.getOrderId(), request.getOrderEvent());
        
        long startTime = System.currentTimeMillis();
        Order savedOrder = updateOrderService.execute(request.getOrderId(), request.getOrderEvent());
        OrderResponse response = OrderMapper.toOrderResponse(savedOrder);
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[API] PUT /api/v1/orders - Response: orderId={}, newStatus={}, duration={}ms",
                response.getId(), response.getStatus(), duration);
        
        return response;
    }
}
