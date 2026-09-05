package com.finance.personalfinancemanager.auth;

import com.finance.personalfinancemanager.auth.dto.LoginRequest;
import com.finance.personalfinancemanager.auth.dto.RegisterRequest;
import com.finance.personalfinancemanager.auth.dto.RegisterResponse;
import com.finance.personalfinancemanager.exception.ConflictException;
import com.finance.personalfinancemanager.exception.UnauthorizedException;
import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Registration and session-based login.
 * The Node/Express analogy: {@code login()} is the equivalent of
 * {@code req.session.userId = user.id} — Spring stores the authenticated
 * principal in the HTTP session and issues the JSESSIONID cookie.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /**
     * Registers a new user with a BCrypt-hashed password.
     *
     * @throws ConflictException when the username (email) is already taken (409)
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.username().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .build();

        User saved = userRepository.save(user);

        return new RegisterResponse("User registered successfully", saved.getId());
    }

    /**
     * Authenticates credentials and binds the SecurityContext to a new HTTP session.
     *
     * @throws UnauthorizedException on invalid credentials (401)
     */
    public void login(LoginRequest request, HttpServletRequest httpRequest,
                      HttpServletResponse httpResponse) {
        String username = request.username().trim().toLowerCase(Locale.ROOT);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.password())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Persists the context into the HTTP session -> JSESSIONID cookie is set.
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }
}