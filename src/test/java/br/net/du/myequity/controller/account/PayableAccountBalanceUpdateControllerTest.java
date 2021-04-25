package br.net.du.myequity.controller.account;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.PayableAccount;
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
class PayableAccountBalanceUpdateControllerTest extends AccountAjaxControllerTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.LIABILITY;
    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("99.00");

    PayableAccountBalanceUpdateControllerTest() {
        super("/snapshot/updateAccountBalance", "108.00");
    }

    @Override
    public void createEntity() {
        account =
                new PayableAccount(
                        "Friend", CURRENCY_UNIT, LocalDate.now(), LocalDate.now(), CURRENT_BALANCE);
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void updateAccountBalance_payableAccount_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        snapshot.addAccount(account);

        when(snapshotService.findByIdAndUserId(SNAPSHOT_ID, user.getId()))
                .thenReturn(Optional.of(snapshot));

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
        assertEquals("$108.00", jsonNode.get(JSON_BALANCE).asText());
        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals("$-108.00", jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals("$108.00", jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());

        verify(snapshotService).findByIdAndUserId(eq(SNAPSHOT_ID), eq(user.getId()));
    }
}
