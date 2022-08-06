package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.model.account.FutureTithingAccount;
import br.net.du.sztoks.model.account.PayableAccount;
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
class PayableAccountDueDateUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("4200.00");
    private static final LocalDate CURRENT_DUE_DATE = LocalDate.parse("2020-12-31");

    PayableAccountDueDateUpdateControllerTest() {
        super("/snapshot/updateAccountDueDate", "2020-09-16");
    }

    @Override
    public void createEntity() {
        account =
                new PayableAccount(
                        "Friend",
                        CURRENCY_UNIT,
                        LocalDate.now(),
                        CURRENT_DUE_DATE,
                        CURRENT_BALANCE,
                        false);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateAccountDueDate_payableAccount_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        final FutureTithingAccount futureTithingAccount = prepareFutureTithingAccount();

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

        assertEquals(newValue, jsonNode.get(JSON_DUE_DATE).asText());

        verify(snapshotService).findById(eq(SNAPSHOT_ID));
        verify(accountService, times(0)).save(futureTithingAccount);
    }
}
