package org.effective_mobile.task_management_system.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationTokenProperties {

    public AuthenticationTokenProperties(
        ObjectMapper objectMapper,
        @Value("${app.auth.token.ttl}") String tokenTimeToLiveInfo,
        @Value("${app.auth.token.name}") String authTokenName,
        @Value("${app.auth.secret}") final String secret
    ) throws JsonProcessingException {
        this.tokenTimeToLiveInfo = objectMapper.readValue(tokenTimeToLiveInfo, TimeToLiveInfo.class);
        this.authTokenName = authTokenName;
        this.secret = secret;
    }

    @Getter private final TimeToLiveInfo tokenTimeToLiveInfo;
    @Getter private final String authTokenName;
    @Getter private final String secret;
}
