package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_ACCOUNT_TYPE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_AMOUNT_INVESTED;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_CURRENT_SHARE_VALUE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_FUTURE_TITHING_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_INVESTMENT_TOTALS;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_NET_WORTH;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_PROFIT_PERCENTAGE;
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
class InvestmentAccountCurrentShareValueUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final BigDecimal CURRENT_CURRENT_SHARE_VALUE = new BigDecimal("123.00");
    private static final BigDecimal CURRENT_AMOUNT_INVESTED = new BigDecimal("4696.97");
    private static final BigDecimal CURRENT_SHARES = new BigDecimal("36.00");

    InvestmentAccountCurrentShareValueUpdateControllerTest() {
        super("/snapshot/updateAccountCurrentShareValue", "180.99");
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
    public void updateAccountCurrentShareValue_futureTithingPolicyNone_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        assertThat(snapshot.getFutureTithingBalance(), is(BigDecimal.ZERO));

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("4428.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("4428.0000")));
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
        assertThat(jsonNode.get(JSON_CURRENT_SHARE_VALUE).asText(), is("$180.99"));
        assertThat(jsonNode.get(JSON_PROFIT_PERCENTAGE).asText(), is("38.72%"));
        assertThat(jsonNode.get(JSON_BALANCE).asText(), is("$6,515.64"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.ASSET.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$6,515.64"));

        final JsonNode investmentTotals = jsonNode.get(JSON_INVESTMENT_TOTALS);
        assertThat(investmentTotals.get(JSON_AMOUNT_INVESTED).asText(), is("$4,696.97"));
        assertThat(investmentTotals.get(JSON_PROFIT_PERCENTAGE).asText(), is("38.72%"));
        assertThat(investmentTotals.get(JSON_BALANCE).asText(), is("$6,515.64"));

        assertThat(jsonNode.get(JSON_FUTURE_TITHING_BALANCE).asText(), is("$0.00"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("$0.00"));
        assertThat(jsonNode.get(JSON_TOTAL_LIABILITY).asText(), is("$0.00"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$6,515.64"));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("6515.64")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("6515.6400")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }

    @Test
    public void updateAccountCurrentShareValue_futureTithingPolicyProfitsOnly_happy()
            throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        ((InvestmentAccount) account).setFutureTithingPolicy(FutureTithingPolicy.PROFITS_ONLY);
        assertThat(snapshot.getFutureTithingBalance(), is(new BigDecimal("-40.34550000")));

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("4468.35")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("4428.0000")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("-40.34550000")));

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
        assertThat(jsonNode.get(JSON_CURRENT_SHARE_VALUE).asText(), is("$180.99"));
        assertThat(jsonNode.get(JSON_PROFIT_PERCENTAGE).asText(), is("38.72%"));
        assertThat(jsonNode.get(JSON_BALANCE).asText(), is("$6,515.64"));

        assertThat(jsonNode.get(JSON_ACCOUNT_TYPE).asText(), is(AccountType.ASSET.toString()));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText(), is("$6,515.64"));

        final JsonNode investmentTotals = jsonNode.get(JSON_INVESTMENT_TOTALS);
        assertThat(investmentTotals.get(JSON_AMOUNT_INVESTED).asText(), is("$4,696.97"));
        assertThat(investmentTotals.get(JSON_PROFIT_PERCENTAGE).asText(), is("38.72%"));
        assertThat(investmentTotals.get(JSON_BALANCE).asText(), is("$6,515.64"));

        assertThat(jsonNode.get(JSON_FUTURE_TITHING_BALANCE).asText(), is("$272.80"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("$272.80"));
        assertThat(jsonNode.get(JSON_TOTAL_LIABILITY).asText(), is("$272.80"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$6,242.84"));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("6242.84")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("6515.6400")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("272.80050000")));

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }
}
