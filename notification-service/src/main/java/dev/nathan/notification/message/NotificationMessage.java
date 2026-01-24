package dev.nathan.notification.message;

import dev.nathan.notification.entity.OrderEvent;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private String orderId;
    private String message;
    private OrderEvent event;

}
