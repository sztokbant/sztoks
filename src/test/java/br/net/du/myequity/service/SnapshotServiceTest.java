package br.net.du.myequity.service;

import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.BEGGAR_DONATION;
import static br.net.du.myequity.test.TestConstants.CHARITY_DONATION;
import static br.net.du.myequity.test.TestConstants.CREDIT_CARD_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.INVESTMENT_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.SALARY_INCOME;
import static br.net.du.myequity.test.TestConstants.SIDE_GIG_INCOME;
import static br.net.du.myequity.test.TestConstants.SIMPLE_ASSET_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_LIABILITY_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.transaction.Donation;
import br.net.du.myequity.model.transaction.Income;
import br.net.du.myequity.persistence.SnapshotRepository;
import com.google.common.collect.ImmutableSortedSet;
import java.util.SortedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SnapshotServiceTest {

    private static final long SNAPSHOT_INDEX = 1L;

    private SnapshotService snapshotService;

    @Mock private SnapshotRepository snapshotRepository;

    @Mock private UserService userService;

    private User user;

    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        user = buildUser();

        snapshot = newEmptySnapshot(SNAPSHOT_INDEX);
        snapshot.setId(108L);
        user.addSnapshot(snapshot);

        snapshot.addAccountSnapshot(SIMPLE_ASSET_SNAPSHOT);
        snapshot.addAccountSnapshot(SIMPLE_LIABILITY_SNAPSHOT);
        snapshot.addAccountSnapshot(CREDIT_CARD_SNAPSHOT);
        snapshot.addAccountSnapshot(INVESTMENT_SNAPSHOT);

        snapshot.addIncome(SALARY_INCOME);
        snapshot.addIncome(SIDE_GIG_INCOME);

        snapshot.addDonation(CHARITY_DONATION);
        snapshot.addDonation(BEGGAR_DONATION);

        snapshotService = new SnapshotService(snapshotRepository, userService);
    }

    @Test
    public void newSnapshot_happy() {
        // WHEN
        final Snapshot newSnapshot = snapshotService.newSnapshot(user);

        // THEN
        final SortedSet<AccountSnapshot> originalAccountSnapshots = snapshot.getAccountSnapshots();
        final SortedSet<AccountSnapshot> newAccountSnapshots = newSnapshot.getAccountSnapshots();
        assertEquals(originalAccountSnapshots.size(), newAccountSnapshots.size());

        for (final AccountSnapshot originalAccountSnapshot : originalAccountSnapshots) {
            boolean found = false;
            for (final AccountSnapshot newAccountSnapshot : newAccountSnapshots) {
                if (newAccountSnapshot.getClass().isInstance(originalAccountSnapshot)) {
                    found = newAccountSnapshot.equalsIgnoreId(originalAccountSnapshot);
                    break;
                }
            }
            assertTrue(found);
        }

        // Only recurring Incomes and Donations are copied
        final SortedSet<Income> originalIncomes = snapshot.getIncomes();
        final SortedSet<Income> newIncomes = newSnapshot.getIncomes();

        assertEquals(2, originalIncomes.size());
        assertEquals(1, newIncomes.size());

        assertTrue(SALARY_INCOME.equalsIgnoreId(newIncomes.iterator().next()));

        final SortedSet<Donation> originalDonations = snapshot.getDonations();
        final SortedSet<Donation> newDonations = newSnapshot.getDonations();

        assertTrue(CHARITY_DONATION.equalsIgnoreId(newDonations.iterator().next()));

        assertEquals(2, originalDonations.size());
        assertEquals(1, newDonations.size());

        verify(snapshotRepository).save(snapshot);
        verify(snapshotRepository).save(newSnapshot);

        assertNull(snapshot.getPrevious());

        assertEquals(newSnapshot, snapshot.getNext());
        assertEquals(snapshot, newSnapshot.getPrevious());
        assertNull(newSnapshot.getNext());
    }

    @Test
    public void newSnapshot_twice_properlySetsNextAndPrevious() {
        // WHEN
        final Snapshot secondSnapshot = snapshotService.newSnapshot(user);
        final Snapshot thirdSnapshot = snapshotService.newSnapshot(user);

        // THEN
        verify(snapshotRepository, times(1)).save(eq(snapshot));
        verify(snapshotRepository, times(2)).save(eq(secondSnapshot));
        verify(snapshotRepository, times(1)).save(eq(thirdSnapshot));

        assertNull(snapshot.getPrevious());
        assertEquals(secondSnapshot, snapshot.getNext());

        assertEquals(snapshot, secondSnapshot.getPrevious());
        assertEquals(thirdSnapshot, secondSnapshot.getNext());

        assertEquals(secondSnapshot, thirdSnapshot.getPrevious());
        assertNull(thirdSnapshot.getNext());
    }

    @Test
    public void deleteSnapshot_first_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(SNAPSHOT_INDEX + 1);
        secondSnapshot.setId(99L);
        secondSnapshot.setPrevious(snapshot);
        snapshot.setNext(secondSnapshot);
        user.addSnapshot(secondSnapshot);

        assertEquals(2, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, snapshot);

        // THEN
        verify(snapshotRepository).save(eq(secondSnapshot));

        assertEquals(1, user.getSnapshots().size());
        assertEquals(secondSnapshot, user.getSnapshots().iterator().next());

        assertNull(secondSnapshot.getPrevious());
        assertNull(secondSnapshot.getNext());
    }

    @Test
    public void deleteSnapshot_last_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(SNAPSHOT_INDEX + 1);
        secondSnapshot.setId(99L);
        secondSnapshot.setPrevious(snapshot);
        snapshot.setNext(secondSnapshot);
        user.addSnapshot(secondSnapshot);

        assertEquals(2, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, secondSnapshot);

        // THEN
        verify(snapshotRepository).save(eq(snapshot));

        assertEquals(1, user.getSnapshots().size());
        assertEquals(snapshot, user.getSnapshots().iterator().next());

        assertNull(snapshot.getPrevious());
        assertNull(snapshot.getNext());
    }

    @Test
    public void deleteSnapshot_middle_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(SNAPSHOT_INDEX + 1);
        secondSnapshot.setId(99L);
        user.addSnapshot(secondSnapshot);

        final Snapshot thirdSnapshot = newEmptySnapshot(SNAPSHOT_INDEX + 2);
        thirdSnapshot.setId(108L);
        user.addSnapshot(thirdSnapshot);

        final Snapshot fourthSnapshot = newEmptySnapshot(SNAPSHOT_INDEX + 3);
        fourthSnapshot.setId(144L);
        user.addSnapshot(fourthSnapshot);

        snapshot.setNext(secondSnapshot);
        secondSnapshot.setPrevious(snapshot);
        secondSnapshot.setNext(thirdSnapshot);
        thirdSnapshot.setPrevious(secondSnapshot);
        thirdSnapshot.setNext(fourthSnapshot);
        fourthSnapshot.setPrevious(thirdSnapshot);

        assertEquals(4, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, thirdSnapshot);

        // THEN
        verify(snapshotRepository).save(eq(secondSnapshot));
        verify(snapshotRepository).save(eq(fourthSnapshot));

        assertEquals(3, user.getSnapshots().size());
        assertEquals(fourthSnapshot, secondSnapshot.getNext());
        assertEquals(secondSnapshot, fourthSnapshot.getPrevious());
    }

    @Test
    public void deleteSnapshot_onlyRemainingSnapshot() {
        // GIVEN
        assertEquals(1, user.getSnapshots().size());

        // THEN
        assertThrows(
                MyEquityException.class,
                () -> {
                    snapshotService.deleteSnapshot(user, snapshot);
                });

        assertEquals(1, user.getSnapshots().size());
    }

    private Snapshot newEmptySnapshot(final long snapshotIndex) {
        return new Snapshot(
                snapshotIndex,
                now,
                ImmutableSortedSet.of(),
                ImmutableSortedSet.of(),
                ImmutableSortedSet.of());
    }
}
