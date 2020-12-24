package br.net.du.myequity.controller.accountsnapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.service.AccountSnapshotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class InvestmentSnapshotSharesUpdateControllerTest extends AccountSnapshotAjaxControllerTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.ASSET;
    private static final BigDecimal CURRENT_CURRENT_SHARE_VALUE = new BigDecimal("4200.00");
    private static final BigDecimal CURRENT_ORIGINAL_SHARE_VALUE = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_SHARES = new BigDecimal("50.00");

    @MockBean private AccountSnapshotService accountSnapshotService;

    InvestmentSnapshotSharesUpdateControllerTest() {
        super("/snapshot/updateInvestmentShares", "75.00");
    }

    @Override
    public void createEntity() {
        account = new InvestmentAccount("AMZN", CURRENCY_UNIT, LocalDate.now());
        account.setId(ENTITY_ID);
    }

    @Test
    public void updateInvestmentShares_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        final InvestmentSnapshot investmentSnapshot =
                new InvestmentSnapshot(
                        account,
                        CURRENT_SHARES,
                        CURRENT_ORIGINAL_SHARE_VALUE,
                        CURRENT_CURRENT_SHARE_VALUE);
        snapshot.addAccountSnapshot(investmentSnapshot);

        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountService.findById(ENTITY_ID)).thenReturn(Optional.of(account));

        when(accountSnapshotService.findBySnapshotIdAndAccountId(snapshot.getId(), ENTITY_ID))
                .thenReturn(Optional.of(investmentSnapshot));

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

        assertEquals(newValue, jsonNode.get(JSON_SHARES).asText());

        final String expectedAccountBalance = "R$ 315,000.00";
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(CURRENCY_UNIT.getSymbol(), jsonNode.get(JSON_CURRENCY_UNIT_SYMBOL).asText());
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals(expectedAccountBalance, jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
