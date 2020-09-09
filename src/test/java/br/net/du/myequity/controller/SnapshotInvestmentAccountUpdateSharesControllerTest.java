package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static br.net.du.myequity.test.ControllerTestUtil.verifyRedirect;
import static br.net.du.myequity.test.ModelTestUtil.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotInvestmentAccountUpdateSharesControllerTest {

    private static final String UPDATE_INVESTMENT_SHARES_URL = "/updateInvestmentShares";

    private static final Long SNAPSHOT_ID = 99L;

    private static final Long ACCOUNT_ID = 1L;
    private static final AccountType ACCOUNT_TYPE = AccountType.ASSET;
    private static final CurrencyUnit CURRENCY_UNIT = CurrencyUnit.of("BRL");

    private static final BigDecimal CURRENT_SHARES = new BigDecimal("50.00");
    private static final BigDecimal NEW_SHARES = new BigDecimal("75.00");

    private static final BigDecimal CURRENT_ORIGINAL_SHARE_VALUE = new BigDecimal("2100.00");
    private static final BigDecimal CURRENT_CURRENT_SHARE_VALUE = new BigDecimal("4200.00");

    private static final String JSON_ACCOUNT_TYPE = "accountType";
    private static final String JSON_BALANCE = "balance";
    private static final String JSON_CURRENCY_UNIT = "currencyUnit";
    private static final String JSON_NET_WORTH = "netWorth";
    private static final String JSON_SHARES = "shares";
    private static final String JSON_TOTAL_FOR_ACCOUNT_TYPE = "totalForAccountType";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SnapshotRepository snapshotRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private AccountSnapshotRepository accountSnapshotRepository;

    private String requestContent;

    private User user;

    private Snapshot snapshot;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        user = buildUser();

        // WHEN
        snapshot = new Snapshot(LocalDate.now(), ImmutableSortedSet.of());
        snapshot.setId(SNAPSHOT_ID);

        account = new InvestmentAccount("AMZN", CURRENCY_UNIT, LocalDate.now());
        account.setId(ACCOUNT_ID);

        final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest =
                SnapshotAccountUpdateJsonRequest.builder()
                                                .snapshotId(SNAPSHOT_ID)
                                                .accountId(ACCOUNT_ID)
                                                .newValue(NEW_SHARES)
                                                .build();
        requestContent = new ObjectMapper().writeValueAsString(snapshotAccountUpdateJsonRequest);
    }

    @Test
    public void updateInvestmentShares_noCsrfToken_forbidden() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void updateInvestmentShares_withCsrfTokenUserNotLoggedIn_redirectToLogin() throws Exception {
        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        verifyRedirect(resultActions, "/login");
    }

    @Test
    public void updateInvestmentShares_userNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(null);

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInvestmentShares_snapshotNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInvestmentShares_snapshotDoesNotBelongToUser_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        final User anotherUser = new User(user.getEmail(), user.getFirstName(), user.getLastName());
        final Long anotherUserId = user.getId() * 7;
        anotherUser.setId(anotherUserId);

        snapshot.setUser(anotherUser);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInvestmentShares_accountNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInvestmentShares_accountDoesNotBelongToUser_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        final User anotherUser = new User(user.getEmail(), user.getFirstName(), user.getLastName());
        final Long anotherUserId = user.getId() * 7;
        anotherUser.setId(anotherUserId);

        account.setUser(anotherUser);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInvestmentShares_accountDoesNotBelongInSnapshot_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInvestmentShares_happy() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        final InvestmentSnapshot investmentSnapshot = new InvestmentSnapshot(account,
                                                                             CURRENT_SHARES,
                                                                             CURRENT_ORIGINAL_SHARE_VALUE,
                                                                             CURRENT_CURRENT_SHARE_VALUE);
        investmentSnapshot.setId(108L);
        snapshot.addAccountSnapshot(investmentSnapshot);

        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        when(accountSnapshotRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(investmentSnapshot));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(UPDATE_INVESTMENT_SHARES_URL)
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

        assertEquals(NEW_SHARES.toString(), jsonNode.get(JSON_SHARES).asText());

        final BigDecimal expectedAccountBalance = NEW_SHARES.multiply(CURRENT_CURRENT_SHARE_VALUE).setScale(2);
        assertEquals(expectedAccountBalance.toString(), jsonNode.get(JSON_BALANCE).asText());

        assertEquals(CURRENCY_UNIT.toString(), jsonNode.get(JSON_CURRENCY_UNIT).asText());
        assertEquals(expectedAccountBalance.toString(), jsonNode.get(JSON_NET_WORTH).asText());
        assertEquals(ACCOUNT_TYPE.toString(), jsonNode.get(JSON_ACCOUNT_TYPE).asText());
        assertEquals(expectedAccountBalance.toString(), jsonNode.get(JSON_TOTAL_FOR_ACCOUNT_TYPE).asText());
    }
}
