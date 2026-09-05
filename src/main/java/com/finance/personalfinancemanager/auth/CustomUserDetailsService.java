package com.finance.personalfinancemanager.auth;

import com.finance.personalfinancemanager.user.User;
import com.finance.personalfinancemanager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

/** Loads users for Spring Security authentication. */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = username.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByUsername(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return new CustomUserDetails(user);
    }
}