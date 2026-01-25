package dev.nathan.order.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order")
public class CreateOrderRequest {

    @NotBlank(message = "Customer ID is required")
    @Schema(description = "Unique identifier of the customer", example = "cust-123")
    private String customerId;

    @NotBlank(message = "Basket ID is required")
    @Schema(description = "Unique identifier of the shopping basket", example = "basket-456")
    private String basketId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(description = "Order amount without shipping", example = "99.90")
    private BigDecimal amount;

    @NotNull(message = "Shipping cost is required")
    @DecimalMin(value = "0.00", message = "Shipping cost cannot be negative")
    @Schema(description = "Shipping cost", example = "10.00")
    private BigDecimal shippingCost;

}
