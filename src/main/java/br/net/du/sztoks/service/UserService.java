package br.net.du.sztoks.service;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.persistence.UserRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(
            @NonNull final String email,
            @NonNull final String firstName,
            @NonNull final String lastName,
            @NonNull final CurrencyUnit baseCurrencyUnit,
            @NonNull final BigDecimal defaultTithingPercentage,
            @NonNull final String password) {
        final User user = new User(email.trim(), firstName.trim(), lastName.trim());
        user.setPassword(passwordEncoder.encode(password));

        final LocalDate now = LocalDate.now();
        user.addSnapshot(
                new Snapshot(
                        now.getYear(),
                        now.getMonthValue(),
                        baseCurrencyUnit,
                        defaultTithingPercentage,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));

        save(user);
    }

    @Transactional
    public void save(final User user) {
        userRepository.save(user);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public boolean validateLogin(final String email, final String rawPassword) {
        final User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return true;
        }
        return false;
    }
}
