package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.NAME_KEY;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.controller.ControllerTestConstants;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.account.SimpleLiabilityAccount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
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
class AccountRenameControllerTest extends AccountAjaxControllerTestBase {

    private static final String NEW_ACCOUNT_NAME_NOT_TRIMMED = "   Wells Fargo Mortgage   ";
    private static final String NEW_ACCOUNT_NAME_TRIMMED = "Wells Fargo Mortgage";

    public AccountRenameControllerTest() {
        super("/snapshot/renameAccount", NEW_ACCOUNT_NAME_NOT_TRIMMED);
    }

    @BeforeEach
    public void setUp() throws Exception {
        account =
                new SimpleLiabilityAccount(
                        ControllerTestConstants.ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        LocalDate.now(),
                        BigDecimal.ZERO);
        account.setId(ACCOUNT_ID);

        final ValueUpdateJsonRequest valueUpdateJsonRequest =
                ValueUpdateJsonRequest.builder()
                        .snapshotId(SNAPSHOT_ID)
                        .entityId(ACCOUNT_ID)
                        .newValue(newValue)
                        .build();
        requestContent = new ObjectMapper().writeValueAsString(valueUpdateJsonRequest);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        final Snapshot snapshot = user.getSnapshots().first();

        snapshot.addAccount(account);

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("0.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(BigDecimal.ZERO));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

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

        // Only checking fields relevant to the AJAX callback
        assertThat(jsonNode.get(NAME_KEY).textValue(), is(NEW_ACCOUNT_NAME_TRIMMED));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("0.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(BigDecimal.ZERO));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

        verify(accountService).save(account);
    }
}
