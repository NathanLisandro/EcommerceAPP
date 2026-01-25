package dev.nathan.order.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.nathan.order.entity.OrderStatus;
import dev.nathan.order.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@Schema(description = "Order response")
public class OrderResponse {

    @Schema(description = "Unique order identifier", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Customer identifier", example = "cust-123")
    private String customerId;

    @Schema(description = "Basket identifier", example = "basket-456")
    private String basketId;

    @Schema(description = "Total amount including shipping", example = "109.90")
    private BigDecimal totalAmount;

    @Schema(description = "Current order status", example = "CREATED")
    private OrderStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Payment method (if paid)", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;
}
