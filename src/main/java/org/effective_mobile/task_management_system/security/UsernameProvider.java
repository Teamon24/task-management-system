package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Контракт содержит логику, которая определяет,
 * что за данные будут использоваться в качестве {@link UserDetails#getUsername()}
 */
public interface UsernameProvider {
    User getUserBy(String username);
    String getUsername(User user);
    String getUsername(SigninRequestPojo pojo);
    Object getCredentials(SigninRequestPojo pojo);
    String getSubject(UsernamePasswordAuthenticationToken authentication);
    String getSubject(UserDetails userDetails);
}
