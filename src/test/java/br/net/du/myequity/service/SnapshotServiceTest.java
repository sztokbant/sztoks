package br.net.du.myequity.service;

import static br.net.du.myequity.test.ModelTestUtils.buildUser;
import static br.net.du.myequity.test.TestConstants.FOURTH_SNAPSHOT_NAME;
import static br.net.du.myequity.test.TestConstants.SECOND_SNAPSHOT_NAME;
import static br.net.du.myequity.test.TestConstants.THIRD_SNAPSHOT_NAME;
import static br.net.du.myequity.test.TestConstants.newCreditCardAccount;
import static br.net.du.myequity.test.TestConstants.newInvestmentAccount;
import static br.net.du.myequity.test.TestConstants.newRecurringDonation;
import static br.net.du.myequity.test.TestConstants.newRecurringIncome;
import static br.net.du.myequity.test.TestConstants.newSimpleAssetAccount;
import static br.net.du.myequity.test.TestConstants.newSimpleLiabilityAccount;
import static br.net.du.myequity.test.TestConstants.newSingleDonation;
import static br.net.du.myequity.test.TestConstants.newSingleIncome;
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
import br.net.du.myequity.model.account.Account;
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

        snapshot.addAccount(newSimpleAssetAccount());
        snapshot.addAccount(newSimpleLiabilityAccount());
        snapshot.addAccount(newCreditCardAccount());
        snapshot.addAccount(newInvestmentAccount());

        snapshot.addTransaction(newRecurringIncome());
        snapshot.addTransaction(newSingleIncome());
        snapshot.addTransaction(newRecurringDonation());
        snapshot.addTransaction(newSingleDonation());

        snapshotService = new SnapshotService(snapshotRepository, userService);
    }

    @Test
    public void newSnapshot_happy() {
        // WHEN
        final Snapshot newSnapshot = snapshotService.newSnapshot(user, SECOND_SNAPSHOT_NAME);

        // THEN
        final SortedSet<Account> originalAccounts = snapshot.getAccounts();
        final SortedSet<Account> newAccounts = newSnapshot.getAccounts();
        assertEquals(originalAccounts.size(), newAccounts.size());

        for (final Account originalAccount : originalAccounts) {
            boolean found = false;
            for (final Account newAccount : newAccounts) {
                if (newAccount.getClass().isInstance(originalAccount)) {
                    found = newAccount.equalsIgnoreId(originalAccount);
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

        verify(snapshotRepository).save(newSnapshot);

        assertNull(snapshot.getPrevious());

        assertEquals(newSnapshot, snapshot.getNext());
        assertEquals(snapshot, newSnapshot.getPrevious());
        assertNull(newSnapshot.getNext());
    }

    @Test
    public void newSnapshot_twice_properlySetsNextAndPrevious() {
        // WHEN
        final Snapshot secondSnapshot = snapshotService.newSnapshot(user, SECOND_SNAPSHOT_NAME);
        final Snapshot thirdSnapshot = snapshotService.newSnapshot(user, THIRD_SNAPSHOT_NAME);

        // THEN
        verify(snapshotRepository, times(1)).save(eq(secondSnapshot));
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
        final Snapshot secondSnapshot = newEmptySnapshot(SECOND_SNAPSHOT_NAME);
        secondSnapshot.setId(snapshot.getId() + 1);
        user.addSnapshot(secondSnapshot);

        assertEquals(2, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, snapshot);

        // THEN
        assertEquals(1, user.getSnapshots().size());
        assertEquals(secondSnapshot, user.getSnapshots().first());

        assertNull(secondSnapshot.getPrevious());
        assertNull(secondSnapshot.getNext());
    }

    @Test
    public void deleteSnapshot_last_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(SECOND_SNAPSHOT_NAME);
        secondSnapshot.setId(snapshot.getId() + 1);
        user.addSnapshot(secondSnapshot);

        assertEquals(2, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, secondSnapshot);

        // THEN
        assertEquals(1, user.getSnapshots().size());
        assertEquals(snapshot, user.getSnapshots().first());

        assertNull(snapshot.getPrevious());
        assertNull(snapshot.getNext());
    }

    @Test
    public void deleteSnapshot_middle_happy() {
        // GIVEN
        final Snapshot secondSnapshot = newEmptySnapshot(SECOND_SNAPSHOT_NAME);
        secondSnapshot.setId(snapshot.getId() + 1);
        user.addSnapshot(secondSnapshot);

        final Snapshot thirdSnapshot = newEmptySnapshot(THIRD_SNAPSHOT_NAME);
        thirdSnapshot.setId(secondSnapshot.getId() + 1);
        user.addSnapshot(thirdSnapshot);

        final Snapshot fourthSnapshot = newEmptySnapshot(FOURTH_SNAPSHOT_NAME);
        fourthSnapshot.setId(thirdSnapshot.getId() + 1);
        user.addSnapshot(fourthSnapshot);

        assertEquals(4, user.getSnapshots().size());

        // WHEN
        snapshotService.deleteSnapshot(user, thirdSnapshot);

        // THEN
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

    private Snapshot newEmptySnapshot(final String snapshotName) {
        return new Snapshot(snapshotName, ImmutableSortedSet.of(), ImmutableList.of());
    }
}
