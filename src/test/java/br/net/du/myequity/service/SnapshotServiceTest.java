package br.net.du.myequity.service;

import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.CREDIT_CARD_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.INVESTMENT_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_ASSET_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.SIMPLE_LIABILITY_SNAPSHOT;
import static br.net.du.myequity.test.TestConstants.newRecurringDonation;
import static br.net.du.myequity.test.TestConstants.newRecurringIncome;
import static br.net.du.myequity.test.TestConstants.newSingleDonation;
import static br.net.du.myequity.test.TestConstants.newSingleIncome;
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
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.persistence.SnapshotRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Iterator;
import java.util.SortedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SnapshotServiceTest {

    private SnapshotService snapshotService;

    @Mock private SnapshotRepository snapshotRepository;

    @Mock private UserService userService;

    private User user;

    private Snapshot snapshot;

    @BeforeEach
    public void setUp() {
        initMocks(this);

        user = buildUser();

        snapshot = user.getSnapshots().first();

        snapshot.addAccountSnapshot(SIMPLE_ASSET_SNAPSHOT);
        snapshot.addAccountSnapshot(SIMPLE_LIABILITY_SNAPSHOT);
        snapshot.addAccountSnapshot(CREDIT_CARD_SNAPSHOT);
        snapshot.addAccountSnapshot(INVESTMENT_SNAPSHOT);

        snapshot.addTransaction(newRecurringIncome());
        snapshot.addTransaction(newSingleIncome());
        snapshot.addTransaction(newRecurringDonation());
        snapshot.addTransaction(newSingleDonation());

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

        // Only recurring Transactions are copied
        final SortedSet<Transaction> originalTransactions = snapshot.getTransactions();
        final SortedSet<Transaction> newTransactions = newSnapshot.getTransactions();

        assertEquals(4, originalTransactions.size());
        assertEquals(2, newTransactions.size());

        final Iterator<Transaction> iterator = newTransactions.iterator();
        assertTrue(newRecurringDonation().equalsIgnoreId(iterator.next()));
        assertTrue(newRecurringIncome().equalsIgnoreId(iterator.next()));

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
        final Snapshot secondSnapshot = newEmptySnapshot(snapshot.getIndex() + 1);
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
        assertEquals(secondSnapshot, user.getSnapshots().first());

        assertNull(secondSnapshot.getPrevious());
        assertNull(secondSnapshot.getNext());
    }

    @Test
    public void deleteSnapshot_last_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(snapshot.getIndex() + 1);
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
        assertEquals(snapshot, user.getSnapshots().first());

        assertNull(snapshot.getPrevious());
        assertNull(snapshot.getNext());
    }

    @Test
    public void deleteSnapshot_middle_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(snapshot.getIndex() + 1);
        secondSnapshot.setId(99L);
        user.addSnapshot(secondSnapshot);

        final Snapshot thirdSnapshot = newEmptySnapshot(snapshot.getIndex() + 2);
        thirdSnapshot.setId(108L);
        user.addSnapshot(thirdSnapshot);

        final Snapshot fourthSnapshot = newEmptySnapshot(snapshot.getIndex() + 3);
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
        return new Snapshot(snapshotIndex, now, ImmutableSortedSet.of(), ImmutableList.of());
    }
}
