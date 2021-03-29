package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

abstract class SnapshotControllerPostTestBase extends PostControllerTestBase {

    @MockBean protected SnapshotService snapshotService;

    @MockBean protected AccountService accountService;

    protected User anotherUser;

    protected Snapshot snapshot;

    public SnapshotControllerPostTestBase(final String url) {
        super(url);
    }

    @BeforeEach
    public void snapshotControllerPostTestBaseSetUp() throws Exception {
        anotherUser = buildUser();
        anotherUser.setId(user.getId() * 7);
    }

    @Test
    public void post_snapshotNotFound_redirect() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(snapshot.getId()))).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        verifyRedirect(resultActions, "/");
    }

    @Test
    public void post_snapshotDoesNotBelongToUser_redirect() throws Exception {
        // GIVEN
        snapshot = anotherUser.getSnapshots().first();

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(snapshot.getId()))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        verifyRedirect(resultActions, "/");
    }
}
