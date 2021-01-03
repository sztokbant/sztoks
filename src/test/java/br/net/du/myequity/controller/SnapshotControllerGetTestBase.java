package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.now;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

abstract class SnapshotControllerGetTestBase extends GetControllerTestBase {

    protected static final long SNAPSHOT_ID = 99L;
    protected static final long SNAPSHOT_INDEX = 1L;

    @MockBean protected SnapshotService snapshotService;

    @MockBean protected AccountService accountService;

    protected User anotherUser;

    protected Snapshot snapshot;

    public SnapshotControllerGetTestBase(final String url) {
        super(url);
    }

    @BeforeEach
    public void snapshotControllerGetTestBaseSetUp() {
        anotherUser = buildUser();
        anotherUser.setId(user.getId() * 7);

        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());
        snapshot.setId(SNAPSHOT_ID);
    }

    @Test
    public void get_snapshotNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void get_snapshotDoesNotBelongToUser_redirect() throws Exception {
        // GIVEN
        snapshot.setUser(anotherUser);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        verifyRedirect(resultActions, "/");
    }
}