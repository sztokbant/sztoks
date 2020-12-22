package br.net.du.myequity.model;

import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SnapshotTest {

    private static final long SNAPSHOT_INDEX = 1L;

    private SimpleAssetAccount simpleAssetAccount;
    private SimpleAssetSnapshot simpleAssetSnapshot;
    private SimpleLiabilityAccount simpleLiabilityAccount;
    private SimpleLiabilitySnapshot simpleLiabilitySnapshot;
    private Map<CurrencyUnit, BigDecimal> expectedNetWorth;

    @BeforeEach
    public void setUp() {
        simpleAssetAccount = new SimpleAssetAccount("Asset Account", CurrencyUnit.USD);
        simpleAssetSnapshot = new SimpleAssetSnapshot(simpleAssetAccount, new BigDecimal("100.00"));
        simpleLiabilityAccount = new SimpleLiabilityAccount("Liability Account", CurrencyUnit.USD);
        simpleLiabilitySnapshot =
                new SimpleLiabilitySnapshot(simpleLiabilityAccount, new BigDecimal("320000.00"));
        expectedNetWorth = ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00"));
    }

    @Test
    public void constructor() {
        // WHEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));
        snapshot.setId(42L);

        // THEN
        assertEquals(SNAPSHOT_INDEX, snapshot.getIndex());
        assertEquals(now, snapshot.getName());

        assertNull(simpleAssetSnapshot.getSnapshot());
        assertNull(simpleLiabilitySnapshot.getSnapshot());

        assertEquals(2, snapshot.getAccountSnapshots().size());
        for (final AccountSnapshot accountSnapshot : snapshot.getAccountSnapshots()) {
            assertEquals(snapshot, accountSnapshot.getSnapshot());
        }

        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        // THEN
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getTotalForAccountType_assets() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        // THEN
        assertEquals(
                ImmutableMap.of(simpleAssetAccount.getCurrencyUnit(), new BigDecimal("100.00")),
                snapshot.getTotalForAccountType(AccountType.ASSET));
    }

    @Test
    public void getTotalForAccountType_liabilities() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        // THEN
        assertEquals(
                ImmutableMap.of(
                        simpleLiabilityAccount.getCurrencyUnit(),
                        new BigDecimal("320000.00").negate()),
                snapshot.getTotalForAccountType(AccountType.LIABILITY));
    }

    @Test
    public void putAccount_addNew() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());

        // WHEN
        snapshot.addAccountSnapshot(simpleLiabilitySnapshot);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshots().size());
        assertEquals(
                new BigDecimal("320000.00"),
                snapshot.getAccountSnapshotFor(simpleLiabilityAccount).get().getTotal());
        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-320000.00")),
                snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        // WHEN
        snapshot.removeAccountSnapshotFor(simpleLiabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshots().size());
        assertFalse(snapshot.getAccountSnapshotFor(simpleLiabilityAccount).isPresent());
        assertEquals(
                ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("100.00")),
                snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        // WHEN
        final Account notInSnapshot = new SimpleAssetAccount("Another Account", CurrencyUnit.USD);
        snapshot.removeAccountSnapshotFor(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccountSnapshots().size());
        assertEquals(
                new BigDecimal("320000.00"),
                snapshot.getAccountSnapshotFor(simpleLiabilityAccount).get().getTotal());
        assertEquals(
                new BigDecimal("100.00"),
                snapshot.getAccountSnapshotFor(simpleAssetAccount).get().getTotal());
        assertFalse(snapshot.getAccountSnapshotFor(notInSnapshot).isPresent());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(simpleAssetSnapshot));

        // THEN
        assertFalse(snapshot.getAccountSnapshotFor(simpleLiabilityAccount).isPresent());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();

        // THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshots.remove(simpleAssetSnapshot);
                });

        final Account anotherAccount = new SimpleAssetAccount("Another Account", CurrencyUnit.USD);
        final AccountSnapshot notInSnapshot =
                new SimpleAssetSnapshot(anotherAccount, new BigDecimal("50000"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshots.add(notInSnapshot);
                });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        SNAPSHOT_INDEX,
                        now,
                        ImmutableSortedSet.of(simpleAssetSnapshot, simpleLiabilitySnapshot));

        // WHEN
        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                snapshot.getAccountSnapshotsByType();

        // THEN
        assertEquals(2, accountSnapshotsByType.size());
        assertTrue(accountSnapshotsByType.containsKey(AccountType.ASSET));
        final SortedSet<AccountSnapshot> assetAccounts =
                accountSnapshotsByType.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertTrue(simpleAssetSnapshot.equalsIgnoreId(assetAccounts.iterator().next()));

        assertTrue(accountSnapshotsByType.containsKey(AccountType.LIABILITY));
        final SortedSet<AccountSnapshot> liabilityAccounts =
                accountSnapshotsByType.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertTrue(simpleLiabilitySnapshot.equalsIgnoreId(liabilityAccounts.iterator().next()));

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshotsByType.remove(AccountType.LIABILITY);
                });

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountSnapshotsByType
                            .get(AccountType.LIABILITY)
                            .remove(simpleLiabilityAccount);
                });
    }

    @Test
    public void equals() {
        final Snapshot snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of());
        snapshot.setId(null);
        anotherSnapshot.setId(null);
        assertFalse(snapshot.equals(anotherSnapshot));
        assertFalse(anotherSnapshot.equals(snapshot));

        // Same Id not null
        snapshot.setId(42L);
        anotherSnapshot.setId(42L);
        assertTrue(snapshot.equals(anotherSnapshot));
        assertTrue(anotherSnapshot.equals(snapshot));
    }
}
