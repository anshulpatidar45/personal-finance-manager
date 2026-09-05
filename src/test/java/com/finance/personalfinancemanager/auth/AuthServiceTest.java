package com.finance.personalfinancemanager.auth;

import com.finance.personalfinancemanager.auth.dto.LoginRequest;
import com.finance.personalfinancemanager.auth.dto.RegisterRequest;
import com.finance.personalfinancemanager.exception.ConflictException;
import com.finance.personalfinancemanager.exception.UnauthorizedException;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private HttpServletRequest httpRequest;
    @Mock private HttpServletResponse httpResponse;

    @InjectMocks private AuthService authService;

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest("test@test.com", "password123", "Test", "+123");
        when(userRepository.existsByUsername("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        var res = authService.register(req);
        assertEquals("User registered successfully", res.message());
        assertEquals(1L, res.userId());
    }

    @Test
    void register_DuplicateUsername_ThrowsConflict() {
        RegisterRequest req = new RegisterRequest("test@test.com", "password123", "Test", "+123");
        when(userRepository.existsByUsername("test@test.com")).thenReturn(true);
        assertThrows(ConflictException.class, () -> authService.register(req));
    }

    @Test
    void login_Success() {
        LoginRequest req = new LoginRequest("test@test.com", "password123");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        assertDoesNotThrow(() -> authService.login(req, httpRequest, httpResponse));
    }

    @Test
    void login_InvalidCredentials_ThrowsUnauthorized() {
        LoginRequest req = new LoginRequest("test@test.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad"));

        assertThrows(UnauthorizedException.class, () -> authService.login(req, httpRequest, httpResponse));
    }
}