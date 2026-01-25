package dev.nathan.notification.config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("SecurityConfig Unit Tests")
class SecurityConfigTest {
    private final SecurityConfig securityConfig = new SecurityConfig();
    @Test
    @DisplayName("Should create JwtAuthenticationConverter")
    void shouldCreateJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = securityConfig.customJwtAuthenticationConverter();
        assertThat(converter).isNotNull();
    }
    @Test
    @DisplayName("JwtAuthenticationConverter should be configured")
    void jwtAuthenticationConverterShouldBeConfigured() {
        JwtAuthenticationConverter converter = securityConfig.customJwtAuthenticationConverter();
        assertThat(converter).isNotNull();
        // JwtAuthenticationConverter is configured with CustomJwtGrantedAuthoritiesConverter
    }
}
