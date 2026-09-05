package com.finance.personalfinancemanager.util;

import com.finance.personalfinancemanager.auth.CustomUserDetails;
import com.finance.personalfinancemanager.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Resolves the authenticated user id from the SecurityContext. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * @return id of the currently authenticated user
     * @throws UnauthorizedException when no valid authentication exists
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails principal) {
            return principal.getId();
        }

        throw new UnauthorizedException("Unauthorized");
    }
}