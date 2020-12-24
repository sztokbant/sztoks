package br.net.du.myequity.service;

import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class AccountServiceTest {

    private static final Account SIMPLE_ASSET_ACCOUNT =
            new SimpleAssetAccount("Savings", CurrencyUnit.USD, LocalDate.now());
    private static final AccountSnapshot SIMPLE_ASSET_SNAPSHOT =
            new SimpleAssetSnapshot(SIMPLE_ASSET_ACCOUNT, new BigDecimal("10000.00"));

    private static final Account SIMPLE_LIABILITY_ACCOUNT =
            new SimpleLiabilityAccount("Mortgage", CurrencyUnit.USD, LocalDate.now());

    private static final long SNAPSHOT_INDEX = 1L;

    private AccountService accountService;

    @Mock private AccountRepository accountRepository;

    @Mock private AccountSnapshotService accountSnapshotService;

    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());

        accountService = new AccountService(accountRepository, accountSnapshotService);
    }

    @Test
    public void deleteAccount_happy() {
        // GIVEN
        when(accountSnapshotService.findAllByAccount(eq(SIMPLE_ASSET_ACCOUNT)))
                .thenReturn(ImmutableList.of());

        // WHEN
        accountService.deleteAccount(SIMPLE_LIABILITY_ACCOUNT);

        // THEN
        verify(accountRepository).delete(SIMPLE_LIABILITY_ACCOUNT);
    }

    @Test
    public void deleteAccount_accountInUse_error() {
        // GIVEN
        when(accountSnapshotService.findAllByAccount(eq(SIMPLE_ASSET_ACCOUNT)))
                .thenReturn(ImmutableList.of(SIMPLE_ASSET_SNAPSHOT));

        // THEN
        assertThrows(
                MyEquityException.class,
                () -> {
                    accountService.deleteAccount(SIMPLE_ASSET_ACCOUNT);
                });

        verify(accountRepository, never()).delete(SIMPLE_ASSET_ACCOUNT);
    }
}
