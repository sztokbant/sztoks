package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_ACCOUNT_SUBTYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_FUTURE_TITHING_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_NET_WORTH;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_FOR_ACCOUNT_SUBTYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_FOR_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_LIABILITY;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_TITHING_BALANCE;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SimpleLiabilityAccountBalanceUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final String ACCOUNT_NAME = "Mortgage";
    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("99.00");

    SimpleLiabilityAccountBalanceUpdateControllerTest() {
        super("/snapshot/updateAccountBalance", "108.00");
    }

    @BeforeEach
    public void setUp() {
        account =
                new SimpleLiabilityAccount(
                        ACCOUNT_NAME, CURRENCY_UNIT, LocalDate.now(), CURRENT_BALANCE);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateAccountBalance_simpleLiability_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        when(snapshotService.findByIdAndUserId(SNAPSHOT_ID, user.getId()))
                .thenReturn(Optional.of(snapshot));

        when(accountService.findByIdAndSnapshotId(ACCOUNT_ID, SNAPSHOT_ID))
                .thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        final String resultContentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull(resultContentAsString);

        final JsonNode jsonNode = new ObjectMapper().readTree(resultContentAsString);

        // Only checking fields relevant to the AJAX callback
        assertThat(jsonNode.get(JSON_BALANCE).asText(), is("$108.00"));

        assertThat(
                jsonNode.get(JSON_ACCOUNT_SUBTYPE).asText(),
                is(SimpleLiabilityAccount.ACCOUNT_SUB_TYPE));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$108.00"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.LIABILITY.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_SUBTYPE).asText(), is("$108.00"));

        assertThat(jsonNode.get(JSON_FUTURE_TITHING_BALANCE).asText(), is("null"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("null"));
        assertThat(jsonNode.get(JSON_TOTAL_LIABILITY).asText(), is("null"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$-108.00"));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }
}
