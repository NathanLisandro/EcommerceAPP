package dev.nathan.order.mapper;
import dev.nathan.order.controller.request.CreateOrderRequest;
import dev.nathan.order.controller.response.OrderResponse;
import dev.nathan.order.entity.Order;
import dev.nathan.order.entity.OrderStatus;
import dev.nathan.order.entity.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("OrderMapper Unit Tests")
class OrderMapperTest {
    @Test
    @DisplayName("Should map CreateOrderRequest to Order entity")
    void shouldMapCreateOrderRequestToOrder() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .build();
        Order result = OrderMapper.toOrder(request);
        assertThat(result.getCustomerId()).isEqualTo("customer-123");
        assertThat(result.getBasketId()).isEqualTo("basket-456");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.getShippingCost()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(result.getId()).isNull();
        assertThat(result.getStatus()).isNull();
    }
    @Test
    @DisplayName("Should map Order entity to OrderResponse")
    void shouldMapOrderToOrderResponse() {
        Order order = Order.builder()
                .id("order-789")
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .status(OrderStatus.PAID)
                .paymentMethod(PaymentMethod.CREDIT)
                .build();
        OrderResponse result = OrderMapper.toOrderResponse(order);
        assertThat(result.getId()).isEqualTo("order-789");
        assertThat(result.getCustomerId()).isEqualTo("customer-123");
        assertThat(result.getBasketId()).isEqualTo("basket-456");
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("110.00"));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT);
    }
    @Test
    @DisplayName("Should calculate total amount correctly")
    void shouldCalculateTotalAmountCorrectly() {
        Order order = Order.builder()
                .id("order-789")
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("99.99"))
                .shippingCost(new BigDecimal("15.50"))
                .status(OrderStatus.CREATED)
                .build();
        OrderResponse result = OrderMapper.toOrderResponse(order);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("115.49"));
    }
    @Test
    @DisplayName("Should handle null payment method")
    void shouldHandleNullPaymentMethod() {
        Order order = Order.builder()
                .id("order-789")
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .status(OrderStatus.CREATED)
                .paymentMethod(null)
                .build();
        OrderResponse result = OrderMapper.toOrderResponse(order);
        assertThat(result.getPaymentMethod()).isNull();
    }
}
