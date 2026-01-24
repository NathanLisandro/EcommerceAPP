package dev.nathan.order.mapper;

import dev.nathan.order.controller.request.CreateOrderRequest;
import dev.nathan.order.controller.response.OrderResponse;
import dev.nathan.order.entity.Order;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {

    public static Order toOrder(CreateOrderRequest request) {
        return Order.builder()
                .customerId(request.getCustomerId())
                .basketId(request.getBasketId())
                .amount(request.getAmount())
                .shippingCost(request.getShippingCost())
                .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .basketId(order.getBasketId())
                .totalAmount(order.totalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }
}
