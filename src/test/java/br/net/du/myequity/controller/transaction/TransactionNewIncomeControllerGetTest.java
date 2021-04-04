package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.GetControllerTestBase;
import br.net.du.myequity.controller.viewmodel.TransactionViewModelInput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionNewIncomeControllerGetTest extends GetControllerTestBase {

    @MockBean private SnapshotService snapshotService;

    @Mock private Snapshot snapshot;

    public TransactionNewIncomeControllerGetTest() {
        super("/snapshot/42/newIncomeTransaction");
    }

    @BeforeEach
    public void TransactionNewIncomeControllerGetTest() {
        initMocks(this);
    }

    @Test
    public void get_newTransaction_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        when(snapshot.getUser()).thenReturn(user);
        when(snapshot.getBaseCurrencyUnit()).thenReturn(CURRENCY_UNIT);
        when(snapshot.getDefaultTithingPercentage()).thenReturn(TITHING_PERCENTAGE);

        when(snapshotService.findById(eq(42L))).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders.get(url).with(user(user.getEmail())));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        assertEquals(
                "transaction/new_income_transaction", mvcResult.getModelAndView().getViewName());

        final TransactionViewModelInput transactionViewModelInput =
                (TransactionViewModelInput)
                        mvcResult.getModelAndView().getModel().get("transactionForm");
        assertTrue(transactionViewModelInput instanceof TransactionViewModelInput);

        assertNull(transactionViewModelInput.getTypeName());

        assertNull(transactionViewModelInput.getDate());
        assertEquals(CURRENCY_UNIT.toString(), transactionViewModelInput.getCurrencyUnit());
        assertNull(transactionViewModelInput.getAmount());
        assertNull(transactionViewModelInput.getDescription());
        assertNull(transactionViewModelInput.getIsRecurring());

        assertEquals(
                TITHING_PERCENTAGE.toPlainString(),
                transactionViewModelInput.getTithingPercentage());
    }
}
