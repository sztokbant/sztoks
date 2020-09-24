package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.AjaxControllerTestBase;
import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static br.net.du.myequity.test.TestConstants.now;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class AccountSnapshotAjaxControllerTestBase extends AjaxControllerTestBase {

    static final Long SNAPSHOT_ID = 99L;
    static final long SNAPSHOT_INDEX = 1L;

    static final String JSON_ACCOUNT_TYPE = "accountType";
    static final String JSON_AVAILABLE_CREDIT = "availableCredit";
    static final String JSON_BALANCE = "balance";
    static final String JSON_CURRENCY_UNIT = "currencyUnit";
    static final String JSON_CURRENCY_UNIT_SYMBOL = "currencyUnitSymbol";
    static final String JSON_CURRENT_SHARE_VALUE = "currentShareValue";
    static final String JSON_DUE_DATE = "dueDate";
    static final String JSON_NET_WORTH = "netWorth";
    static final String JSON_ORIGINAL_SHARE_VALUE = "originalShareValue";
    static final String JSON_PROFIT_PERCENTAGE = "profitPercentage";
    static final String JSON_SHARES = "shares";
    static final String JSON_TOTAL_CREDIT = "totalCredit";
    static final String JSON_TOTAL_FOR_ACCOUNT_TYPE = "totalForAccountType";
    static final String JSON_USED_CREDIT_PERCENTAGE = "usedCreditPercentage";

    final String newValue;

    @MockBean
    SnapshotRepository snapshotRepository;

    @MockBean
    AccountRepository accountRepository;

    Snapshot snapshot;

    Account account;

    AccountSnapshotAjaxControllerTestBase(final String url, final String newValue) {
        super(url);
        this.newValue = newValue;
    }

    @BeforeEach
    public void ajaxSnapshotControllerTestBaseSetUp() throws Exception {
        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());
        snapshot.setId(SNAPSHOT_ID);

        final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest =
                AccountSnapshotUpdateJsonRequest.builder()
                                                .snapshotId(SNAPSHOT_ID)
                                                .accountId(ENTITY_ID)
                                                .newValue(newValue)
                                                .build();
        requestContent = new ObjectMapper().writeValueAsString(accountSnapshotUpdateJsonRequest);
    }

    @Test
    public void post_accountNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        when(accountRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void post_accountDoesNotBelongToUser_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        final User anotherUser = new User(user.getEmail(), user.getFirstName(), user.getLastName());
        final Long anotherUserId = user.getId() * 7;
        anotherUser.setId(anotherUserId);

        account.setUser(anotherUser);
        when(accountRepository.findById(ENTITY_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void post_snapshotNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void post_snapshotDoesNotBelongToUser_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        final User anotherUser = new User(user.getEmail(), user.getFirstName(), user.getLastName());
        final Long anotherUserId = user.getId() * 7;
        anotherUser.setId(anotherUserId);

        snapshot.setUser(anotherUser);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    public void post_accountDoesNotBelongInSnapshot_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotRepository.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        account.setUser(user);
        when(accountRepository.findById(ENTITY_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(url)
                                                                              .with(csrf())
                                                                              .with(user(user.getEmail()))
                                                                              .contentType(MediaType.APPLICATION_JSON)
                                                                              .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }
}
