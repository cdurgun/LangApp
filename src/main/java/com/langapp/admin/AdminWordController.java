package com.langapp.admin;

import com.langapp.content.*;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/words")
public class AdminWordController {

    private final AdminWordService adminWordService;
    private final VocabItemRepository vocabItemRepository;
    private final TopicRepository topicRepository;

    public AdminWordController(AdminWordService adminWordService,
                                VocabItemRepository vocabItemRepository,
                                TopicRepository topicRepository) {
        this.adminWordService = adminWordService;
        this.vocabItemRepository = vocabItemRepository;
        this.topicRepository = topicRepository;
    }

    /** Formda bos birakilan (opsiyonel) Long alanlar ("" secili) null'a donsun, hata firlatmasin. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
    }

    @GetMapping
    public String list(Model model) {
        if (!model.containsAttribute("wordForm")) {
            model.addAttribute("wordForm", new WordForm());
        }
        populateCommonAttributes(model);
        return "admin/words";
    }

    @PostMapping
    public String add(@Valid @ModelAttribute WordForm wordForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateCommonAttributes(model);
            return "admin/words";
        }
        adminWordService.addWord(wordForm);
        return "redirect:/admin/words?added";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminWordService.deleteWord(id);
        return "redirect:/admin/words?deleted";
    }

    private void populateCommonAttributes(Model model) {
        model.addAttribute("words", vocabItemRepository.findAllWithTopicAndLanguage());
        model.addAttribute("topics", topicRepository.findAllWithLanguage());
        model.addAttribute("wordTypes", WordType.values());
        model.addAttribute("aspects", VerbAspect.values());
        model.addAttribute("verbs", vocabItemRepository.findByWordType(WordType.VERB));
    }
}
