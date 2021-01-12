package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.TestConstants.EMAIL;
import static br.net.du.myequity.test.TestConstants.FIRST_NAME;
import static br.net.du.myequity.test.TestConstants.LAST_NAME;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private static final long SNAPSHOT_INDEX = 1L;

    private User user;
    private SimpleAssetAccount simpleAssetAccount;
    private SimpleLiabilityAccount simpleLiabilityAccount;
    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        user = new User(EMAIL, FIRST_NAME, LAST_NAME);
        user.setId(1L);

        simpleAssetAccount = new SimpleAssetAccount("Asset Account", CurrencyUnit.USD);
        simpleAssetAccount.setId(99L);

        simpleLiabilityAccount = new SimpleLiabilityAccount("Liability Account", CurrencyUnit.USD);
        simpleLiabilityAccount.setId(7L);

        snapshot =
                new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableSortedSet.of());
        snapshot.setId(42L);
    }

    @Test
    public void constructor() {
        // THEN
        assertEquals(EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
    }

    @Test
    public void getFullName() {
        // THEN
        assertEquals("Bill Gates", user.getFullName());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(simpleAssetAccount);
        user.addAccount(simpleLiabilityAccount);
        final Map<AccountType, SortedSet<Account>> accounts = user.getAccounts();

        // THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accounts.remove(AccountType.ASSET);
                });

        final Account newAccount =
                new SimpleAssetAccount("Another Asset Account", CurrencyUnit.USD);
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accounts.get(AccountType.ASSET).add(newAccount);
                });
    }

    @Test
    public void addAccount() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());

        // WHEN
        user.addAccount(simpleLiabilityAccount);

        // THEN
        final Map<AccountType, SortedSet<Account>> accounts = user.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        assertEquals(simpleLiabilityAccount, accounts.get(AccountType.LIABILITY).iterator().next());
        assertEquals(user, simpleLiabilityAccount.getUser());
    }

    @Test
    public void addAccount_addSameTwice() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(simpleLiabilityAccount);

        // WHEN
        user.addAccount(simpleLiabilityAccount);

        // THEN
        final Map<AccountType, SortedSet<Account>> accounts = user.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        assertEquals(simpleLiabilityAccount, accounts.get(AccountType.LIABILITY).iterator().next());
        assertEquals(user, simpleLiabilityAccount.getUser());
    }

    @Test
    public void removeAccount() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(simpleLiabilityAccount);

        // WHEN
        user.removeAccount(simpleLiabilityAccount);

        // THEN
        assertTrue(user.getAccounts().isEmpty());
        assertNull(simpleLiabilityAccount.getUser());
    }

    @Test
    public void removeAccount_removeSameTwice() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(simpleLiabilityAccount);
        user.removeAccount(simpleLiabilityAccount);

        // WHEN
        user.removeAccount(simpleLiabilityAccount);

        // THEN
        assertTrue(user.getAccounts().isEmpty());
        assertNull(simpleLiabilityAccount.getUser());
    }

    @Test
    public void addSnapshot() {
        // GIVEN
        assertTrue(user.getSnapshots().isEmpty());

        // WHEN
        user.addSnapshot(snapshot);

        // THEN
        final Set<Snapshot> snapshots = user.getSnapshots();
        assertEquals(1, snapshots.size());
        assertEquals(snapshot, snapshots.iterator().next());
        assertEquals(user, snapshot.getUser());
    }

    @Test
    public void addSnapshot_addSameTwice() {
        // GIVEN
        assertTrue(user.getSnapshots().isEmpty());
        user.addSnapshot(snapshot);

        // WHEN
        user.addSnapshot(snapshot);

        // THEN
        final Set<Snapshot> snapshots = user.getSnapshots();
        assertEquals(1, snapshots.size());
        assertEquals(snapshot, snapshots.iterator().next());
        assertEquals(user, snapshot.getUser());
    }

    @Test
    public void removeSnapshot() {
        // GIVEN
        assertTrue(user.getSnapshots().isEmpty());
        user.addSnapshot(snapshot);

        // WHEN
        user.removeSnapshot(snapshot);

        // THEN
        final Set<Snapshot> snapshots = user.getSnapshots();
        assertTrue(snapshots.isEmpty());
        assertNull(snapshot.getUser());
    }

    @Test
    public void removeSnapshot_removeSameTwice() {
        // GIVEN
        assertTrue(user.getSnapshots().isEmpty());
        user.addSnapshot(snapshot);
        user.removeSnapshot(snapshot);

        // WHEN
        user.removeSnapshot(snapshot);

        // THEN
        final Set<Snapshot> snapshots = user.getSnapshots();
        assertTrue(snapshots.isEmpty());
        assertNull(snapshot.getUser());
    }

    @Test
    public void compareTo_accountsOrderedByNameAscending() {
        // GIVEN
        user.addAccount(new SimpleAssetAccount("One", CurrencyUnit.USD));
        user.addAccount(new SimpleAssetAccount("Two", CurrencyUnit.USD));
        user.addAccount(new SimpleAssetAccount("Three", CurrencyUnit.USD));
        user.addAccount(new SimpleAssetAccount("Four", CurrencyUnit.USD));
        user.addAccount(new SimpleAssetAccount("Five", CurrencyUnit.USD));
        user.addAccount(new SimpleAssetAccount("Six", CurrencyUnit.USD));
        user.addAccount(new SimpleAssetAccount("Seven", CurrencyUnit.USD));

        // WHEN
        final Iterator<Account> iterator = user.getAccounts().get(AccountType.ASSET).iterator();

        // THEN
        assertEquals("Five", iterator.next().getName());
        assertEquals("Four", iterator.next().getName());
        assertEquals("One", iterator.next().getName());
        assertEquals("Seven", iterator.next().getName());
        assertEquals("Six", iterator.next().getName());
        assertEquals("Three", iterator.next().getName());
        assertEquals("Two", iterator.next().getName());
    }

    @Test
    public void compareTo_snapshotsAreOrderedByIndexDescending() {
        // GIVEN
        user.addSnapshot(new Snapshot(5L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));
        user.addSnapshot(new Snapshot(3L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));
        user.addSnapshot(new Snapshot(6L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));
        user.addSnapshot(new Snapshot(7L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));
        user.addSnapshot(new Snapshot(1L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));
        user.addSnapshot(new Snapshot(2L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));
        user.addSnapshot(new Snapshot(4L, now, ImmutableSortedSet.of(), ImmutableSortedSet.of()));

        // WHEN
        final Iterator<Snapshot> iterator = user.getSnapshots().iterator();

        // THEN
        assertEquals(7L, iterator.next().getIndex());
        assertEquals(6L, iterator.next().getIndex());
        assertEquals(5L, iterator.next().getIndex());
        assertEquals(4L, iterator.next().getIndex());
        assertEquals(3L, iterator.next().getIndex());
        assertEquals(2L, iterator.next().getIndex());
        assertEquals(1L, iterator.next().getIndex());
    }

    @Test
    public void equals() {
        // Itself
        assertTrue(user.equals(user));

        // Not instance of User
        assertFalse(this.user.equals(null));
        assertFalse(this.user.equals("Another type of object"));

        // Same Id null
        final User anotherUser = new User();
        user.setId(null);
        anotherUser.setId(null);
        assertFalse(user.equals(anotherUser));
        assertFalse(anotherUser.equals(user));

        // Same Id not null
        user.setId(42L);
        anotherUser.setId(42L);
        assertTrue(user.equals(anotherUser));
        assertTrue(anotherUser.equals(user));
    }
}
