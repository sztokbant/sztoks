package br.net.du.myequity.controller.account;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.InvestmentAccount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Optional;
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

    private static final AccountType ACCOUNT_TYPE = AccountType.ASSET;
    private static final BigDecimal CURRENT_CURRENT_SHARE_VALUE = new BigDecimal("123.00");
    private static final BigDecimal CURRENT_AMOUNT_INVESTED = new BigDecimal("4696.97");
    private static final BigDecimal CURRENT_SHARES = new BigDecimal("36.00");

    InvestmentAccountCurrentShareValueUpdateControllerTest() {
        super("/snapshot/updateInvestmentCurrentShareValue", "180.99");
    }

    @Override
    public void createEntity() {
        account =
                new InvestmentAccount(
                        "AMZN",
                        CURRENCY_UNIT,
                        CURRENT_SHARES,
                        CURRENT_AMOUNT_INVESTED,
                        CURRENT_CURRENT_SHARE_VALUE);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateInvestmentAmountInvested_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

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

        assertEquals("$180.99", jsonNode.get(JSON_CURRENT_SHARE_VALUE).asText());
        assertEquals("$130.47", jsonNode.get(JSON_AVERAGE_PURCHASE_PRICE).asText());

        final String expectedProfitPercentage = "38.72%";
        assertEquals(expectedProfitPercentage, jsonNode.get(JSON_PROFIT_PERCENTAGE).asText());

        final String expectedAccountBalance = "$6,515.64";
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(CURRENCY_UNIT.getSymbol(), jsonNode.get(JSON_CURRENCY_UNIT_SYMBOL).asText());
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
