package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.test.ModelTestUtils.SNAPSHOT_ID;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.net.du.myequity.controller.SnapshotControllerAjaxTestBase;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

abstract class TransactionAjaxControllerTestBase extends SnapshotControllerAjaxTestBase {

    static final Long TRANSACTION_ID = 1L;

    static final BigDecimal CURRENT_TITHING_ACCOUNT_BALANCE = new BigDecimal("500.00");
    static final Long TITHING_ACCOUNT_ID = 108L;

    static final String JSON_AMOUNT = "amount";
    static final String JSON_CURRENCY_UNIT = "currencyUnit";
    static final String JSON_NET_WORTH = "netWorth";
    static final String JSON_TITHING_BALANCE = "tithingBalance";
    static final String JSON_TOTAL_FOR_TRANSACTION_TYPE = "totalForTransactionType";
    static final String JSON_TOTAL_LIABILITY = "totalLiability";

    final String newValue;

    @MockBean TransactionService transactionService;

    @MockBean AccountService accountService;

    Transaction transaction;

    TransactionAjaxControllerTestBase(final String url, final String newValue) {
        super(url);
        this.newValue = newValue;
    }

    @BeforeEach
    public void ajaxSnapshotControllerTestBaseSetUp() throws Exception {
        snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        snapshot.setId(SNAPSHOT_ID);

        final ValueUpdateJsonRequest valueUpdateJsonRequest =
                ValueUpdateJsonRequest.builder()
                        .snapshotId(SNAPSHOT_ID)
                        .entityId(TRANSACTION_ID)
                        .newValue(newValue)
                        .build();
        requestContent = new ObjectMapper().writeValueAsString(valueUpdateJsonRequest);
    }

    @Test
    public void post_transactionNotFound_clientError() throws Exception {
        // GIVEN
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        snapshot.setUser(user);
        when(snapshotService.findById(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

        when(transactionService.findByIdAndSnapshotId(TRANSACTION_ID, SNAPSHOT_ID))
                .thenReturn(Optional.empty());

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

    protected TithingAccount prepareTithingAccount() {
        final TithingAccount tithingAccount =
                new TithingAccount(CURRENCY_UNIT, LocalDate.now(), BigDecimal.ZERO);

        snapshot.addAccount(tithingAccount);
        tithingAccount.setBalance(CURRENT_TITHING_ACCOUNT_BALANCE);
        tithingAccount.setId(TITHING_ACCOUNT_ID);

        return tithingAccount;
    }
}
