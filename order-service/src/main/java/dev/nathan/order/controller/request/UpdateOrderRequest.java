package dev.nathan.order.controller.request;

import dev.nathan.order.entity.OrderEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an order status")
public class UpdateOrderRequest {

    @NotBlank(message = "Order ID is required")
    @Schema(description = "Unique identifier of the order", example = "507f1f77bcf86cd799439011")
    private String orderId;

    @NotNull(message = "Order event is required")
    @Schema(description = "Event to trigger state transition", example = "PAY")
    private OrderEvent orderEvent;
}
