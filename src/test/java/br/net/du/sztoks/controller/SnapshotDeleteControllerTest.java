package br.net.du.sztoks.controller;

import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotDeleteControllerTest extends SnapshotControllerPostTestBase {

    private static final String URL = String.format("/snapshot/delete/%d", SNAPSHOT_ID);

    public SnapshotDeleteControllerTest() {
        super(URL);
    }

    @BeforeEach
    public void setUp() {
        snapshot = user.getSnapshots().first();
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));
    }

    @Test
    public void post_noCsrf_forbidden() throws Exception {
        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        resultActions.andExpect(status().isForbidden());
        verifyNoInteractions(snapshotService);
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
        resultActions.andExpect(status().is3xxRedirection());
        verify(snapshotService).deleteSnapshot(eq(user), eq(snapshot));
    }
}
