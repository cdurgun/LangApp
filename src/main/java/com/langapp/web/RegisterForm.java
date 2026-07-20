package com.langapp.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "Kullanici adi bos olamaz")
    @Size(min = 3, max = 50, message = "Kullanici adi 3-50 karakter olmali")
    private String username;

    @NotBlank(message = "E-posta bos olamaz")
    @Email(message = "Gecerli bir e-posta girin")
    private String email;

    @NotBlank(message = "Sifre bos olamaz")
    @Size(min = 6, message = "Sifre en az 6 karakter olmali")
    private String password;

    @NotBlank(message = "Hedef dil secin")
    private String targetLanguage;
}
