package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreditCardSnapshotTotalCreditUpdateControllerTest extends AccountSnapshotAjaxControllerTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.LIABILITY;
    private static final BigDecimal CURRENT_AVAILABLE_CREDIT = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_TOTAL_CREDIT = new BigDecimal("3000.00");

    @MockBean
    private AccountSnapshotRepository accountSnapshotRepository;

    CreditCardSnapshotTotalCreditUpdateControllerTest() {
        super("/snapshot/updateCreditCardTotalCredit", "7000.00");
    }

    @Override
    public void createEntity() {
        account = new CreditCardAccount("Chase Sapphire Reserve", CURRENCY_UNIT, LocalDate.now());
        account.setId(ENTITY_ID);
    }

    @Test
    public void updateCreditCardTotalCredit_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        final CreditCardSnapshot creditCardSnapshot =
                new CreditCardSnapshot(account, CURRENT_TOTAL_CREDIT, CURRENT_AVAILABLE_CREDIT);
        snapshot.addAccountSnapshot(creditCardSnapshot);

        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ENTITY_ID)).thenReturn(Optional.of(account));

        when(accountSnapshotRepository.findBySnapshotIdAndAccountId(snapshot.getId(),
                                                                    ENTITY_ID)).thenReturn(Optional.of(
                creditCardSnapshot));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(SecurityMockMvcRequestPostProcessors
                                                                                            .user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        final String resultContentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull(resultContentAsString);

        final JsonNode jsonNode = new ObjectMapper().readTree(resultContentAsString);

        assertEquals(newValue, jsonNode.get(JSON_TOTAL_CREDIT).asText());

        final String expectedCreditAvailablePercentage = "70.00%";
        assertEquals(expectedCreditAvailablePercentage, jsonNode.get(JSON_USED_CREDIT_PERCENTAGE).asText());

        final BigDecimal expectedAccountBalance = new BigDecimal(newValue).subtract(CURRENT_AVAILABLE_CREDIT);
        assertEquals(expectedAccountBalance.toString(), jsonNode.get(JSON_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(CURRENCY_UNIT.getSymbol(), jsonNode.get(JSON_CURRENCY_UNIT_SYMBOL).asText());
        assertEquals(expectedAccountBalance.negate().toString(), jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals(expectedAccountBalance.negate().toString(), jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
