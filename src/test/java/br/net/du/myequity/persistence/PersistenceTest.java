package br.net.du.myequity.persistence;

import static br.net.du.myequity.test.TestConstants.EMAIL;
import static br.net.du.myequity.test.TestConstants.FIRST_NAME;
import static br.net.du.myequity.test.TestConstants.LAST_NAME;
import static br.net.du.myequity.test.TestConstants.PASSWORD;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.service.AccountSnapshotService;
import br.net.du.myequity.service.SnapshotService;
import br.net.du.myequity.service.UserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedSet;
import javax.transaction.Transactional;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class PersistenceTest {
    @Autowired private UserService userService;

    @Autowired private AccountSnapshotService accountService;

    @Autowired private SnapshotService snapshotService;

    private User user;

    private Snapshot secondSnapshot;

    private SimpleAssetSnapshot simpleAssetAccount;
    private BigDecimal assetAmount;
    private SimpleLiabilitySnapshot simpleLiabilityAccount;
    private BigDecimal liabilityAmount;

    @BeforeEach
    public void setUp() {
        assetAmount = new BigDecimal("100.00");
        simpleAssetAccount =
                new SimpleAssetSnapshot("Asset Account", CurrencyUnit.USD, assetAmount);
        liabilityAmount = new BigDecimal("320000.00");
        simpleLiabilityAccount =
                new SimpleLiabilitySnapshot("Liability Account", CurrencyUnit.USD, liabilityAmount);

        userService.signUp(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD);
        user = userService.findByEmail(EMAIL);

        secondSnapshot = new Snapshot(2L, now, ImmutableSortedSet.of(), ImmutableList.of());
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
        final Snapshot actualSnapshot = snapshotService.findAllByUser(user).get(1);
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
        secondSnapshot.addAccountSnapshot(simpleAssetAccount);
        secondSnapshot.addAccountSnapshot(simpleLiabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotService.findAllByUser(user).get(1);

        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                actualSnapshot.getAccountSnapshotsByType();
        assertEquals(2, accountSnapshotsByType.size());
        assertEquals(1, accountSnapshotsByType.get(AccountType.ASSET).size());
        assertEquals(1, accountSnapshotsByType.get(AccountType.LIABILITY).size());

        assertNotNull(simpleAssetAccount.getId());
        final BigDecimal actualAssetAmount =
                accountSnapshotsByType.get(AccountType.ASSET).iterator().next().getTotal();
        assertEquals(assetAmount, actualAssetAmount);

        assertNotNull(simpleLiabilityAccount.getId());
        final BigDecimal actualLiabilityAmount =
                accountSnapshotsByType.get(AccountType.LIABILITY).iterator().next().getTotal();
        assertEquals(liabilityAmount, actualLiabilityAmount);
    }

    @Test
    public void removeAccountFromSnapshot() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertNull(simpleAssetAccount.getId());
        assertNull(simpleLiabilityAccount.getId());

        secondSnapshot.addAccountSnapshot(simpleAssetAccount);
        secondSnapshot.addAccountSnapshot(simpleLiabilityAccount);

        // WHEN
        final Snapshot persistedSnapshot = snapshotService.findAllByUser(user).get(1);
        assertEquals(2, persistedSnapshot.getAccountSnapshots().size());
        persistedSnapshot.removeAccountSnapshot(simpleLiabilityAccount);

        // THEN
        final Snapshot actualSnapshot = snapshotService.findAllByUser(user).get(1);
        final SortedSet<AccountSnapshot> actualAccountSnapshots =
                actualSnapshot.getAccountSnapshots();
        assertEquals(1, actualAccountSnapshots.size());
        assertTrue(simpleAssetAccount.equalsIgnoreId(actualAccountSnapshots.iterator().next()));
    }

    @Test
    public void updateAccountInSnapshot() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertNull(simpleAssetAccount.getId());
        assertNull(simpleLiabilityAccount.getId());

        secondSnapshot.addAccountSnapshot(simpleAssetAccount);
        secondSnapshot.addAccountSnapshot(simpleLiabilityAccount);

        final Snapshot persistedSnapshot = snapshotService.findAllByUser(user).get(1);
        assertEquals(2, persistedSnapshot.getAccountSnapshots().size());
        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00")),
                persistedSnapshot.getNetWorth());

        // WHEN
        final SimpleLiabilitySnapshot persistedSimpleLiabilityAccount =
                (SimpleLiabilitySnapshot)
                        persistedSnapshot
                                .getAccountSnapshotById(simpleLiabilityAccount.getId())
                                .get();
        persistedSimpleLiabilityAccount.setAmount(
                persistedSimpleLiabilityAccount.getAmount().add(new BigDecimal("100000.00")));

        // THEN
        final Snapshot actualSnapshot = snapshotService.findAllByUser(user).get(1);
        assertEquals(2, actualSnapshot.getAccountSnapshots().size());
        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-419900.00")),
                actualSnapshot.getNetWorth());
    }

    @Test
    public void removeSnapshotFromPersistedUser() {
        // GIVEN
        user.addSnapshot(secondSnapshot);
        assertEquals(2, user.getSnapshots().size());
        assertEquals(2, snapshotService.findAllByUser(user).size());

        // WHEN
        final User persistedUser = userService.findByEmail(EMAIL);
        final Snapshot savedSnapshot = persistedUser.getSnapshots().first();
        persistedUser.removeSnapshot(savedSnapshot);

        // THEN
        final User actualUser = userService.findByEmail(EMAIL);
        assertEquals(1, actualUser.getSnapshots().size());
        assertEquals(1, snapshotService.findAllByUser(user).size());
    }
}
