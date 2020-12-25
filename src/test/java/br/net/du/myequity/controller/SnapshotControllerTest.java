package br.net.du.myequity.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotControllerTest extends SnapshotControllerGetTestBase {

    public SnapshotControllerTest() {
        super(String.format("/snapshot/%d", SNAPSHOT_ID));
    }

    @Test
    public void get_happy() throws Exception {
        // GIVEN
        snapshot.setUser(user);

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotService.findById(eq(SNAPSHOT_ID))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals("snapshot", mvcResult.getModelAndView().getViewName());
        assertEquals(
                UserViewModelOutput.of(user), mvcResult.getModelAndView().getModel().get("user"));

        assertEquals(
                SnapshotViewModelOutput.of(snapshot),
                mvcResult.getModelAndView().getModel().get("snapshot"));
    }
}
