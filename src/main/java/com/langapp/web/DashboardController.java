package com.langapp.web;

import com.langapp.progress.ProgressService;
import com.langapp.user.AppUserDetails;
import com.langapp.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProgressService progressService;

    public DashboardController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        User user = principal.getUser();
        model.addAttribute("user", user);
        model.addAttribute("progressList", progressService.getProgressForUser(user));
        model.addAttribute("accuracy", progressService.getOverallAccuracyPercent(user));
        return "dashboard";
    }
}
