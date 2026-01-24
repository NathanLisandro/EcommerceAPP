package dev.nathan.order.controller.request;

import dev.nathan.order.entity.OrderEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateOrderRequest {

    private String orderId;
    private OrderEvent orderEvent;
}
