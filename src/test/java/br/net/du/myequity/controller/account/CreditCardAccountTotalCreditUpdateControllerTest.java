package br.net.du.myequity.controller.account;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.CreditCardAccount;
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
class CreditCardAccountTotalCreditUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.LIABILITY;
    private static final BigDecimal CURRENT_AVAILABLE_CREDIT = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_TOTAL_CREDIT = new BigDecimal("3000.00");
    private static final BigDecimal CURRENT_STATEMENT = new BigDecimal("400.00");

    CreditCardAccountTotalCreditUpdateControllerTest() {
        super("/snapshot/updateCreditCardTotalCredit", "7000.00");
    }

    @Override
    public void createEntity() {
        account =
                new CreditCardAccount(
                        "Chase Sapphire Reserve",
                        CURRENCY_UNIT,
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

        assertEquals("$7,000.00", jsonNode.get(JSON_TOTAL_CREDIT).asText());
        assertEquals("$2,100.00", jsonNode.get(JSON_AVAILABLE_CREDIT).asText());

        final String expectedUsedCreditPercentage = "70.00%";
        assertEquals(
                expectedUsedCreditPercentage, jsonNode.get(JSON_USED_CREDIT_PERCENTAGE).asText());

        final String expectedAccountBalance = "$4,900.00";
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_BALANCE).asText());

        assertEquals("$400.00", jsonNode.get(JSON_STATEMENT).asText());

        assertEquals("$4,500.00", jsonNode.get(JSON_REMAINING_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(CURRENCY_UNIT.getSymbol(), jsonNode.get(JSON_CURRENCY_UNIT_SYMBOL).asText());
        assertEquals("$-4,900.00", jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals("$4,900.00", jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
