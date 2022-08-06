package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.controller.AccountControllerTestBase;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.SimpleLiabilityAccount;
import br.net.du.sztoks.service.SnapshotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AccountRenameControllerTest extends AccountControllerTestBase {

    private static final String JSON_NAME = "name";

    private static final String NEW_ACCOUNT_NAME_NOT_TRIMMED = "   Wells Fargo Mortgage   ";
    private static final String NEW_ACCOUNT_NAME_TRIMMED = "Wells Fargo Mortgage";

    @MockBean private SnapshotService snapshotService;

    public AccountRenameControllerTest() {
        super("/snapshot/renameAccount");
    }

    @BeforeEach
    public void setUp() throws Exception {
        final ValueUpdateJsonRequest valueUpdateJsonRequest =
                ValueUpdateJsonRequest.builder()
                        .snapshotId(SNAPSHOT_ID)
                        .entityId(ACCOUNT_ID)
                        .newValue(NEW_ACCOUNT_NAME_NOT_TRIMMED)
                        .build();
        requestContent = new ObjectMapper().writeValueAsString(valueUpdateJsonRequest);
    }

    @Override
    public void createEntity() {
        account =
                new SimpleLiabilityAccount(
                        ACCOUNT_NAME, CURRENCY_UNIT, LocalDate.now(), BigDecimal.ZERO);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        final Snapshot snapshot = user.getSnapshots().first();

        snapshot.addAccount(account);

        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));
        when(accountService.findByIdAndSnapshotId(ACCOUNT_ID, SNAPSHOT_ID))
                .thenReturn(Optional.of(account));

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
        assertEquals(NEW_ACCOUNT_NAME_TRIMMED, jsonNode.get(JSON_NAME).textValue());
    }
}
