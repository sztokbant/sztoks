package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ControllerTestUtils.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.now;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.service.SnapshotService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotNewControllerTest extends PostControllerTestBase {

    private static final long SNAPSHOT_INDEX = 1L;

    private static final String NEW_SNAPSHOT_URL = String.format("/snapshot/%d", SNAPSHOT_ID + 1);
    private static final String URL = "/snapshot/new";

    @MockBean protected SnapshotService snapshotService;

    private Snapshot snapshot;

    public SnapshotNewControllerTest() {
        super(URL);
    }

    @BeforeEach
    public void setUp() {
        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
        snapshot.setId(SNAPSHOT_ID);

        user.addSnapshot(snapshot);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        final Snapshot newSnapshot =
                new Snapshot(
                        snapshot.getIndex() + 1,
                        "New Snapshot",
                        ImmutableSortedSet.of(),
                        ImmutableList.of());
        newSnapshot.setId(snapshot.getId() + 1);
        when(snapshotService.newSnapshot(eq(user))).thenReturn(newSnapshot);
    }

    @Test
    public void post_happy() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        verify(snapshotService).newSnapshot(eq(user));
        verifyRedirect(resultActions, NEW_SNAPSHOT_URL);
    }
}
