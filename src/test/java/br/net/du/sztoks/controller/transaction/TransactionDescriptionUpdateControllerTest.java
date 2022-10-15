package br.net.du.sztoks.controller.transaction;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_DESCRIPTION;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class TransactionDescriptionUpdateControllerTest extends TransactionAjaxControllerTestBase {

    private static final String ORIGINAL_DESCRIPTION = "BTC (TODO)";
    private static final String NEW_DESCRIPTION = "BTC (1.87654321)";

    TransactionDescriptionUpdateControllerTest() {
        super("/transaction/updateDescription", NEW_DESCRIPTION);
    }

    @BeforeEach
    public void setUp() throws Exception {
        transaction =
                new IncomeTransaction(
                        LocalDate.now(),
                        CURRENCY_UNIT.getCode(),
                        new BigDecimal("0.00"),
                        ORIGINAL_DESCRIPTION,
                        RecurrencePolicy.NONE,
                        new BigDecimal("20.00"),
                        IncomeCategory.JOB);
        transaction.setId(TRANSACTION_ID);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
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

        // Only checking fields relevant to the AJAX callback
        assertThat(jsonNode.get(JSON_DESCRIPTION).textValue(), is(NEW_DESCRIPTION));

        verify(transactionService).save(transaction);
    }
}
