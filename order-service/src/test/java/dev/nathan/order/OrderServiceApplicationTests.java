package dev.nathan.order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("Order Service Application Tests")
class OrderServiceApplicationTests {
	@Test
	@DisplayName("Application class should be instantiable")
	void applicationClassShouldBeInstantiable() {
		OrderServiceApplication app = new OrderServiceApplication();
		assertThat(app).isNotNull();
	}
}
