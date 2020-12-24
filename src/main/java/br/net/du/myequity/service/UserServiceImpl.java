package br.net.du.myequity.service;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.UserRepository;
import com.google.common.collect.ImmutableSortedSet;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final String FIRST_SNAPSHOT_NAME = "First Snapshot";

    @Autowired private UserRepository userRepository;

    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void signUp(
            @NonNull final String email,
            @NonNull final String firstName,
            @NonNull final String lastName,
            @NonNull final String password) {
        final User user = new User(email.trim(), firstName.trim(), lastName.trim());
        user.setPassword(bCryptPasswordEncoder.encode(password));

        user.addSnapshot(new Snapshot(1L, FIRST_SNAPSHOT_NAME, ImmutableSortedSet.of()));

        save(user);
    }

    @Override
    public void save(final User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }
}
