package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User user;
    private Account assetAccount;
    private Account liabilityAccount;
    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        user = new User("example@example.com", "Bill", "Gates");
        user.setId(1L);

        assetAccount = new Account("Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assetAccount.setId(99L);

        liabilityAccount = new Account("Liability Account", AccountType.LIABILITY, CurrencyUnit.USD);
        liabilityAccount.setId(7L);

        snapshot = new Snapshot(LocalDate.now(), ImmutableMap.of());
        snapshot.setId(42L);
    }

    @Test
    public void constructor() {
        // THEN
        assertEquals("example@example.com", user.getEmail());
        assertEquals("Bill", user.getFirstName());
        assertEquals("Gates", user.getLastName());
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
        user.addAccount(assetAccount);
        user.addAccount(liabilityAccount);
        final Map<AccountType, List<Account>> accounts = user.getAccounts();

        // THEN
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.remove(AccountType.ASSET);
        });

        final Account newAccount = new Account("Another Asset Account", AccountType.ASSET, CurrencyUnit.USD);
        assertThrows(UnsupportedOperationException.class, () -> {
            accounts.get(AccountType.ASSET).add(newAccount);
        });
    }

    @Test
    public void addAccount() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());

        // WHEN
        user.addAccount(liabilityAccount);

        // THEN
        final Map<AccountType, List<Account>> accounts = user.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        assertEquals(liabilityAccount, accounts.get(AccountType.LIABILITY).get(0));
        assertEquals(user, liabilityAccount.getUser());
    }

    @Test
    public void addAccount_addSameTwice() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(liabilityAccount);

        // WHEN
        user.addAccount(liabilityAccount);

        // THEN
        final Map<AccountType, List<Account>> accounts = user.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(AccountType.LIABILITY).size());
        assertEquals(liabilityAccount, accounts.get(AccountType.LIABILITY).get(0));
        assertEquals(user, liabilityAccount.getUser());
    }

    @Test
    public void removeAccount() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(liabilityAccount);

        // WHEN
        user.removeAccount(liabilityAccount);

        // THEN
        assertTrue(user.getAccounts().isEmpty());
        assertNull(liabilityAccount.getUser());
    }

    @Test
    public void removeAccount_removeSameTwice() {
        // GIVEN
        assertTrue(user.getAccounts().isEmpty());
        user.addAccount(liabilityAccount);
        user.removeAccount(liabilityAccount);

        // WHEN
        user.removeAccount(liabilityAccount);

        // THEN
        assertTrue(user.getAccounts().isEmpty());
        assertNull(liabilityAccount.getUser());
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