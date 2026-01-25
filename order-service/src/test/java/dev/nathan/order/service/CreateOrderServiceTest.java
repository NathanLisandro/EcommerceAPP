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
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderService Unit Tests")
class CreateOrderServiceTest {
    @Mock
    private OrderRepository repository;
    @Mock
    private NotificationProducerService notificationProducerService;
    @InjectMocks
    private CreateOrderService createOrderService;
    private Order order;
    @BeforeEach
    void setUp() {
        order = Order.builder()
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .build();
    }
    @Test
    @DisplayName("Should create order with CREATED status")
    void shouldCreateOrderWithCreatedStatus() {
        Order savedOrder = Order.builder()
                .id("order-789")
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .status(OrderStatus.CREATED)
                .build();
        when(repository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        Order result = createOrderService.execute(order);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("order-789");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getCustomerId()).isEqualTo("customer-123");
    }
    @Test
    @DisplayName("Should set createdAt timestamp when creating order")
    void shouldSetCreatedAtTimestamp() {
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        createOrderService.execute(order);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(repository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getCreatedAt()).isNotNull();
        assertThat(capturedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
    }
    @Test
    @DisplayName("Should send notification when order is created")
    void shouldSendNotificationWhenOrderCreated() {
        Order savedOrder = Order.builder()
                .id("order-789")
                .customerId("customer-123")
                .basketId("basket-456")
                .amount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .status(OrderStatus.CREATED)
                .build();
        when(repository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        createOrderService.execute(order);
        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationProducerService).send(messageCaptor.capture());
        NotificationMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getOrderId()).isEqualTo("order-789");
        assertThat(capturedMessage.getEvent()).isEqualTo(OrderEvent.CREATE);
        assertThat(capturedMessage.getMessage()).isEqualTo("Order created");
    }
    @Test
    @DisplayName("Should save order to repository")
    void shouldSaveOrderToRepository() {
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationProducerService).send(any(NotificationMessage.class));
        createOrderService.execute(order);
        verify(repository, times(1)).save(any(Order.class));
    }
}
