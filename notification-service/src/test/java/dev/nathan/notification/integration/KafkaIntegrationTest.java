package dev.nathan.notification.integration;
import dev.nathan.notification.entity.OrderEvent;
import dev.nathan.notification.message.NotificationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("Notification Message Tests")
class NotificationMessageTest {
    @Test
    @DisplayName("Should create notification message with builder")
    void shouldCreateNotificationMessageWithBuilder() {
        NotificationMessage message = NotificationMessage.builder()
                .orderId("order-123")
                .message("Order created successfully")
                .event(OrderEvent.CREATE)
                .build();
        assertThat(message.getOrderId()).isEqualTo("order-123");
        assertThat(message.getMessage()).isEqualTo("Order created successfully");
        assertThat(message.getEvent()).isEqualTo(OrderEvent.CREATE);
    }
    @Test
    @DisplayName("Should update notification message fields")
    void shouldUpdateNotificationMessageFields() {
        NotificationMessage message = new NotificationMessage();
        message.setOrderId("order-456");
        message.setMessage("Payment received");
        message.setEvent(OrderEvent.PAY);
        assertThat(message.getOrderId()).isEqualTo("order-456");
        assertThat(message.getMessage()).isEqualTo("Payment received");
        assertThat(message.getEvent()).isEqualTo(OrderEvent.PAY);
    }
    @Test
    @DisplayName("Should create notification message with all args constructor")
    void shouldCreateNotificationMessageWithAllArgsConstructor() {
        NotificationMessage message = new NotificationMessage("order-789", "Order shipped", OrderEvent.SHIP);
        assertThat(message.getOrderId()).isEqualTo("order-789");
        assertThat(message.getMessage()).isEqualTo("Order shipped");
        assertThat(message.getEvent()).isEqualTo(OrderEvent.SHIP);
    }
    @Test
    @DisplayName("Should handle all event types")
    void shouldHandleAllEventTypes() {
        for (OrderEvent event : OrderEvent.values()) {
            NotificationMessage message = NotificationMessage.builder()
                    .orderId("order-test")
                    .event(event)
                    .build();
            assertThat(message.getEvent()).isEqualTo(event);
        }
    }
    @Test
    @DisplayName("Should generate toString correctly")
    void shouldGenerateToStringCorrectly() {
        NotificationMessage message = NotificationMessage.builder()
                .orderId("order-123")
                .message("Test message")
                .event(OrderEvent.CREATE)
                .build();
        String toString = message.toString();
        assertThat(toString).contains("order-123");
        assertThat(toString).contains("Test message");
        assertThat(toString).contains("CREATE");
    }
}
