package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.sztoks.controller.AjaxControllerTestBase;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.FutureTithingAccount;
import br.net.du.sztoks.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

abstract class AccountAjaxControllerTestBase extends AjaxControllerTestBase {

    static final Long ACCOUNT_ID = 1L;

    static final BigDecimal CURRENT_FUTURE_TITHING_ACCOUNT_REFERENCE_AMOUNT =
            new BigDecimal("500.00");
    static final Long FUTURE_TITHING_ACCOUNT_ID = 1008L;

    final String newValue;

    @MockBean AccountService accountService;

    Account account;

    AccountAjaxControllerTestBase(final String url, final String newValue) {
        super(url);
        this.newValue = newValue;
    }

    @BeforeEach
    public void ajaxSnapshotControllerTestBaseSetUp() throws Exception {
        final ValueUpdateJsonRequest valueUpdateJsonRequest =
                ValueUpdateJsonRequest.builder()
                        .snapshotId(SNAPSHOT_ID)
                        .entityId(ACCOUNT_ID)
                        .newValue(newValue)
                        .build();
        requestContent = new ObjectMapper().writeValueAsString(valueUpdateJsonRequest);
    }

    @Test
    public void post_accountNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        when(accountService.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
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
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        final User anotherUser = new User(user.getEmail(), user.getFirstName(), user.getLastName());
        final Long anotherUserId = user.getId() * 7;
        anotherUser.setId(anotherUserId);

        when(accountService.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
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
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        when(accountService.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        // WHEN
        final ResultActions resultActions =
                mvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .with(csrf())
                                .with(user(user.getEmail()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent));

        // THEN
        resultActions.andExpect(status().is4xxClientError());
    }

    /**
     * This method should only be used to populate a FutureTithingAccount in test Snapshots that do
     * not already contain a FutureTithingAccount, e.g. when all Snapshot accounts are liabilities
     * or assets with FutureTithingPolicy.NONE.
     */
    protected FutureTithingAccount prepareFutureTithingAccount() {
        final FutureTithingAccount futureTithingAccount =
                new FutureTithingAccount(CURRENCY_UNIT, LocalDate.now(), BigDecimal.ZERO);

        snapshot.addAccount(futureTithingAccount);
        futureTithingAccount.setReferenceAmount(CURRENT_FUTURE_TITHING_ACCOUNT_REFERENCE_AMOUNT);
        futureTithingAccount.setId(FUTURE_TITHING_ACCOUNT_ID);

        return futureTithingAccount;
    }
}
