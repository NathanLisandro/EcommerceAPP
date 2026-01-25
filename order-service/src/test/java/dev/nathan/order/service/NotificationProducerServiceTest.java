package dev.nathan.order.service;
import dev.nathan.order.entity.OrderEvent;
import dev.nathan.order.message.NotificationMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.CompletableFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationProducerService Unit Tests")
class NotificationProducerServiceTest {
    @Mock
    private KafkaTemplate<String, NotificationMessage> kafkaTemplate;
    private NotificationProducerService notificationProducerService;
    @Captor
    private ArgumentCaptor<NotificationMessage> messageCaptor;
    private NotificationMessage testMessage;
    @BeforeEach
    void setUp() {
        notificationProducerService = new NotificationProducerService(kafkaTemplate);
        ReflectionTestUtils.setField(notificationProducerService, "topic", "notification-topic");
        testMessage = NotificationMessage.builder()
                .orderId("order-123")
                .message("Order created")
                .event(OrderEvent.CREATE)
                .build();
    }
    @Test
    @DisplayName("Should send notification message successfully")
    void shouldSendNotificationMessageSuccessfully() {
        CompletableFuture<SendResult<String, NotificationMessage>> future = new CompletableFuture<>();
        ProducerRecord<String, NotificationMessage> producerRecord = new ProducerRecord<>("notification-topic", "order-123", testMessage);
        RecordMetadata metadata = new RecordMetadata(new TopicPartition("notification-topic", 0), 0, 0, 0, 0, 0);
        SendResult<String, NotificationMessage> sendResult = new SendResult<>(producerRecord, metadata);
        future.complete(sendResult);
        when(kafkaTemplate.send(eq("notification-topic"), eq("order-123"), any(NotificationMessage.class)))
                .thenReturn(future);
        notificationProducerService.send(testMessage);
        verify(kafkaTemplate).send(eq("notification-topic"), eq("order-123"), messageCaptor.capture());
        NotificationMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getOrderId()).isEqualTo("order-123");
    }
    @Test
    @DisplayName("Should include message details in notification")
    void shouldIncludeMessageDetailsInNotification() {
        CompletableFuture<SendResult<String, NotificationMessage>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("notification-topic"), eq("order-123"), any(NotificationMessage.class)))
                .thenReturn(future);
        notificationProducerService.send(testMessage);
        verify(kafkaTemplate).send(eq("notification-topic"), eq("order-123"), messageCaptor.capture());
        NotificationMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getMessage()).isEqualTo("Order created");
        assertThat(capturedMessage.getEvent()).isEqualTo(OrderEvent.CREATE);
    }
    @Test
    @DisplayName("Should use order id as kafka message key")
    void shouldUseOrderIdAsKafkaMessageKey() {
        CompletableFuture<SendResult<String, NotificationMessage>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("notification-topic"), eq("order-123"), any(NotificationMessage.class)))
                .thenReturn(future);
        notificationProducerService.send(testMessage);
        verify(kafkaTemplate).send(eq("notification-topic"), eq("order-123"), any(NotificationMessage.class));
    }
    @Test
    @DisplayName("Should handle different events")
    void shouldHandleDifferentEvents() {
        NotificationMessage payMessage = NotificationMessage.builder()
                .orderId("order-456")
                .message("Order paid")
                .event(OrderEvent.PAY)
                .build();
        CompletableFuture<SendResult<String, NotificationMessage>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("notification-topic"), eq("order-456"), any(NotificationMessage.class)))
                .thenReturn(future);
        notificationProducerService.send(payMessage);
        verify(kafkaTemplate).send(eq("notification-topic"), eq("order-456"), messageCaptor.capture());
        assertThat(messageCaptor.getValue().getEvent()).isEqualTo(OrderEvent.PAY);
    }
    @Test
    @DisplayName("Should log notification details")
    void shouldLogNotificationDetails() {
        CompletableFuture<SendResult<String, NotificationMessage>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(eq("notification-topic"), eq("order-123"), any(NotificationMessage.class)))
                .thenReturn(future);
        notificationProducerService.send(testMessage);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(NotificationMessage.class));
    }
}
