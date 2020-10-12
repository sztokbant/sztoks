package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.account.PayableAccount;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
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
class PayableSnapshotAmountUpdateControllerTest extends AccountSnapshotAjaxControllerTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.LIABILITY;
    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("99.00");

    @MockBean
    private AccountSnapshotRepository accountSnapshotRepository;

    PayableSnapshotAmountUpdateControllerTest() {
        super("/snapshot/updateAccountBalance", "108.00");
    }

    @Override
    public void createEntity() {
        account = new PayableAccount("Friend", CURRENCY_UNIT, LocalDate.now());
        account.setId(ENTITY_ID);
    }

    @Test
    public void post_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        final PayableSnapshot accountSnapshot = new PayableSnapshot(account, LocalDate.now(), CURRENT_BALANCE);
        snapshot.addAccountSnapshot(accountSnapshot);

        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ENTITY_ID)).thenReturn(Optional.of(account));

        when(accountSnapshotRepository.findBySnapshotIdAndAccountId(snapshot.getId(),
                                                                    ENTITY_ID)).thenReturn(Optional.of(accountSnapshot));

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
        assertEquals("R$ 108.00", jsonNode.get(JSON_BALANCE).asText());
        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals("R$ -108.00", jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals("R$ -108.00", jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
