package com.langapp.progress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndTopicId(Long userId, Long topicId);

    /**
     * open-in-view=false oldugu icin view render edilirken Hibernate oturumu kapali olur.
     * Topic LAZY oldugundan JOIN FETCH ile birlikte cekmezsek Thymeleaf'te
     * LazyInitializationException alinir. Dashboard bu metodu kullanmali.
     */
    @Query("SELECT up FROM UserProgress up JOIN FETCH up.topic WHERE up.user.id = :userId")
    List<UserProgress> findByUserIdWithTopic(@Param("userId") Long userId);

    List<UserProgress> findByUserIdAndTopicLanguageCode(Long userId, String languageCode);
}
