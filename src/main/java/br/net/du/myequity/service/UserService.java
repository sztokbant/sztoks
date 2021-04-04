package br.net.du.myequity.service;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.UserRepository;
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

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

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
        final String firstSnapshotName =
                String.format("%04d-%02d", now.getYear(), now.getMonth().getValue());
        user.addSnapshot(
                new Snapshot(
                        firstSnapshotName,
                        baseCurrencyUnit,
                        defaultTithingPercentage,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));

        save(user);
    }

    public void save(final User user) {
        userRepository.save(user);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }
}
