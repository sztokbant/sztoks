package br.net.du.myequity.model;

import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreIdAndDate;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE;
import static br.net.du.myequity.test.TestConstants.newRecurringDonation;
import static br.net.du.myequity.test.TestConstants.newRecurringIncome;
import static br.net.du.myequity.test.TestConstants.newRecurringInvestment;
import static br.net.du.myequity.test.TestConstants.newSimpleAssetAccount;
import static br.net.du.myequity.test.TestConstants.newSimpleLiabilityAccount;
import static br.net.du.myequity.test.TestConstants.newSingleDonation;
import static br.net.du.myequity.test.TestConstants.newSingleIncome;
import static br.net.du.myequity.test.TestConstants.newSingleInvestment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.FutureTithingPolicy;
import br.net.du.myequity.model.account.SimpleAssetAccount;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SnapshotTest {

    private BigDecimal EXPECTED_NET_WORTH = new BigDecimal("7500.00");

    private BigDecimal EXPECTED_NET_WORTH_WITH_TRANSACTIONS = new BigDecimal("4878.00");

    private SimpleLiabilityAccount simpleLiabilityAccount;
    private SimpleAssetAccount simpleAssetAccount;

    @BeforeEach
    public void setUp() {
        simpleLiabilityAccount = newSimpleLiabilityAccount();
        simpleAssetAccount = newSimpleAssetAccount();
    }

    @Test
    public void constructor() {
        // WHEN
        final IncomeTransaction recurringIncome = newRecurringIncome();
        final IncomeTransaction singleIncome = newSingleIncome();
        final DonationTransaction recurringDonation = newRecurringDonation();
        final DonationTransaction singleDonation = newSingleDonation();

        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(
                                recurringIncome, singleIncome, recurringDonation, singleDonation),
                        ImmutableMap.of());
        snapshot.setId(42L);

        // THEN
        assertEquals(FIRST_SNAPSHOT_YEAR, snapshot.getYear());
        assertEquals(FIRST_SNAPSHOT_MONTH, snapshot.getMonth());

        assertEquals(CURRENCY_UNIT, snapshot.getBaseCurrencyUnit());
        assertTrue(TITHING_PERCENTAGE.compareTo(snapshot.getDefaultTithingPercentage()) == 0);

        assertNull(simpleAssetAccount.getSnapshot());
        assertNull(simpleLiabilityAccount.getSnapshot());

        assertEquals(3, snapshot.getAccounts().size());
        for (final Account account : snapshot.getAccounts()) {
            assertEquals(snapshot, account.getSnapshot());
        }

        assertEquals(EXPECTED_NET_WORTH_WITH_TRANSACTIONS, snapshot.getNetWorth());

        assertNull(recurringIncome.getSnapshot());
        assertNull(singleIncome.getSnapshot());
        assertNull(recurringDonation.getSnapshot());
        assertNull(singleDonation.getSnapshot());

        assertEquals(4, snapshot.getTransactions().size());
        for (final Transaction transaction : snapshot.getTransactions()) {
            assertEquals(snapshot, transaction.getSnapshot());
            assertEquals(2021, transaction.getDate().getYear());
            assertEquals(3, transaction.getDate().getMonthValue());
        }
    }

    @Test
    public void getNetWorth() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // THEN
        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());
    }

    @Test
    public void getTotalFor_assets() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // THEN
        assertEquals(new BigDecimal("10000.00"), snapshot.getTotalFor(AccountType.ASSET));
    }

    @Test
    public void getTotalFor_liabilities() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // THEN
        assertEquals(new BigDecimal("2500.00"), snapshot.getTotalFor(AccountType.LIABILITY));
    }

    @Test
    public void addAccount_addNew() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // WHEN
        snapshot.addAccount(simpleLiabilityAccount);

        // THEN
        final SortedSet<Account> accounts = snapshot.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals(simpleLiabilityAccount, accounts.iterator().next());
        assertEquals(snapshot, simpleLiabilityAccount.getSnapshot());

        assertEquals(new BigDecimal("-2500.00"), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleLiabilityAccount, simpleAssetAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // WHEN
        snapshot.removeAccount(simpleLiabilityAccount);

        // THEN
        assertEquals(1, snapshot.getAccounts().size());

        final SortedSet<Account> accounts = snapshot.getAccounts();
        assertTrue(
                equalsIgnoreId(
                        simpleAssetAccount, (SimpleAssetAccount) accounts.iterator().next()));

        assertEquals(new BigDecimal("10000.00"), snapshot.getNetWorth());
    }

    @Test
    public void removeAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // WHEN
        final Account notInSnapshot =
                new SimpleAssetAccount(
                        "Another Account", CurrencyUnit.USD, FutureTithingPolicy.NONE);
        snapshot.removeAccount(notInSnapshot);

        // THEN
        assertEquals(2, snapshot.getAccounts().size());
        assertEquals(EXPECTED_NET_WORTH, snapshot.getNetWorth());
    }

    @Test
    public void getAccount_existing() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        simpleAssetAccount.setId(42L);
        snapshot.addAccount(simpleAssetAccount);

        // THEN
        assertTrue(snapshot.getAccountById(42L).isPresent());
    }

    @Test
    public void getAccount_nonexisting() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        simpleAssetAccount.setId(42L);
        snapshot.addAccount(simpleAssetAccount);

        // THEN
        assertFalse(snapshot.getAccountById(99L).isPresent());
    }

    @Test
    public void getAccounts_containersAreImmutable() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        final SortedSet<Account> accounts = snapshot.getAccounts();

        // THEN
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accounts.remove(simpleAssetAccount);
                });

        final Account notInSnapshot =
                new SimpleAssetAccount(
                        "Another Account",
                        CurrencyUnit.USD,
                        FutureTithingPolicy.NONE,
                        new BigDecimal("50000"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accounts.add(notInSnapshot);
                });
    }

    @Test
    public void getAccountsByType() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(simpleAssetAccount, simpleLiabilityAccount),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // WHEN
        final Map<AccountType, SortedSet<Account>> accountsByType = snapshot.getAccountsByType();

        // THEN
        assertEquals(2, accountsByType.size());
        assertTrue(accountsByType.containsKey(AccountType.ASSET));
        final SortedSet<Account> assetAccounts = accountsByType.get(AccountType.ASSET);
        assertEquals(1, assetAccounts.size());
        assertTrue(
                equalsIgnoreId(
                        simpleAssetAccount, (SimpleAssetAccount) assetAccounts.iterator().next()));

        assertTrue(accountsByType.containsKey(AccountType.LIABILITY));
        final SortedSet<Account> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);
        assertEquals(1, liabilityAccounts.size());
        assertTrue(
                equalsIgnoreId(
                        simpleLiabilityAccount,
                        (SimpleLiabilityAccount) liabilityAccounts.iterator().next()));

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountsByType.remove(AccountType.LIABILITY);
                });

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    accountsByType.get(AccountType.LIABILITY).remove(simpleLiabilityAccount);
                });
    }

    @Test
    public void addTransaction_addRecurring() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        final IncomeTransaction transaction = newRecurringIncome();

        // WHEN
        snapshot.addTransaction(transaction);

        // THEN
        final SortedSet<Transaction> transactions = snapshot.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.iterator().next());
        assertEquals(snapshot, transaction.getSnapshot());

        final List<Transaction> recurringTransactions = snapshot.getRecurringTransactions();
        assertEquals(1, recurringTransactions.size());
        assertEquals(transaction, recurringTransactions.get(0));
    }

    @Test
    public void addTransaction_addNonRecurring() {
        // GIVEN
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
        final IncomeTransaction transaction = newSingleIncome();

        // WHEN
        snapshot.addTransaction(transaction);

        // THEN
        final SortedSet<Transaction> transactions = snapshot.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.iterator().next());
        assertEquals(snapshot, transaction.getSnapshot());

        final List<Transaction> recurringTransactions = snapshot.getRecurringTransactions();
        assertTrue(recurringTransactions.isEmpty());
    }

    @Test
    public void removeTransaction_existing() {
        // GIVEN
        final IncomeTransaction recurringIncome = newRecurringIncome(42L);
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        snapshot.addTransaction(recurringIncome);
        snapshot.addTransaction(newSingleIncome());
        snapshot.addTransaction(newRecurringDonation());
        snapshot.addTransaction(newSingleDonation());

        // WHEN
        snapshot.removeTransaction(recurringIncome);

        // THEN
        assertEquals(3, snapshot.getTransactions().size());
        assertFalse(snapshot.getTransactions().contains(recurringIncome));
        assertNull(recurringIncome.getSnapshot());
    }

    @Test
    public void getTransactionsByType() {
        // GIVEN
        final IncomeTransaction recurringIncome = newRecurringIncome();
        final IncomeTransaction singleIncome = newSingleIncome();
        final InvestmentTransaction recurringInvestment = newRecurringInvestment();
        final InvestmentTransaction singleInvestment = newSingleInvestment();
        final DonationTransaction recurringDonation = newRecurringDonation();
        final DonationTransaction singleDonation = newSingleDonation();

        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(
                                recurringIncome,
                                singleIncome,
                                recurringDonation,
                                singleDonation,
                                recurringInvestment,
                                singleInvestment),
                        ImmutableMap.of());

        // WHEN
        final Map<TransactionType, SortedSet<Transaction>> transactionsByType =
                snapshot.getTransactionsByType();

        // THEN
        assertEquals(3, transactionsByType.size());

        assertTrue(transactionsByType.containsKey(TransactionType.INCOME));
        final SortedSet<Transaction> incomes = transactionsByType.get(TransactionType.INCOME);
        assertEquals(2, incomes.size());
        final Iterator<Transaction> incomeIterator = incomes.iterator();
        assertTrue(equalsIgnoreIdAndDate(singleIncome, incomeIterator.next()));
        assertTrue(equalsIgnoreIdAndDate(recurringIncome, incomeIterator.next()));

        assertTrue(transactionsByType.containsKey(TransactionType.INVESTMENT));
        final SortedSet<Transaction> investments =
                transactionsByType.get(TransactionType.INVESTMENT);
        assertEquals(2, investments.size());
        final Iterator<Transaction> investmentIterator = investments.iterator();
        assertTrue(equalsIgnoreIdAndDate(singleInvestment, investmentIterator.next()));
        assertTrue(equalsIgnoreIdAndDate(recurringInvestment, investmentIterator.next()));

        assertTrue(transactionsByType.containsKey(TransactionType.DONATION));
        final SortedSet<Transaction> donations = transactionsByType.get(TransactionType.DONATION);
        assertEquals(2, donations.size());
        final Iterator<Transaction> donationIterator = donations.iterator();
        assertTrue(equalsIgnoreIdAndDate(singleDonation, donationIterator.next()));
        assertTrue(equalsIgnoreIdAndDate(recurringDonation, donationIterator.next()));

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    transactionsByType.remove(TransactionType.INCOME);
                });

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    transactionsByType.get(TransactionType.DONATION).remove(recurringDonation);
                });
    }

    @Test
    public void equals() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());

        // Itself
        assertTrue(snapshot.equals(snapshot));

        // Not instance of Snapshot
        assertFalse(snapshot.equals(null));
        assertFalse(snapshot.equals("Another type of object"));

        // Same Id null
        final Snapshot anotherSnapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(),
                        ImmutableList.of(),
                        ImmutableMap.of());
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
