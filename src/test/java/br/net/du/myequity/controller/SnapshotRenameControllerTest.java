package br.net.du.myequity.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.viewmodel.EntityRenameJsonRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotRenameControllerTest extends SnapshotControllerAjaxTestBase {

    protected static final String NEW_SNAPSHOT_NAME_NOT_TRIMMED = "   My Best Snapshot   ";
    protected static final String NEW_SNAPSHOT_NAME_TRIMMED = "My Best Snapshot";

    protected static final String JSON_NAME = "name";

    public SnapshotRenameControllerTest() {
        super("/snapshot/updateName");
    }

    @BeforeEach
    public void setUp() throws Exception {
        final EntityRenameJsonRequest entityNameJsonRequest =
                EntityRenameJsonRequest.builder()
                        .id(SNAPSHOT_ID)
                        .newValue(NEW_SNAPSHOT_NAME_NOT_TRIMMED)
                        .build();
        requestContent = new ObjectMapper().writeValueAsString(entityNameJsonRequest);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        final String resultContentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull(resultContentAsString);

        final JsonNode jsonNode = new ObjectMapper().readTree(resultContentAsString);
        assertEquals(NEW_SNAPSHOT_NAME_TRIMMED, jsonNode.get(JSON_NAME).textValue());
    }
}
