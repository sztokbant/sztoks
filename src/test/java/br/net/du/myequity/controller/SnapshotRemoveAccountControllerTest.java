package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotRemoveAccountControllerTest extends AjaxSnapshotControllerTestBase {

    private static final AccountType ACCOUNT_TYPE = AccountType.LIABILITY;
    private static final BigDecimal CURRENT_BALANCE = new BigDecimal("99.00");

    @MockBean
    private AccountSnapshotRepository accountSnapshotRepository;

    SnapshotRemoveAccountControllerTest() {
        super("/removeAccountFromSnapshot", null);
    }

    @BeforeEach
    public void setUp() throws Exception {
        final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest =
                SnapshotAccountUpdateJsonRequest.builder().snapshotId(SNAPSHOT_ID).accountId(ACCOUNT_ID).build();
        requestContent = new ObjectMapper().writeValueAsString(snapshotAccountUpdateJsonRequest);
    }

    @Override
    public void createAccount() {
        account = new SimpleLiabilityAccount("Mortgage", CURRENCY_UNIT, LocalDate.now());
        account.setId(ACCOUNT_ID);
    }

    @Test
    public void removeAccountFromSnapshot_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        final SimpleLiabilitySnapshot simpleLiabilitySnapshot = new SimpleLiabilitySnapshot(account, CURRENT_BALANCE);
        simpleLiabilitySnapshot.setId(108L);
        snapshot.addAccountSnapshot(simpleLiabilitySnapshot);

        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(accountSnapshotRepository.findBySnapshotIdAndAccountId(snapshot.getId(),
                                                                    ACCOUNT_ID)).thenReturn(Optional.of(
                simpleLiabilitySnapshot));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().isOk());

        final MvcResult mvcResult = resultActions.andReturn();
        final String resultContentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull(resultContentAsString);

        final JsonNode jsonNode = new ObjectMapper().readTree(resultContentAsString);
        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(CURRENCY_UNIT.getSymbol(), jsonNode.get(JSON_CURRENCY_UNIT_SYMBOL).asText());
        assertEquals("0.00", jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals("0.00", jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
