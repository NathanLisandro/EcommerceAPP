package dev.nathan.notification.service;
import dev.nathan.notification.entity.OrderEvent;
import dev.nathan.notification.message.NotificationMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatCode;
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationListenerService Unit Tests")
class NotificationListenerServiceTest {
    @InjectMocks
    private NotificationListenerService notificationListenerService;
    private NotificationMessage testMessage;
    private ConsumerRecord<String, NotificationMessage> consumerRecord;
    @BeforeEach
    void setUp() {
        testMessage = NotificationMessage.builder()
                .orderId("order-123")
                .message("Test notification")
                .event(OrderEvent.CREATE)
                .build();
        consumerRecord = new ConsumerRecord<>(
                "notification-topic",
                0,
                100L,
                "order-123",
                testMessage
        );
    }
    @Test
    @DisplayName("Should process notification message successfully")
    void shouldProcessNotificationMessageSuccessfully() {
        assertThatCode(() -> notificationListenerService.consumeMessage(consumerRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should process notification with CREATE event")
    void shouldProcessNotificationWithCreateEvent() {
        testMessage.setEvent(OrderEvent.CREATE);
        assertThatCode(() -> notificationListenerService.consumeMessage(consumerRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should process notification with PAY event")
    void shouldProcessNotificationWithPayEvent() {
        testMessage.setEvent(OrderEvent.PAY);
        assertThatCode(() -> notificationListenerService.consumeMessage(consumerRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should process notification with SHIP event")
    void shouldProcessNotificationWithShipEvent() {
        testMessage.setEvent(OrderEvent.SHIP);
        assertThatCode(() -> notificationListenerService.consumeMessage(consumerRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should process notification with DELIVER event")
    void shouldProcessNotificationWithDeliverEvent() {
        testMessage.setEvent(OrderEvent.DELIVER);
        assertThatCode(() -> notificationListenerService.consumeMessage(consumerRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should process notification with CANCEL event")
    void shouldProcessNotificationWithCancelEvent() {
        testMessage.setEvent(OrderEvent.CANCEL);
        assertThatCode(() -> notificationListenerService.consumeMessage(consumerRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should handle notification from different partitions")
    void shouldHandleNotificationFromDifferentPartitions() {
        ConsumerRecord<String, NotificationMessage> partitionRecord = new ConsumerRecord<>(
                "notification-topic",
                2,
                500L,
                "order-456",
                testMessage
        );
        assertThatCode(() -> notificationListenerService.consumeMessage(partitionRecord))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("Should handle different message content")
    void shouldHandleDifferentMessageContent() {
        NotificationMessage customMessage = NotificationMessage.builder()
                .orderId("order-custom")
                .message("Custom notification message")
                .event(OrderEvent.PAY)
                .build();
        ConsumerRecord<String, NotificationMessage> customRecord = new ConsumerRecord<>(
                "notification-topic",
                0,
                200L,
                "order-custom",
                customMessage
        );
        assertThatCode(() -> notificationListenerService.consumeMessage(customRecord))
                .doesNotThrowAnyException();
    }
}
