package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotTest {

    private LocalDate now;
    private Account assetAccount;
    private AssetSnapshot assetSnapshot;
    private Account liabilityAccount;
    private LiabilitySnapshot liabilitySnapshot;
    private Map<CurrencyUnit, BigDecimal> expectedNetWorth;

    @BeforeEach
    public void setUp() {
        now = LocalDate.now();
        assetAccount = new Account("Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assetSnapshot = new AssetSnapshot(assetAccount, new BigDecimal("100.00"));
        liabilityAccount = new Account("Liability Account", AccountType.LIABILITY, CurrencyUnit.USD);
        liabilitySnapshot = new LiabilitySnapshot(liabilityAccount, new BigDecimal("320000.00"));
        expectedNetWorth = ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-319900.00"));
    }

    @Test
    public void constructor() {
        // WHEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // THEN
        assertEquals(now, snapshot.getDate());
        assertEquals(2, snapshot.getAccountSnapshots().size());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // THEN
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getTotalForAccountType_assets() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // THEN
        assertEquals(ImmutableMap.of(assetAccount.getCurrencyUnit(), new BigDecimal("100.00")),
                     snapshot.getTotalForAccountType(AccountType.ASSET));
    }

    @Test
    public void getTotalForAccountType_liabilities() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // THEN
        assertEquals(ImmutableMap.of(liabilityAccount.getCurrencyUnit(), new BigDecimal("320000.00").negate()),
                     snapshot.getTotalForAccountType(AccountType.LIABILITY));
    }

    @Test
    public void putAccount_addNew() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of());

        // WHEN
        snapshot.addAccountSnapshot(liabilitySnapshot);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshots().size());
        assertEquals(new BigDecimal("320000.00"), snapshot.getAccountSnapshotFor(liabilityAccount).get().getTotal());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("-320000.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // WHEN
        snapshot.removeAccountSnapshotFor(liabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccountSnapshots().size());
        assertFalse(snapshot.getAccountSnapshotFor(liabilityAccount).isPresent());
        assertEquals(ImmutableMap.of(CurrencyUnit.USD, new BigDecimal("100.00")), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // WHEN
        final Account notInSnapshot = new Account("Another Account", AccountType.ASSET, CurrencyUnit.USD);
        snapshot.removeAccountSnapshotFor(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccountSnapshots().size());
        assertEquals(new BigDecimal("320000.00"), snapshot.getAccountSnapshotFor(liabilityAccount).get().getTotal());
        assertEquals(new BigDecimal("100.00"), snapshot.getAccountSnapshotFor(assetAccount).get().getTotal());
        assertFalse(snapshot.getAccountSnapshotFor(notInSnapshot).isPresent());
        assertEquals(expectedNetWorth, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot));

        // THEN
        assertFalse(snapshot.getAccountSnapshotFor(liabilityAccount).isPresent());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        final SortedSet<AccountSnapshot> accountSnapshots = snapshot.getAccountSnapshots();

        // THEN
        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshots.remove(assetSnapshot);
        });

        final Account anotherAccount = new Account("Another Account", AccountType.ASSET, CurrencyUnit.USD);
        final AccountSnapshot notInSnapshot = new AssetSnapshot(anotherAccount, new BigDecimal("50000"));
        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshots.add(notInSnapshot);
        });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot = new Snapshot(now, ImmutableSortedSet.of(assetSnapshot, liabilitySnapshot));

        // WHEN
        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                snapshot.getAccountSnapshotsByType();

        // THEN
        assertEquals(2, accountSnapshotsByType.size());
        assertTrue(accountSnapshotsByType.containsKey(AccountType.ASSET));
        final SortedSet<AccountSnapshot> assetAccounts = accountSnapshotsByType.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertEquals(assetSnapshot, assetAccounts.iterator().next());

        assertTrue(accountSnapshotsByType.containsKey(AccountType.LIABILITY));
        final SortedSet<AccountSnapshot> liabilityAccounts = accountSnapshotsByType.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertEquals(liabilitySnapshot, liabilityAccounts.iterator().next());

        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshotsByType.remove(AccountType.LIABILITY);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            accountSnapshotsByType.get(AccountType.LIABILITY).remove(liabilityAccount);
        });
    }

    @Test
    public void equals() {
        final Snapshot snapshot = new Snapshot(LocalDate.now(), ImmutableSortedSet.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot = new Snapshot(LocalDate.now(), ImmutableSortedSet.of());
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
