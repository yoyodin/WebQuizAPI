package org.example.webquizapi.service;

import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.security.AppUserAdapter;
import org.example.webquizapi.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        AppUser user = new AppUser();
        user.setId(42L);
        user.setEmail("test@test.com");
        user.setPassword("password12");

        AppUserAdapter principal = new AppUserAdapter(user);
        auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    void generateToken_shouldReturnString() {
        var jwt = new Jwt("token",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.MINUTES),
                Map.of("header", 1),
                Map.of("claim", 1));

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        String token = tokenService.generateToken(auth);

        assertThat(token).isEqualTo("token");

        verify(jwtEncoder).encode(any());
    }
}
