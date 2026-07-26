package com.langapp.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanici bulunamadi: " + username));
        return new AppUserDetails(user);
    }

    /**
     * Yeni kullanici kaydeder. Kullanici adi ya da e-posta zaten kayitliysa
     * IllegalArgumentException firlatir; controller bunu form hatasi olarak gosterir.
     */
    @Transactional
    public User register(String username, String email, String rawPassword, String targetLanguage) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("register.error.usernameTaken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("register.error.emailTaken");
        }
        User user = new User(username, email, passwordEncoder.encode(rawPassword), targetLanguage);
        return userRepository.save(user);
    }

    /** Kullanici her pratik yaptiginda cagrilir; streak hesaplamasini gunceller. */
    @Transactional
    public void registerActivity(User user) {
        LocalDate today = LocalDate.now();
        LocalDate last = user.getLastActiveDate();

        if (last == null || !last.equals(today)) {
            if (last != null && last.equals(today.minusDays(1))) {
                user.setCurrentStreak(user.getCurrentStreak() + 1);
            } else if (last == null || last.isBefore(today.minusDays(1))) {
                user.setCurrentStreak(1);
            }
            user.setLongestStreak(Math.max(user.getLongestStreak(), user.getCurrentStreak()));
            user.setLastActiveDate(today);
            userRepository.save(user);
        }
    }
}
