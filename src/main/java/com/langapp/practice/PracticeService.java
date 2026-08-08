package com.langapp.practice;

import com.langapp.content.*;
import com.langapp.progress.Attempt;
import com.langapp.progress.AttemptRepository;
import com.langapp.progress.ExerciseType;
import com.langapp.progress.FlashcardReview;
import com.langapp.progress.FlashcardReviewRepository;
import com.langapp.progress.UserProgress;
import com.langapp.progress.UserProgressRepository;
import com.langapp.user.User;
import com.langapp.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class PracticeService {

    private final VocabItemRepository vocabItemRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final TranslationExerciseRepository translationExerciseRepository;
    private final TopicRepository topicRepository;
    private final UserProgressRepository userProgressRepository;
    private final AttemptRepository attemptRepository;
    private final FlashcardReviewRepository flashcardReviewRepository;
    private final AnswerCheckService answerCheckService;
    private final UserService userService;

    public PracticeService(VocabItemRepository vocabItemRepository,
                            QuizQuestionRepository quizQuestionRepository,
                            TranslationExerciseRepository translationExerciseRepository,
                            TopicRepository topicRepository,
                            UserProgressRepository userProgressRepository,
                            AttemptRepository attemptRepository,
                            FlashcardReviewRepository flashcardReviewRepository,
                            AnswerCheckService answerCheckService,
                            UserService userService) {
        this.vocabItemRepository = vocabItemRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.translationExerciseRepository = translationExerciseRepository;
        this.topicRepository = topicRepository;
        this.userProgressRepository = userProgressRepository;
        this.attemptRepository = attemptRepository;
        this.flashcardReviewRepository = flashcardReviewRepository;
        this.answerCheckService = answerCheckService;
        this.userService = userService;
    }

    /**
     * Sadece "suresi gelen" kelimeleri dondurur: hic incelenmemis (yeni) kelimeler
     * ya da SM-2 tekrar tarihi bugune gelmis/gecmis kelimeler. Iyi bilinen
     * kelimeler, tekrar tarihleri ilerledigi icin giderek daha az sikliklarla
     * bu listeye girer.
     */
    public List<FlashcardView> getFlashcardsForLanguage(User user, String languageCode) {
        List<VocabItem> items = vocabItemRepository.findDueFlashcards(user.getId(), languageCode);
        Collections.shuffle(items);
        return items.stream()
                .map(item -> new FlashcardView(item.getId(), item.getSourceText(), item.getTargetText(), item.getAudioUrl()))
                .toList();
    }

    /** Bu dilde hic kelime var mi kontrolu - "bugun tekrar yok" ile "hic kelime yok" mesajlarini ayirt etmek icin. */
    public boolean hasAnyWordsForLanguage(String languageCode) {
        return !vocabItemRepository.findByTopicLanguageCode(languageCode).isEmpty();
    }

    public List<QuizQuestion> getQuizForLanguage(String languageCode) {
        List<QuizQuestion> questions = quizQuestionRepository.findByTopicLanguageCode(languageCode);
        Collections.shuffle(questions);
        return questions;
    }

    public List<TranslationExercise> getTranslationExercisesForLanguage(String languageCode) {
        List<TranslationExercise> exercises = translationExerciseRepository.findByTopicLanguageCode(languageCode);
        Collections.shuffle(exercises);
        return exercises;
    }

    @Transactional
    public boolean submitQuizAnswer(User user, Long questionId, String selectedAnswer) {
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Soru bulunamadi: " + questionId));
        boolean correct = question.getCorrectAnswer().equalsIgnoreCase(selectedAnswer == null ? "" : selectedAnswer.trim());
        recordAttempt(user, ExerciseType.QUIZ, questionId, correct, selectedAnswer, question.getTopic());
        return correct;
    }

    @Transactional
    public boolean submitTranslationAnswer(User user, Long exerciseId, String userTranslation) {
        TranslationExercise exercise = translationExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Alistirma bulunamadi: " + exerciseId));
        boolean correct = answerCheckService.isRoughlyCorrect(userTranslation, exercise.getExpectedTranslation());
        recordAttempt(user, ExerciseType.TRANSLATION, exerciseId, correct, userTranslation, exercise.getTopic());
        return correct;
    }

    /**
     * Flashcard'i "biliyorum" / "bilmiyorum" olarak isaretlemek icin - kullanici kendi degerlendirir.
     * Hem eski (konu bazinda mastery %) takibini hem yeni SM-2 tekrar zamanlamasini gunceller.
     * @return bir sonraki tekrara kadar gecen gun sayisi (arayuzde geri bildirim icin)
     */
    @Transactional
    public int submitFlashcardSelfAssessment(User user, Long vocabItemId, boolean knewIt) {
        VocabItem item = vocabItemRepository.findById(vocabItemId)
                .orElseThrow(() -> new IllegalArgumentException("Kelime bulunamadi: " + vocabItemId));
        recordAttempt(user, ExerciseType.FLASHCARD, vocabItemId, knewIt, null, item.getTopic());

        FlashcardReview review = flashcardReviewRepository.findByUserIdAndVocabItemId(user.getId(), vocabItemId)
                .orElseGet(() -> new FlashcardReview(user, item));
        review.applyReview(knewIt);
        flashcardReviewRepository.save(review);

        return review.getIntervalDays();
    }

    private void recordAttempt(User user, ExerciseType type, Long exerciseId, boolean correct, String userAnswer, Topic topic) {
        attemptRepository.save(new Attempt(user, type, exerciseId, correct, userAnswer));

        UserProgress progress = userProgressRepository.findByUserIdAndTopicId(user.getId(), topic.getId())
                .orElseGet(() -> new UserProgress(user, topic));
        progress.applyAttemptResult(correct);
        userProgressRepository.save(progress);

        userService.registerActivity(user);
    }
}
