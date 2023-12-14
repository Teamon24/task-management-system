package org.effective_mobile.task_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.InvalidTokenException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenComponent {
    ResponseCookie getCleanTokenCookie();
    String getTokenFromCookies(HttpServletRequest request);
    ResponseCookie generateTokenCookie(UserDetails userDetails);
    String generateToken(final UserDetails userDetails);
    String validateToken(final String token) throws InvalidTokenException;
}
