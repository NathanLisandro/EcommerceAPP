package dev.nathan.order.service;
import dev.nathan.order.entity.Order;
import dev.nathan.order.entity.OrderEvent;
import dev.nathan.order.entity.OrderStatus;
import dev.nathan.order.message.NotificationMessage;
import dev.nathan.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateOrderService Unit Tests")
class UpdateOrderServiceTest {
    @Mock
    private OrderRepository repository;
    @Mock
    private OrderStateService orderStateService;
    @Mock
    private NotificationProducerService notificationProducerService;
    @InjectMocks
    private UpdateOrderService updateOrderService;
    private Order existingOrder;
    @BeforeEach
    void setUp() {
        existingOrder = Order.builder()
                .id("order-123")
                .customerId("customer-456")
                .basketId("basket-789")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();
    }
    @Test
    @DisplayName("Should update order status from CREATED to PAID")
    void shouldUpdateOrderStatusFromCreatedToPaid() {
        String orderId = "order-123";
        OrderEvent event = OrderEvent.PAY;
        when(repository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderStateService.processEvent(OrderStatus.CREATED, event)).thenReturn(OrderStatus.PAID);
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        Order result = updateOrderService.execute(orderId, event);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.getUpdatedAt()).isNotNull();
    }
    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        String orderId = "non-existent-order";
        OrderEvent event = OrderEvent.PAY;
        when(repository.findById(orderId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updateOrderService.execute(orderId, event))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Order not found");
    }
    @Test
    @DisplayName("Should send notification when order is updated")
    void shouldSendNotificationWhenOrderUpdated() {
        String orderId = "order-123";
        OrderEvent event = OrderEvent.SHIP;
        when(repository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderStateService.processEvent(any(), any())).thenReturn(OrderStatus.SHIPPED);
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        updateOrderService.execute(orderId, event);
        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationProducerService).send(messageCaptor.capture());
        NotificationMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getOrderId()).isEqualTo(orderId);
        assertThat(capturedMessage.getEvent()).isEqualTo(event);
    }
    @Test
    @DisplayName("Should set updatedAt timestamp when updating order")
    void shouldSetUpdatedAtTimestamp() {
        String orderId = "order-123";
        OrderEvent event = OrderEvent.PAY;
        when(repository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderStateService.processEvent(any(), any())).thenReturn(OrderStatus.PAID);
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        Order result = updateOrderService.execute(orderId, event);
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isAfter(existingOrder.getCreatedAt());
    }
    @Test
    @DisplayName("Should call state service to process event")
    void shouldCallStateServiceToProcessEvent() {
        String orderId = "order-123";
        OrderEvent event = OrderEvent.PAY;
        when(repository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderStateService.processEvent(OrderStatus.CREATED, event)).thenReturn(OrderStatus.PAID);
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        updateOrderService.execute(orderId, event);
        verify(orderStateService).processEvent(OrderStatus.CREATED, event);
    }
}
