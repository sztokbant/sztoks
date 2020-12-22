package br.net.du.myequity.controller.accountsnapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
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
class InvestmentSnapshotOriginalShareValueUpdateControllerTest
        extends AccountSnapshotAjaxControllerTestBase {

    private static final BigDecimal CURRENT_CURRENT_SHARE_VALUE = new BigDecimal("4000.00");
    private static final BigDecimal CURRENT_ORIGINAL_SHARE_VALUE = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_SHARES = new BigDecimal("15.00");

    @MockBean private AccountSnapshotRepository accountSnapshotRepository;

    InvestmentSnapshotOriginalShareValueUpdateControllerTest() {
        super("/snapshot/updateInvestmentOriginalShareValue", "2000.00");
    }

    @Override
    public void createEntity() {
        account = new InvestmentAccount("AMZN", CURRENCY_UNIT, LocalDate.now());
        account.setId(ENTITY_ID);
    }

    @Test
    public void updateInvestmentOriginalShareValue_happy() throws Exception {
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

        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ENTITY_ID)).thenReturn(Optional.of(account));

        when(accountSnapshotRepository.findBySnapshotIdAndAccountId(snapshot.getId(), ENTITY_ID))
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

        assertEquals("R$ 2,000.00", jsonNode.get(JSON_ORIGINAL_SHARE_VALUE).asText());

        final String expectedProfitPercentage = "100.00%";
        assertEquals(expectedProfitPercentage, jsonNode.get(JSON_PROFIT_PERCENTAGE).asText());

        final BigDecimal expectedAccountBalance =
                CURRENT_SHARES.multiply(CURRENT_CURRENT_SHARE_VALUE).setScale(2);
        assertEquals("R$ 60,000.00", jsonNode.get(JSON_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(CURRENCY_UNIT.getSymbol(), jsonNode.get(JSON_CURRENCY_UNIT_SYMBOL).asText());
        assertEquals("null", jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals("null", jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals("null", jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
