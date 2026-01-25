package dev.nathan.notification.config;
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
        // No roles key
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
    @DisplayName("Should return empty collection when roles is empty")
    void shouldReturnEmptyWhenRolesIsEmpty() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of());
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
    @DisplayName("Should prefix roles with ROLE_")
    void shouldPrefixRolesWithRolePrefix() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("manager"));
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_manager");
    }
    @Test
    @DisplayName("Should handle multiple roles correctly")
    void shouldHandleMultipleRolesCorrectly() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("admin", "user", "manager", "viewer"));
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).hasSize(4);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_admin", "ROLE_user", "ROLE_manager", "ROLE_viewer");
    }
}
