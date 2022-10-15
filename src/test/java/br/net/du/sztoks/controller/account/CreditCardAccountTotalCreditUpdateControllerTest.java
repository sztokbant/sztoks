package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_AVAILABLE_CREDIT;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_CREDIT_CARD_TOTALS_FOR_CURRENCY_UNIT;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_CURRENCY_UNIT;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_NET_WORTH;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_REMAINING_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_STATEMENT;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_CREDIT;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_FOR_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_USED_CREDIT_PERCENTAGE;
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
import br.net.du.sztoks.model.account.CreditCardAccount;
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
class CreditCardAccountTotalCreditUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final BigDecimal CURRENT_AVAILABLE_CREDIT = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_TOTAL_CREDIT = new BigDecimal("3000.00");
    private static final BigDecimal CURRENT_STATEMENT = new BigDecimal("400.00");

    CreditCardAccountTotalCreditUpdateControllerTest() {
        super("/snapshot/updateCreditCardTotalCredit", "7000.00");
    }

    @BeforeEach
    public void setUp() {
        account =
                new CreditCardAccount(
                        "Chase Sapphire Reserve",
                        CURRENCY_UNIT,
                        LocalDate.now(),
                        CURRENT_TOTAL_CREDIT,
                        CURRENT_AVAILABLE_CREDIT,
                        CURRENT_STATEMENT);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateCreditCardTotalCredit_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("-900.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(BigDecimal.ZERO));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("900.00")));

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
        assertThat(jsonNode.get(JSON_TOTAL_CREDIT).asText(), is("$7,000.00"));
        assertThat(jsonNode.get(JSON_USED_CREDIT_PERCENTAGE).asText(), is("70.00%"));
        assertThat(jsonNode.get(JSON_BALANCE).asText(), is("$4,900.00"));
        assertThat(jsonNode.get(JSON_REMAINING_BALANCE).asText(), is("$4,500.00"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.LIABILITY.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$4,900.00"));

        assertThat(jsonNode.get(JSON_CURRENCY_UNIT).asText(), is(CURRENCY_UNIT.toString()));
        final JsonNode creditCardTotalsForCurrencyUnit =
                jsonNode.get(JSON_CREDIT_CARD_TOTALS_FOR_CURRENCY_UNIT);
        assertThat(
                creditCardTotalsForCurrencyUnit.get(JSON_TOTAL_CREDIT).asText(), is("$7,000.00"));
        assertThat(
                creditCardTotalsForCurrencyUnit.get(JSON_AVAILABLE_CREDIT).asText(),
                is("$2,100.00"));
        assertThat(
                creditCardTotalsForCurrencyUnit.get(JSON_USED_CREDIT_PERCENTAGE).asText(),
                is("70.00%"));
        assertThat(creditCardTotalsForCurrencyUnit.get(JSON_STATEMENT).asText(), is("$400.00"));
        assertThat(
                creditCardTotalsForCurrencyUnit.get(JSON_REMAINING_BALANCE).asText(),
                is("$4,500.00"));
        assertThat(creditCardTotalsForCurrencyUnit.get(JSON_BALANCE).asText(), is("$4,900.00"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$-4,900.00"));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("-4900.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(BigDecimal.ZERO));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("4900.00")));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }
}
