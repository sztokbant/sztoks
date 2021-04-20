package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.model.transaction.IncomeCategory;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
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
class IncomeTransactionAmountUpdateControllerTest extends TransactionAjaxControllerTestBase {

    private static final BigDecimal CURRENT_TRANSACTION_AMOUNT = new BigDecimal("0.00");
    private static final BigDecimal TITHING_PERCENTAGE = new BigDecimal("20.00");
    private static final BigDecimal CURRENT_TITHING_ACCOUNT_BALANCE = new BigDecimal("500.00");

    IncomeTransactionAmountUpdateControllerTest() {
        super("/transaction/updateAmount", "108.00");
    }

    @Override
    public void createEntity() {
        transaction =
                new IncomeTransaction(
                        LocalDate.now(),
                        CURRENCY_UNIT.getCode(),
                        CURRENT_TRANSACTION_AMOUNT,
                        "Salary",
                        false,
                        TITHING_PERCENTAGE,
                        IncomeCategory.JOB);
        transaction.setId(TRANSACTION_ID);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);

        final TithingAccount tithingAccount = new TithingAccount(CURRENCY_UNIT);
        snapshot.addAccount(tithingAccount);
        tithingAccount.setBalance(CURRENT_TITHING_ACCOUNT_BALANCE);

        snapshot.addTransaction(transaction);

        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        when(transactionService.findByIdAndSnapshotId(TRANSACTION_ID, SNAPSHOT_ID))
                .thenReturn(Optional.of(transaction));

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
        assertEquals("$108.00", jsonNode.get(JSON_AMOUNT).asText());
        assertEquals("$108.00", jsonNode.get(JSON_TOTAL_FOR_TRANSACTION_TYPE).asText());
        assertEquals("$521.60", jsonNode.get(JSON_TITHING_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals("$521.60", jsonNode.get(JSON_TOTAL_LIABILITY).asText());
        assertEquals("$-521.60", jsonNode.get(JSON_NET_WORTH).asText());
    }
}
