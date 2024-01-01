package org.effective_mobile.task_management_system.resource.json.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;
import org.effective_mobile.task_management_system.component.validator.Signup;
import org.effective_mobile.task_management_system.component.validator.StrongPassword;

import static org.effective_mobile.task_management_system.component.validator.Signup.Type.EMAIL;
import static org.effective_mobile.task_management_system.component.validator.Signup.Type.USERNAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequestPojo implements RequestPojo {

    @JsonProperty
    @Email
    @Signup(field = EMAIL)
    private String email;

    @JsonProperty
    @Signup(field = USERNAME)
    private String username;

    @JsonProperty
    @StrongPassword private String password;
}
