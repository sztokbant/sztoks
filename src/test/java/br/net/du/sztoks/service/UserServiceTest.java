package br.net.du.sztoks.service;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.EMAIL;
import static br.net.du.sztoks.test.TestConstants.EXTRA_SPACES;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME;
import static br.net.du.sztoks.test.TestConstants.PASSWORD;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.SnapshotSummary;
import br.net.du.sztoks.model.User;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired private UserService userService;

    @Autowired private SnapshotService snapshotService;

    @Test
    public void signUpAndFindByEmail_newUser_idIsSetAndSnapshotsAreInitialized() {
        // GIVEN
        assertNull(userService.findByEmail(EMAIL));

        // WHEN
        userService.signUp(
                EMAIL + EXTRA_SPACES,
                FIRST_NAME + EXTRA_SPACES,
                LAST_NAME + EXTRA_SPACES,
                CURRENCY_UNIT,
                TITHING_PERCENTAGE,
                PASSWORD);

        // THEN
        final User user = userService.findByEmail(EMAIL);
        assertNotNull(user.getId());

        assertEquals(EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());

        final List<SnapshotSummary> snapshotSummaries =
                snapshotService.findAllSummariesByUser(user);
        assertEquals(1, snapshotSummaries.size());

        final Snapshot snapshot = snapshotService.findTopByUser(user);
        assertEquals(CURRENCY_UNIT, snapshot.getBaseCurrencyUnit());
        assertThat(snapshot.getDefaultTithingPercentage(), comparesEqualTo(TITHING_PERCENTAGE));
    }

    @Test
    public void findByEmail_nonexistentUser_null() {
        // WHEN
        final User user = userService.findByEmail(EMAIL);

        // THEN
        assertNull(user);
    }
}
