package org.effective_mobile.task_management_system.config;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.effective_mobile.task_management_system.security.EmailAsUsernameProvider;
import org.effective_mobile.task_management_system.security.UsernameProvider;
import org.effective_mobile.task_management_system.security.authentication.AuthenticationComponent;
import org.effective_mobile.task_management_system.security.authentication.AuthenticationComponentImpl;
import org.effective_mobile.task_management_system.security.authentication.AuthenticationTokenComponent;
import org.effective_mobile.task_management_system.security.authentication.AuthenticationTokenFilter;
import org.effective_mobile.task_management_system.security.authorization.AuthorizationComponent;
import org.effective_mobile.task_management_system.security.authorization.AuthorizationComponentImpl;
import org.effective_mobile.task_management_system.security.authorization.AuthorizationFilter;
import org.effective_mobile.task_management_system.security.authorization.privilege.PrivilegesComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain configure(
        final HttpSecurity http,
        final AuthenticationTokenFilter authenticationTokenFilter,
        final AuthorizationFilter authorizationFilter
    ) throws Exception {
        return http.cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UsernameProvider usernameProvider(UserComponent userComponent) {
        return new EmailAsUsernameProvider(userComponent);
    }

    @Bean
    public AuthenticationManager authenticationManager(
        final AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationComponent authenticationComponent(
        final AuthenticationTokenComponent authenticationTokenComponent,
        final ContextComponent contextComponent
    ) {
        return new AuthenticationComponentImpl(authenticationTokenComponent, contextComponent);
    }

    @Bean
    public AuthorizationComponent authorizationComponent(
        UserComponent userComponent,
        ContextComponent contextComponent,
        TaskComponent taskComponent,
        PrivilegesComponent privilegesComponent
    ) {
        return new AuthorizationComponentImpl(
            userComponent,
            contextComponent,
            taskComponent,
            privilegesComponent);
    }

}

