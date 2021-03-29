package br.net.du.myequity.controller;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    public void post_happy() throws Exception {
        // WHEN
        mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .with(csrf())
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // THEN
        verify(snapshotService).deleteSnapshot(eq(user), eq(snapshot));
    }
}
