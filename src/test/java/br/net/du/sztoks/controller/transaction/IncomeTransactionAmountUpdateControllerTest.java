package br.net.du.sztoks.controller.transaction;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_AMOUNT;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_NET_WORTH;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TITHING_BALANCE;
import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_TOTAL_FOR_TRANSACTION_TYPE;
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
import br.net.du.sztoks.model.transaction.IncomeCategory;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.RecurrencePolicy;
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
class IncomeTransactionAmountUpdateControllerTest extends TransactionAjaxControllerTestBase {

    private static final BigDecimal CURRENT_TRANSACTION_AMOUNT = new BigDecimal("99.00");
    private static final BigDecimal TITHING_PERCENTAGE = new BigDecimal("20.00");

    IncomeTransactionAmountUpdateControllerTest() {
        super("/transaction/updateAmount", "108.00");
    }

    @BeforeEach
    public void setUp() throws Exception {
        transaction =
                new IncomeTransaction(
                        LocalDate.now(),
                        CURRENCY_UNIT.getCode(),
                        CURRENT_TRANSACTION_AMOUNT,
                        "Salary",
                        RecurrencePolicy.NONE,
                        TITHING_PERCENTAGE,
                        IncomeCategory.JOB);
        transaction.setId(TRANSACTION_ID);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addTransaction(transaction);

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("-19.80")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(BigDecimal.ZERO));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("19.80000000")));

        when(snapshotService.findByIdAndUserId(SNAPSHOT_ID, user.getId()))
                .thenReturn(Optional.of(snapshot));

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

        // Only checking fields relevant to the AJAX callback
        assertThat(jsonNode.get(JSON_AMOUNT).asText(), is("$108.00"));
        assertThat(jsonNode.get(JSON_TOTAL_FOR_TRANSACTION_TYPE).asText(), is("$108.00"));

        assertThat(jsonNode.get(JSON_TITHING_BALANCE).asText(), is("$21.60"));
        assertThat(jsonNode.get(JSON_TOTAL_TITHING_BALANCE).asText(), is("$21.60"));
        assertThat(jsonNode.get(JSON_TOTAL_LIABILITY).asText(), is("$21.60"));

        assertThat(jsonNode.get(JSON_NET_WORTH).asText(), is("$-21.60"));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("-21.60")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(BigDecimal.ZERO));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(new BigDecimal("21.60000000")));

        verify(userService).findByEmail(eq(user.getEmail()));
        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
        verify(transactionService).findByIdAndSnapshotId(eq(TRANSACTION_ID), eq(SNAPSHOT_ID));

        verify(transactionService).save(transaction);
        verify(snapshotService).save(snapshot);
    }
}
