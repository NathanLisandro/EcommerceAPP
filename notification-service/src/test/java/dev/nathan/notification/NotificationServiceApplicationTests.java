package dev.nathan.notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("Notification Service Application Tests")
class NotificationServiceApplicationTests {
	@Test
	@DisplayName("Application class should be instantiable")
	void applicationClassShouldBeInstantiable() {
		NotificationServiceApplication app = new NotificationServiceApplication();
		assertThat(app).isNotNull();
	}
}
