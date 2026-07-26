package com.langapp.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "{register.error.usernameBlank}")
    @Size(min = 3, max = 50, message = "{register.error.usernameSize}")
    private String username;

    @NotBlank(message = "{register.error.emailBlank}")
    @Email(message = "{register.error.emailInvalid}")
    private String email;

    @NotBlank(message = "{register.error.passwordBlank}")
    @Size(min = 6, message = "{register.error.passwordSize}")
    private String password;

    @NotBlank(message = "{register.error.languageBlank}")
    private String targetLanguage;
}
