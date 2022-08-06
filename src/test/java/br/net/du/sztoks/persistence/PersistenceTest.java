package br.net.du.sztoks.persistence;

import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.EMAIL;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME;
import static br.net.du.sztoks.test.TestConstants.PASSWORD;
import static br.net.du.sztoks.test.TestConstants.SECOND_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.SECOND_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.SimpleAssetAccount;
import br.net.du.sztoks.model.account.SimpleLiabilityAccount;
import br.net.du.sztoks.service.AccountService;
import br.net.du.sztoks.service.SnapshotService;
import br.net.du.sztoks.service.UserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;
import javax.transaction.Transactional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class PersistenceTest {
    @Autowired private UserService userService;

    @Autowired private AccountService accountService;

    @Autowired private SnapshotService snapshotService;

    private User user;

    private Snapshot secondSnapshot;

    private SimpleAssetAccount simpleAssetAccount;
    private BigDecimal assetAmount;
    private SimpleLiabilityAccount simpleLiabilityAccount;
    private BigDecimal liabilityAmount;

    @BeforeEach
    public void setUp() {
        assetAmount = new BigDecimal("100.00");
        simpleAssetAccount =
                new SimpleAssetAccount(
                        "Asset Account",
                        CurrencyUnit.USD,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        assetAmount);

        liabilityAmount = new BigDecimal("320000.00");
        simpleLiabilityAccount =
                new SimpleLiabilityAccount(
                        "Liability Account", CurrencyUnit.USD, LocalDate.now(), liabilityAmount);

        final LocalDate now = LocalDate.of(2021, 03, 01);
        try (MockedStatic<LocalDate> localDateStaticMock = mockStatic(LocalDate.class)) {
            localDateStaticMock.when(LocalDate::now).thenReturn(now);
            userService.signUp(
                    EMAIL, FIRST_NAME, LAST_NAME, CURRENCY_UNIT, TITHING_PERCENTAGE, PASSWORD);
        }

        user = userService.findByEmail(EMAIL);

        secondSnapshot =
                new Snapshot(
                        SECOND_SNAPSHOT_YEAR,
                        SECOND_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
    }

    @Test
    public void addSnapshotToPersistedUser() {
        // GIVEN
        assertEquals(1, user.getSnapshots().size());
        assertNull(secondSnapshot.getId());

        // WHEN
        user.addSnapshot(secondSnapshot);

        // THEN
        // UserService's perspective
        final User actualUser = userService.findByEmail(EMAIL);
        assertEquals(user, actualUser);
        assertEquals(2, actualUser.getSnapshots().size());
        assertEquals(secondSnapshot, user.getSnapshots().first());

        // SnapshotService's perspective
        final Snapshot actualSnapshot = snapshotService.findTopByUser(user);
        assertNotNull(actualSnapshot.getId());
        assertEquals(user, actualSnapshot.getUser());
        assertEquals(secondSnapshot, actualSnapshot);
    }

    @Test
    public void addAccountsToPersistedSnapshot() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertNull(simpleAssetAccount.getId());
        assertNull(simpleLiabilityAccount.getId());

        // WHEN
        secondSnapshot.addAccount(simpleAssetAccount);
        secondSnapshot.addAccount(simpleLiabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotService.findTopByUser(user);

        final Map<AccountType, SortedSet<Account>> accountsByType =
                actualSnapshot.getAccountsByType();
        assertEquals(2, accountsByType.size());
        assertEquals(1, accountsByType.get(AccountType.ASSET).size());
        assertEquals(1, accountsByType.get(AccountType.LIABILITY).size());

        assertNotNull(simpleAssetAccount.getId());
        final BigDecimal actualAssetAmount =
                accountsByType.get(AccountType.ASSET).iterator().next().getBalance();
        assertEquals(assetAmount, actualAssetAmount);

        assertNotNull(simpleLiabilityAccount.getId());
        final BigDecimal actualLiabilityAmount =
                accountsByType.get(AccountType.LIABILITY).iterator().next().getBalance();
        assertEquals(liabilityAmount, actualLiabilityAmount);
    }

    @Test
    public void removeAccountFromSnapshot() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertNull(simpleAssetAccount.getId());
        assertNull(simpleLiabilityAccount.getId());

        secondSnapshot.addAccount(simpleAssetAccount);
        secondSnapshot.addAccount(simpleLiabilityAccount);

        // WHEN
        final Snapshot persistedSnapshot = snapshotService.findTopByUser(user);
        assertEquals(2, persistedSnapshot.getAccounts().size());
        persistedSnapshot.removeAccount(simpleLiabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotService.findTopByUser(user);
        final SortedSet<Account> actualAccounts = actualSnapshot.getAccounts();
        assertEquals(1, actualAccounts.size());
        assertTrue(
                equalsIgnoreId(
                        simpleAssetAccount, (SimpleAssetAccount) actualAccounts.iterator().next()));
    }

    @Test
    public void updateAccountInSnapshot() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertNull(simpleAssetAccount.getId());
        assertNull(simpleLiabilityAccount.getId());

        secondSnapshot.addAccount(simpleAssetAccount);
        secondSnapshot.addAccount(simpleLiabilityAccount);

        final Snapshot persistedSnapshot = snapshotService.findTopByUser(user);
        assertEquals(2, persistedSnapshot.getAccounts().size());
        assertEquals(new BigDecimal("-319900.00"), persistedSnapshot.getNetWorth());

        // WHEN
        final SimpleLiabilityAccount persistedSimpleLiabilityAccount =
                (SimpleLiabilityAccount)
                        persistedSnapshot.getAccountById(simpleLiabilityAccount.getId()).get();
        persistedSimpleLiabilityAccount.setBalance(
                persistedSimpleLiabilityAccount.getBalance().add(new BigDecimal("100000.00")));

        // THEN
        final Snapshot actualSnapshot = snapshotService.findTopByUser(user);
        assertEquals(2, actualSnapshot.getAccounts().size());
        assertEquals(new BigDecimal("-419900.00"), actualSnapshot.getNetWorth());
    }

    @Test
    public void removeSnapshotFromPersistedUser() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertEquals(2, user.getSnapshots().size());
        assertEquals(2, snapshotService.findAllSummariesByUser(user).size());

        // WHEN
        final User persistedUser = userService.findByEmail(EMAIL);
        final Snapshot savedSnapshot = persistedUser.getSnapshots().first();
        persistedUser.removeSnapshot(savedSnapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertEquals(1, actualUser.getSnapshots().size());
        assertEquals(1, snapshotService.findAllSummariesByUser(user).size());
    }
}
