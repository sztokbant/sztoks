package br.net.du.myequity.model;

import static br.net.du.myequity.test.TestConstants.EMAIL;
import static br.net.du.myequity.test.TestConstants.FIRST_NAME;
import static br.net.du.myequity.test.TestConstants.LAST_NAME;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Iterator;
import java.util.Set;
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

        snapshot = new Snapshot(SNAPSHOT_INDEX, now, ImmutableSortedSet.of(), ImmutableList.of());
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
    public void compareTo_snapshotsAreOrderedByIndexDescending() {
        // GIVEN
        user.addSnapshot(new Snapshot(5L, now, ImmutableSortedSet.of(), ImmutableList.of()));
        user.addSnapshot(new Snapshot(3L, now, ImmutableSortedSet.of(), ImmutableList.of()));
        user.addSnapshot(new Snapshot(6L, now, ImmutableSortedSet.of(), ImmutableList.of()));
        user.addSnapshot(new Snapshot(7L, now, ImmutableSortedSet.of(), ImmutableList.of()));
        user.addSnapshot(new Snapshot(1L, now, ImmutableSortedSet.of(), ImmutableList.of()));
        user.addSnapshot(new Snapshot(2L, now, ImmutableSortedSet.of(), ImmutableList.of()));
        user.addSnapshot(new Snapshot(4L, now, ImmutableSortedSet.of(), ImmutableList.of()));

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
