package br.net.du.myequity.model;

import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreIdAndDate;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_MONTH;
import static br.net.du.myequity.test.TestConstants.FIRST_SNAPSHOT_YEAR;
import static br.net.du.myequity.test.TestConstants.TITHING_PERCENTAGE;
import static br.net.du.myequity.test.TestConstants.newRecurringIncome;
import static br.net.du.myequity.test.TestConstants.newRecurringInvestment;
import static br.net.du.myequity.test.TestConstants.newRecurringNonTaxDeductibleDonation;
import static br.net.du.myequity.test.TestConstants.newSimpleAssetAccount;
import static br.net.du.myequity.test.TestConstants.newSimpleLiabilityAccount;
import static br.net.du.myequity.test.TestConstants.newSingleIncome;
import static br.net.du.myequity.test.TestConstants.newSingleInvestment;
import static br.net.du.myequity.test.TestConstants.newSingleTaxDeductibleDonation;
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
import br.net.du.myequity.test.TestConstants;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SnapshotTest {
    private BigDecimal EXPECTED_NET_WORTH = new BigDecimal("7500.00");
    private BigDecimal EXPECTED_NET_WORTH_WITH_TRANSACTIONS = new BigDecimal("4785.00");

    private SimpleLiabilityAccount simpleLiabilityAccount;
    private SimpleAssetAccount simpleAssetAccount;

    @BeforeEach
    public void setUp() {
        simpleLiabilityAccount = newSimpleLiabilityAccount(CurrencyUnit.USD);
        simpleAssetAccount = newSimpleAssetAccount(CurrencyUnit.USD);
    }

    @Test
    public void constructor() {
        // WHEN
        final IncomeTransaction recurringIncome = newRecurringIncome();
        final IncomeTransaction singleIncome = newSingleIncome(CurrencyUnit.USD);
        final DonationTransaction recurringDonation =
                newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD);
        final DonationTransaction singleDonation =
                TestConstants.newSingleTaxDeductibleDonation(CurrencyUnit.USD);

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
                        "Another Account",
                        CurrencyUnit.USD,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO);
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
                        LocalDate.now(),
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
        final IncomeTransaction transaction = newSingleIncome(CurrencyUnit.USD);

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
        snapshot.addTransaction(newSingleIncome(CurrencyUnit.USD));
        snapshot.addTransaction(newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD));
        snapshot.addTransaction(TestConstants.newSingleTaxDeductibleDonation(CurrencyUnit.USD));

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
        final IncomeTransaction singleIncome = newSingleIncome(CurrencyUnit.USD);
        final InvestmentTransaction recurringInvestment = newRecurringInvestment();
        final InvestmentTransaction singleInvestment = newSingleInvestment(CurrencyUnit.USD);
        final DonationTransaction recurringDonation =
                newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD);
        final DonationTransaction singleDonation =
                TestConstants.newSingleTaxDeductibleDonation(CurrencyUnit.USD);

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

    @Test
    public void putCurrencyConversionRate_zero_throws() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN/THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    snapshot.putCurrencyConversionRate(CurrencyUnit.CAD, BigDecimal.ZERO);
                });
    }

    @Test
    public void changeBaseCurrencyUnitTo_sameAsBaseCurrency_noop() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN
        snapshot.changeBaseCurrencyUnitTo(snapshot.getBaseCurrencyUnit());

        // THEN
        final List<String> currenciesInUseBaseFirst = snapshot.getCurrenciesInUseBaseFirst();
        assertEquals(3, currenciesInUseBaseFirst.size());
        assertEquals(CurrencyUnit.USD.getCode(), currenciesInUseBaseFirst.get(0));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.of("BRL").getCode()));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.EUR.getCode()));

        verifySnapshot(
                snapshot,
                CurrencyUnit.USD,
                "24500.00",
                "6896.75",
                "17603.25",
                "4410.00",
                "490.00",
                "551.25",
                "367.50");

        final Map<String, BigDecimal> currencyConversionRates =
                snapshot.getCurrencyConversionRates();
        assertEquals(2, currencyConversionRates.size());
        assertTrue(new BigDecimal("0.80").compareTo(currencyConversionRates.get("EUR")) == 0);
        assertTrue(new BigDecimal("5.00").compareTo(currencyConversionRates.get("BRL")) == 0);
    }

    @Test
    public void changeBaseCurrencyUnitTo_unknownCurrency_throws() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN/THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.CAD);
                });
    }

    @Test
    public void changeBaseCurrencyUnitTo_firstCurrency_happy() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.of("BRL"));

        // THEN
        final List<String> currenciesInUseBaseFirst = snapshot.getCurrenciesInUseBaseFirst();
        assertEquals(3, currenciesInUseBaseFirst.size());
        assertEquals(CurrencyUnit.of("BRL").getCode(), currenciesInUseBaseFirst.get(0));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.EUR.getCode()));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.USD.getCode()));

        verifySnapshot(
                snapshot,
                CurrencyUnit.of("BRL"),
                "122500.00",
                "34483.75",
                "88016.25",
                "22050.00",
                "2450.00",
                "2756.25",
                "1837.50");

        final Map<String, BigDecimal> currencyConversionRates =
                snapshot.getCurrencyConversionRates();
        assertEquals(2, currencyConversionRates.size());
        assertTrue(new BigDecimal("0.20").compareTo(currencyConversionRates.get("USD")) == 0);
        assertTrue(new BigDecimal("0.16").compareTo(currencyConversionRates.get("EUR")) == 0);
    }

    @Test
    public void changeBaseCurrencyUnitTo_secondCurrency_happy() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.of("EUR"));

        // THEN
        final List<String> currenciesInUseBaseFirst = snapshot.getCurrenciesInUseBaseFirst();
        assertEquals(3, currenciesInUseBaseFirst.size());
        assertEquals(CurrencyUnit.EUR.getCode(), currenciesInUseBaseFirst.get(0));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.of("BRL").getCode()));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.USD.getCode()));

        verifySnapshot(
                snapshot,
                CurrencyUnit.EUR,
                "19600.00",
                "5517.40",
                "14082.60",
                "3528.00",
                "392.00",
                "441.00",
                "294.00");

        final Map<String, BigDecimal> currencyConversionRates =
                snapshot.getCurrencyConversionRates();
        assertEquals(2, currencyConversionRates.size());
        assertTrue(new BigDecimal("1.25").compareTo(currencyConversionRates.get("USD")) == 0);
        assertTrue(new BigDecimal("6.25").compareTo(currencyConversionRates.get("BRL")) == 0);
    }

    @Test
    public void changeBaseCurrencyUnitTo_firstCurrencyIdempotency_happy() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.of("BRL"));
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.USD);

        // THEN
        final List<String> currenciesInUseBaseFirst = snapshot.getCurrenciesInUseBaseFirst();
        assertEquals(3, currenciesInUseBaseFirst.size());
        assertEquals(CurrencyUnit.USD.getCode(), currenciesInUseBaseFirst.get(0));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.of("BRL").getCode()));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.EUR.getCode()));

        verifySnapshot(
                snapshot,
                CurrencyUnit.USD,
                "24500.00",
                "6896.75",
                "17603.25",
                "4410.00",
                "490.00",
                "551.25",
                "367.50");

        final Map<String, BigDecimal> currencyConversionRates =
                snapshot.getCurrencyConversionRates();
        assertEquals(2, currencyConversionRates.size());
        assertTrue(new BigDecimal("0.80").compareTo(currencyConversionRates.get("EUR")) == 0);
        assertTrue(new BigDecimal("5.00").compareTo(currencyConversionRates.get("BRL")) == 0);
    }

    @Test
    public void changeBaseCurrencyUnitTo_secondCurrencyIdempotency_happy() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.EUR);
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.USD);

        // THEN
        final List<String> currenciesInUseBaseFirst = snapshot.getCurrenciesInUseBaseFirst();
        assertEquals(3, currenciesInUseBaseFirst.size());
        assertEquals(CurrencyUnit.USD.getCode(), currenciesInUseBaseFirst.get(0));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.of("BRL").getCode()));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.EUR.getCode()));

        verifySnapshot(
                snapshot,
                CurrencyUnit.USD,
                "24500.00",
                "6896.75",
                "17603.25",
                "4410.00",
                "490.00",
                "551.25",
                "367.50");

        final Map<String, BigDecimal> currencyConversionRates =
                snapshot.getCurrencyConversionRates();
        assertEquals(2, currencyConversionRates.size());
        assertTrue(new BigDecimal("0.80").compareTo(currencyConversionRates.get("EUR")) == 0);
        assertTrue(new BigDecimal("5.00").compareTo(currencyConversionRates.get("BRL")) == 0);
    }

    @Test
    public void changeBaseCurrencyUnitTo_multipleCurrenciesIdempotency_happy() {
        // GIVEN
        final Snapshot snapshot = buildSnapshotWithMultipleCurrencies();

        // WHEN
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.of("BRL"));
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.EUR);
        snapshot.changeBaseCurrencyUnitTo(CurrencyUnit.USD);

        // THEN
        final List<String> currenciesInUseBaseFirst = snapshot.getCurrenciesInUseBaseFirst();
        assertEquals(3, currenciesInUseBaseFirst.size());
        assertEquals(CurrencyUnit.USD.getCode(), currenciesInUseBaseFirst.get(0));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.of("BRL").getCode()));
        assertTrue(currenciesInUseBaseFirst.contains(CurrencyUnit.EUR.getCode()));

        verifySnapshot(
                snapshot,
                CurrencyUnit.USD,
                "24500.00",
                "6896.75",
                "17603.25",
                "4410.00",
                "490.00",
                "551.25",
                "367.50");

        final Map<String, BigDecimal> currencyConversionRates =
                snapshot.getCurrencyConversionRates();
        assertEquals(2, currencyConversionRates.size());
        assertTrue(new BigDecimal("0.80").compareTo(currencyConversionRates.get("EUR")) == 0);
        assertTrue(new BigDecimal("5.00").compareTo(currencyConversionRates.get("BRL")) == 0);
    }

    private Snapshot buildSnapshotWithMultipleCurrencies() {
        final Snapshot snapshot =
                new Snapshot(
                        FIRST_SNAPSHOT_YEAR,
                        FIRST_SNAPSHOT_MONTH,
                        CURRENCY_UNIT,
                        TITHING_PERCENTAGE,
                        ImmutableSortedSet.of(
                                newSimpleAssetAccount(CurrencyUnit.USD),
                                newSimpleLiabilityAccount(CurrencyUnit.USD)),
                        ImmutableList.of(
                                newSingleIncome(CurrencyUnit.USD),
                                newSingleInvestment(CurrencyUnit.USD),
                                newSingleTaxDeductibleDonation(CurrencyUnit.USD),
                                newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD)),
                        ImmutableMap.of());
        snapshot.setId(42L);

        assertTrue(snapshot.supports(CurrencyUnit.USD));

        assertFalse(snapshot.supports(CurrencyUnit.of("BRL")));
        snapshot.putCurrencyConversionRate(CurrencyUnit.of("BRL"), new BigDecimal("5.00"));
        assertTrue(snapshot.supports(CurrencyUnit.of("BRL")));

        snapshot.addAccount(newSimpleAssetAccount(CurrencyUnit.of("BRL")));
        snapshot.addAccount(newSimpleLiabilityAccount(CurrencyUnit.of("BRL")));

        snapshot.addTransaction(newSingleIncome(CurrencyUnit.of("BRL")));
        snapshot.addTransaction(newSingleInvestment(CurrencyUnit.of("BRL")));
        snapshot.addTransaction(newSingleTaxDeductibleDonation(CurrencyUnit.of("BRL")));
        snapshot.addTransaction(newRecurringNonTaxDeductibleDonation(CurrencyUnit.of("BRL")));

        assertFalse(snapshot.supports(CurrencyUnit.EUR));
        snapshot.putCurrencyConversionRate(CurrencyUnit.EUR, new BigDecimal("0.80"));
        assertTrue(snapshot.supports(CurrencyUnit.EUR));

        snapshot.addAccount(newSimpleAssetAccount(CurrencyUnit.EUR));
        snapshot.addAccount(newSimpleLiabilityAccount(CurrencyUnit.EUR));

        snapshot.addTransaction(newSingleIncome(CurrencyUnit.EUR));
        snapshot.addTransaction(newSingleInvestment(CurrencyUnit.EUR));
        snapshot.addTransaction(newSingleTaxDeductibleDonation(CurrencyUnit.EUR));
        snapshot.addTransaction(newRecurringNonTaxDeductibleDonation(CurrencyUnit.EUR));

        verifySnapshot(
                snapshot,
                CurrencyUnit.USD,
                "24500.00",
                "6896.75",
                "17603.25",
                "4410.00",
                "490.00",
                "551.25",
                "367.50");

        return snapshot;
    }

    private void verifySnapshot(
            final Snapshot snapshot,
            final CurrencyUnit baseCurrencyUnit,
            final String assetsTotal,
            final String liabilitiesTotal,
            final String netWorth,
            final String incomesTotal,
            final String investmentsTotal,
            final String donationsTotal,
            final String taxDeductibleDonationsTotal) {
        assertEquals(baseCurrencyUnit, snapshot.getBaseCurrencyUnit());
        assertTrue(
                new BigDecimal(assetsTotal).compareTo(snapshot.getTotalFor(AccountType.ASSET))
                        == 0);
        assertTrue(
                new BigDecimal(liabilitiesTotal)
                                .compareTo(snapshot.getTotalFor(AccountType.LIABILITY))
                        == 0);
        assertTrue(new BigDecimal(netWorth).compareTo(snapshot.getNetWorth()) == 0);

        assertTrue(
                new BigDecimal(incomesTotal).compareTo(snapshot.getTotalFor(TransactionType.INCOME))
                        == 0);
        assertTrue(
                new BigDecimal(investmentsTotal)
                                .compareTo(snapshot.getTotalFor(TransactionType.INVESTMENT))
                        == 0);
        assertTrue(
                new BigDecimal(donationsTotal)
                                .compareTo(snapshot.getTotalFor(TransactionType.DONATION))
                        == 0);
        assertTrue(
                new BigDecimal(taxDeductibleDonationsTotal)
                                .compareTo(snapshot.getTaxDeductibleDonationsTotal())
                        == 0);
    }
}
