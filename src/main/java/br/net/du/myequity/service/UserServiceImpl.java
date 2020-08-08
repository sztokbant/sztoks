package br.net.du.myequity.service;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.UserRepository;
import com.google.common.collect.ImmutableSortedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // TODO This method should be flexible enough for new and existing users
    @Override
    public void save(final User user) {
        if (user.getId() == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.addSnapshot(new Snapshot(LocalDate.now(), ImmutableSortedSet.of()));
        }
        userRepository.save(user);
    }

    @Override
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }
}
