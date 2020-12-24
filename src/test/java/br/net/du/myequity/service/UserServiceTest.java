package br.net.du.myequity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.SnapshotRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class UserServiceTest {

    private static final String EMAIL = "example@example.com";
    private static final String FIRST_NAME = "Bill";
    private static final String LAST_NAME = "Gates";
    private static final String PASSWORD = "password";

    @Autowired private UserService userService;

    @Autowired private SnapshotRepository snapshotRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(EMAIL, FIRST_NAME, LAST_NAME);
        user.setPassword(PASSWORD);
        user.setPasswordConfirm(PASSWORD);
    }

    @Test
    public void saveAndFindByEmail_newUser_idIsSetAndSnapshotsAreInitialized() {
        // GIVEN
        assertNull(user.getId());

        // WHEN
        userService.save(user);

        // THEN
        assertNotNull(user.getId());

        final User actualUser = userService.findByEmail(EMAIL);
        assertNotNull(actualUser.getId());
        assertEquals(user, actualUser);

        final List<Snapshot> snapshots = snapshotRepository.findAllByUser(user);
        assertEquals(1, snapshots.size());
    }

    @Test
    public void findByEmail_nonexistingUser_null() {
        // GIVEN
        assertNull(user.getId());

        // WHEN
        final User actualUser = userService.findByEmail(EMAIL);

        // THEN
        assertNull(actualUser);
    }
}
