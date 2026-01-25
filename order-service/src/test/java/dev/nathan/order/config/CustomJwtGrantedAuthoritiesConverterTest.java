package dev.nathan.order.config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("CustomJwtGrantedAuthoritiesConverter Unit Tests")
class CustomJwtGrantedAuthoritiesConverterTest {
    private CustomJwtGrantedAuthoritiesConverter converter;
    @BeforeEach
    void setUp() {
        converter = new CustomJwtGrantedAuthoritiesConverter();
    }
    @Test
    @DisplayName("Should extract roles from realm_access claim")
    void shouldExtractRolesFromRealmAccessClaim() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("admin", "user"));
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).hasSize(2);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_admin", "ROLE_user");
    }
    @Test
    @DisplayName("Should return empty collection when no realm_access claim")
    void shouldReturnEmptyWhenNoRealmAccessClaim() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "user123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }
    @Test
    @DisplayName("Should return empty collection when realm_access has no roles")
    void shouldReturnEmptyWhenRealmAccessHasNoRoles() {
        Map<String, Object> realmAccess = new HashMap<>();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }
    @Test
    @DisplayName("Should handle single role")
    void shouldHandleSingleRole() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("customer"));
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).hasSize(1);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_customer");
    }
    @Test
    @DisplayName("Should prefix all roles with ROLE_")
    void shouldPrefixAllRolesWithRolePrefix() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("manager", "supervisor", "employee"));
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).hasSize(3);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .allMatch(auth -> auth.startsWith("ROLE_"));
    }
}
