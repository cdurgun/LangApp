package com.langapp.web;

import com.langapp.user.UserService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final MessageSource messageSource;

    public AuthController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm registerForm,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.register(
                    registerForm.getUsername(),
                    registerForm.getEmail(),
                    registerForm.getPassword(),
                    registerForm.getTargetLanguage()
            );
        } catch (IllegalArgumentException ex) {
            // UserService, hata mesaji yerine bir i18n anahtari firlatiyor (orn. "register.error.usernameTaken");
            // burada kullanicinin diline gore cozumluyoruz.
            String localizedMessage = messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", localizedMessage);
            return "register";
        }
        return "redirect:/login?registered";
    }
}
