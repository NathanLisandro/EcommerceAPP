package dev.nathan.order.message;

import dev.nathan.order.entity.OrderEvent;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private String orderId;
    private String message;
    private OrderEvent event;

}
