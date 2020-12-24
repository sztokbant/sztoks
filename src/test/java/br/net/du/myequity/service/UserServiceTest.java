package br.net.du.myequity.service;

import static br.net.du.myequity.test.TestConstants.EMAIL;
import static br.net.du.myequity.test.TestConstants.EXTRA_SPACES;
import static br.net.du.myequity.test.TestConstants.FIRST_NAME;
import static br.net.du.myequity.test.TestConstants.LAST_NAME;
import static br.net.du.myequity.test.TestConstants.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.SnapshotRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired private UserService userService;

    @Autowired private SnapshotRepository snapshotRepository;

    @Test
    public void signUpAndFindByEmail_newUser_idIsSetAndSnapshotsAreInitialized() {
        // GIVEN
        assertNull(userService.findByEmail(EMAIL));

        // WHEN
        userService.signUp(
                EMAIL + EXTRA_SPACES,
                FIRST_NAME + EXTRA_SPACES,
                LAST_NAME + EXTRA_SPACES,
                PASSWORD);

        // THEN
        final User user = userService.findByEmail(EMAIL);
        assertNotNull(user.getId());

        assertEquals(EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());

        final List<Snapshot> snapshots = snapshotRepository.findAllByUser(user);
        assertEquals(1, snapshots.size());
    }

    @Test
    public void findByEmail_nonexistingUser_null() {
        // WHEN
        final User user = userService.findByEmail(EMAIL);

        // THEN
        assertNull(user);
    }
}
