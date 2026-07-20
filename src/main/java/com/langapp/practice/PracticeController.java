package com.langapp.practice;

import com.langapp.content.VocabBrowseService;
import com.langapp.content.VocabItem;
import com.langapp.user.AppUserDetails;
import com.langapp.user.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/practice")
public class PracticeController {

    private final PracticeService practiceService;
    private final VocabBrowseService vocabBrowseService;

    public PracticeController(PracticeService practiceService, VocabBrowseService vocabBrowseService) {
        this.practiceService = practiceService;
        this.vocabBrowseService = vocabBrowseService;
    }

    // --- Kelime Listesi (arama + sayfalama) ---

    @GetMapping("/words")
    public String wordList(@AuthenticationPrincipal AppUserDetails principal,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "") String search,
                            Model model) {
        User user = principal.getUser();
        Page<VocabItem> result = vocabBrowseService.browse(user.getTargetLanguage(), page, search);
        model.addAttribute("wordPage", result);
        model.addAttribute("search", search);
        return "practice/word-list";
    }

    // --- Flashcards ---

    @GetMapping("/flashcards")
    public String flashcards(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        User user = principal.getUser();
        model.addAttribute("flashcards", practiceService.getFlashcardsForLanguage(user.getTargetLanguage()));
        return "practice/flashcards";
    }

    @PostMapping("/flashcards/{id}/assess")
    @ResponseBody
    public String assessFlashcard(@AuthenticationPrincipal AppUserDetails principal,
                                   @PathVariable Long id,
                                   @RequestParam boolean knewIt) {
        practiceService.submitFlashcardSelfAssessment(principal.getUser(), id, knewIt);
        return "ok";
    }

    // --- Quiz ---

    @GetMapping("/quiz")
    public String quiz(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        User user = principal.getUser();
        model.addAttribute("questions", practiceService.getQuizForLanguage(user.getTargetLanguage()));
        return "practice/quiz";
    }

    @PostMapping("/quiz/{id}/answer")
    public String answerQuiz(@AuthenticationPrincipal AppUserDetails principal,
                              @PathVariable Long id,
                              @RequestParam String selectedAnswer,
                              Model model) {
        boolean correct = practiceService.submitQuizAnswer(principal.getUser(), id, selectedAnswer);
        model.addAttribute("result", correct ? "dogru" : "yanlis");
        model.addAttribute("questions", practiceService.getQuizForLanguage(principal.getUser().getTargetLanguage()));
        return "practice/quiz";
    }

    // --- Translation ---

    @GetMapping("/translation")
    public String translation(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        User user = principal.getUser();
        model.addAttribute("exercises", practiceService.getTranslationExercisesForLanguage(user.getTargetLanguage()));
        return "practice/translation";
    }

    @PostMapping("/translation/{id}/answer")
    public String answerTranslation(@AuthenticationPrincipal AppUserDetails principal,
                                     @PathVariable Long id,
                                     @RequestParam String userTranslation,
                                     Model model) {
        boolean correct = practiceService.submitTranslationAnswer(principal.getUser(), id, userTranslation);
        model.addAttribute("result", correct ? "dogru" : "yanlis");
        model.addAttribute("exercises", practiceService.getTranslationExercisesForLanguage(principal.getUser().getTargetLanguage()));
        return "practice/translation";
    }
}
