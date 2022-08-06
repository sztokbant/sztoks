package br.net.du.sztoks.model;

import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.sztoks.test.TestConstants.EMAIL;
import static br.net.du.sztoks.test.TestConstants.FIFTH_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIFTH_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.FIRST_NAME;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.FOURTH_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.FOURTH_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.LAST_NAME;
import static br.net.du.sztoks.test.TestConstants.SECOND_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.SECOND_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.SEVENTH_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.SEVENTH_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.SIXTH_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.SIXTH_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.THIRD_SNAPSHOT_MONTH;
import static br.net.du.sztoks.test.TestConstants.THIRD_SNAPSHOT_YEAR;
import static br.net.du.sztoks.test.TestConstants.TITHING_PERCENTAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.sztoks.model.account.FutureTithingPolicy;
import br.net.du.sztoks.model.account.SimpleAssetAccount;
import br.net.du.sztoks.model.account.SimpleLiabilityAccount;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private User user;
    private SimpleAssetAccount simpleAssetAccount;
    private SimpleLiabilityAccount simpleLiabilityAccount;
    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        user = new User(EMAIL, FIRST_NAME, LAST_NAME);
        user.setId(1L);

        simpleAssetAccount =
                new SimpleAssetAccount(
                        "Asset Account",
                        CurrencyUnit.USD,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO);
        simpleAssetAccount.setId(99L);

        simpleLiabilityAccount =
                new SimpleLiabilityAccount(
                        "Liability Account", CurrencyUnit.USD, LocalDate.now(), BigDecimal.ZERO);
        simpleLiabilityAccount.setId(7L);

        snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
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
    public void addSnapshot_addNewWithDuplicateName() {
        // GIVEN
        assertTrue(user.getSnapshots().isEmpty());
        user.addSnapshot(snapshot);
        final Snapshot newSnapshot =
                new Snapshot(
                        snapshot.getYear(),
                        snapshot.getMonth(),
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        newSnapshot.setId(snapshot.getId() + 1);

        // WHEN/THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    user.addSnapshot(newSnapshot);
                });
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
    public void compareTo_snapshotsAreOrderedByNameDescending() {
        // GIVEN
        user.addSnapshot(
                new Snapshot(
                        FIFTH_SNAPSHOT_YEAR,
                        FIFTH_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));
        user.addSnapshot(
                new Snapshot(
                        THIRD_SNAPSHOT_YEAR,
                        THIRD_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));
        user.addSnapshot(
                new Snapshot(
                        SIXTH_SNAPSHOT_YEAR,
                        SIXTH_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));
        user.addSnapshot(
                new Snapshot(
                        SEVENTH_SNAPSHOT_YEAR,
                        SEVENTH_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));
        user.addSnapshot(
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));
        user.addSnapshot(
                new Snapshot(
                        SECOND_SNAPSHOT_YEAR,
                        SECOND_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));
        user.addSnapshot(
                new Snapshot(
                        FOURTH_SNAPSHOT_YEAR,
                        FOURTH_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of()));

        // WHEN
        final SortedSet<Snapshot> snapshots = user.getSnapshots();

        // THEN
        assertEquals(7, snapshots.size());

        final Iterator<Snapshot> iterator = snapshots.iterator();
        Snapshot snapshot = iterator.next();
        assertEquals(SEVENTH_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(SEVENTH_SNAPSHOT_MONTH, snapshot.getMonth());
        snapshot = iterator.next();
        assertEquals(SIXTH_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(SIXTH_SNAPSHOT_MONTH, snapshot.getMonth());
        snapshot = iterator.next();
        assertEquals(FIFTH_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(FIFTH_SNAPSHOT_MONTH, snapshot.getMonth());
        snapshot = iterator.next();
        assertEquals(FOURTH_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(FOURTH_SNAPSHOT_MONTH, snapshot.getMonth());
        snapshot = iterator.next();
        assertEquals(THIRD_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(THIRD_SNAPSHOT_MONTH, snapshot.getMonth());
        snapshot = iterator.next();
        assertEquals(SECOND_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(SECOND_SNAPSHOT_MONTH, snapshot.getMonth());
        snapshot = iterator.next();
        assertEquals(FIRST_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(FIRST_SNAPSHOT_MONTH, snapshot.getMonth());
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
