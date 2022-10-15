package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_AMOUNT_INVESTED;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_AVERAGE_PURCHASE_PRICE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_FUTURE_TITHING_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_INVESTMENT_TOTALS;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_NET_WORTH;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_PROFIT_PERCENTAGE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_SHARES;
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
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.InvestmentAccount;
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
class InvestmentAccountSharesUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final BigDecimal CURRENT_CURRENT_SHARE_VALUE = new BigDecimal("4200.00");
    private static final BigDecimal CURRENT_AMOUNT_INVESTED = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_SHARES = new BigDecimal("50.00");

    InvestmentAccountSharesUpdateControllerTest() {
        super("/snapshot/updateAccountShares", "75.00");
    }

    @BeforeEach
    public void setUp() {
        account =
                new InvestmentAccount(
                        "AMZN",
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        CURRENT_SHARES,
                        CURRENT_AMOUNT_INVESTED,
                        CURRENT_CURRENT_SHARE_VALUE);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateAccountShares_futureTithingPolicyNone_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        assertThat(snapshot.getFutureTithingBalance(), is(BigDecimal.ZERO));

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("210000.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("210000.0000")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

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
        assertThat(jsonNode.get(JSON_SHARES).asText(), is(newValue));
        assertThat(jsonNode.get(JSON_AVERAGE_PURCHASE_PRICE).asText(), is("$28.00"));
        assertThat(jsonNode.get(JSON_PROFIT_PERCENTAGE).asText(), is("14900.00%"));
        assertThat(jsonNode.get(JSON_BALANCE).asText(), is("$315,000.00"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.ASSET.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$315,000.00"));

        final JsonNode investmentTotals = jsonNode.get(JSON_INVESTMENT_TOTALS);
        assertThat(investmentTotals.get(JSON_AMOUNT_INVESTED).asText(), is("$2,100.00"));
        assertThat(investmentTotals.get(JSON_PROFIT_PERCENTAGE).asText(), is("14900.00%"));
        assertThat(investmentTotals.get(JSON_BALANCE).asText(), is("$315,000.00"));

        assertThat(jsonNode.get(JSON_FUTURE_TITHING_BALANCE).asText(), is("$0.00"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("$0.00"));
        assertThat(jsonNode.get(JSON_TOTAL_LIABILITY).asText(), is("$0.00"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$315,000.00"));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("315000.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("315000.0000")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }

    @Test
    public void updateAccountShares_futureTithingPolicyProfitsOnly_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        ((InvestmentAccount) account).setFutureTithingPolicy(FutureTithingPolicy.PROFITS_ONLY);
        assertThat(snapshot.getFutureTithingBalance(), is(new BigDecimal("31185.00000000")));

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("178815.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("210000.0000")));
        assertThat(
                snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("31185.00000000")));

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
        assertThat(jsonNode.get(JSON_SHARES).asText(), is(newValue));
        assertThat(jsonNode.get(JSON_AVERAGE_PURCHASE_PRICE).asText(), is("$28.00"));
        assertThat(jsonNode.get(JSON_PROFIT_PERCENTAGE).asText(), is("14900.00%"));
        assertThat(jsonNode.get(JSON_BALANCE).asText(), is("$315,000.00"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.ASSET.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$315,000.00"));

        final JsonNode investmentTotals = jsonNode.get(JSON_INVESTMENT_TOTALS);
        assertThat(investmentTotals.get(JSON_AMOUNT_INVESTED).asText(), is("$2,100.00"));
        assertThat(investmentTotals.get(JSON_PROFIT_PERCENTAGE).asText(), is("14900.00%"));
        assertThat(investmentTotals.get(JSON_BALANCE).asText(), is("$315,000.00"));

        assertThat(jsonNode.get(JSON_FUTURE_TITHING_BALANCE).asText(), is("$46,935.00"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("$46,935.00"));
        assertThat(jsonNode.get(JSON_TOTAL_LIABILITY).asText(), is("$46,935.00"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$268,065.00"));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("268065.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("315000.0000")));
        assertThat(
                snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("46935.00000000")));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }
}
