package org.effective_mobile.task_management_system.component;

import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class ContextComponent {
    public CustomUserDetails getPrincipal() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional
            .ofNullable(requestAttributes)
            .orElseThrow(() -> new RuntimeException("There is no request in context"))
            .getRequest();
    }

    public void setAuthentication(UsernamePasswordAuthenticationToken authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
