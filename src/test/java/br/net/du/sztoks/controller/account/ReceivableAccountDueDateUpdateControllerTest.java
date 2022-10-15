package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.ControllerTestConstants.JSON_DUE_DATE;
import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.account.FutureTithingAccount;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.ReceivableAccount;
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
class ReceivableAccountDueDateUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("4200.00");
    private static final LocalDate CURRENT_DUE_DATE = LocalDate.parse("2020-12-31");

    ReceivableAccountDueDateUpdateControllerTest() {
        super("/snapshot/updateAccountDueDate", "2020-09-16");
    }

    @BeforeEach
    public void setUp() {
        account =
                new ReceivableAccount(
                        "Friend",
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        CURRENT_DUE_DATE,
                        CURRENT_BALANCE,
                        false);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateAccountDueDate_receivableAccount_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        // Sanity checks (before)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("4200.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("4200.00")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

        final FutureTithingAccount futureTithingAccount = initializeEmptyFutureTithingAccount();

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

        // Only checking fields relevant to the AJAX callback
        assertThat(jsonNode.get(JSON_DUE_DATE).asText(), is(newValue));

        // Sanity checks (after)
        assertThat(snapshot.getNetWorth(), is(new BigDecimal("4200.00")));
        assertThat(snapshot.getTotalFor(AccountType.ASSET), is(new BigDecimal("4200.00")));
        assertThat(snapshot.getTotalFor(AccountType.LIABILITY), is(BigDecimal.ZERO));

        verify(snapshotService).findById(eq(SNAPSHOT_ID));
        verify(accountService, times(0)).save(futureTithingAccount);
    }
}
