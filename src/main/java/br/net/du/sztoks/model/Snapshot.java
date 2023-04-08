package br.net.du.sztoks.model;

import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.account.FutureTithingAccount;
import br.net.du.sztoks.model.account.FutureTithingCapable;
import br.net.du.sztoks.model.account.TithingAccount;
import br.net.du.sztoks.model.transaction.DonationTransaction;
import br.net.du.sztoks.model.transaction.IncomeTransaction;
import br.net.du.sztoks.model.transaction.RecurrencePolicy;
import br.net.du.sztoks.model.transaction.Transaction;
import br.net.du.sztoks.model.transaction.TransactionType;
import br.net.du.sztoks.model.util.UserUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SortNatural;
import org.joda.money.CurrencyUnit;

@Entity
@Table(
        name = "snapshots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "month"}))
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class Snapshot implements Comparable<Snapshot> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter // for testing
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private User user;

    @Column(nullable = false)
    @Getter
    private Integer year;

    @Column(nullable = false)
    @Getter
    private Integer month;

    @Column(nullable = false)
    protected String baseCurrency;

    @Column(nullable = false)
    @Getter
    private BigDecimal defaultTithingPercentage;

    @Column(nullable = true)
    @Setter
    private BigDecimal assetsTotal;

    @Column(nullable = true)
    @Setter
    private BigDecimal liabilitiesTotal;

    @Column(nullable = true)
    @Setter
    private BigDecimal incomesTotal;

    @Column(nullable = true)
    @Setter
    private BigDecimal investmentsTotal;

    @Column(nullable = true)
    @Setter
    private BigDecimal donationsTotal;

    @Column(nullable = true)
    @Setter
    private BigDecimal taxDeductibleDonationsTotal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id", nullable = true)
    @Getter
    private Snapshot previous;

    @Column(name = "previous_id", insertable = false, updatable = false)
    @Getter
    private Long previousId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id", nullable = true)
    @Getter
    private Snapshot next;

    @Column(name = "next_id", insertable = false, updatable = false)
    @Getter
    private Long nextId;

    @OneToMany(
            mappedBy = "snapshot",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @SortNatural // Ref.: https://thorben-janssen.com/ordering-vs-sorting-hibernate-use/
    private final SortedSet<Account> accounts = new TreeSet<>();

    @OneToMany(
            mappedBy = "snapshot",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    // this should match Transaction::compareTo()
    @OrderBy("currency ASC, date ASC, description ASC, id ASC")
    private final List<Transaction> transactions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "snapshot_currency_conversion_rates",
            joinColumns = {@JoinColumn(name = "snapshot_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "to_currency")
    @Column(name = "conversion_rate", precision = 19, scale = 8)
    private Map<String, BigDecimal> currencyConversionRates = new HashMap<>();

    @Transient private List<String> currenciesInUseBaseFirst;

    public Snapshot(
            final int year,
            final int month,
            @NonNull final CurrencyUnit baseCurrencyUnit,
            @NonNull final BigDecimal defaultTithingPercentage,
            @NonNull final SortedSet<Account> accounts,
            @NonNull final List<Transaction> transactions,
            @NonNull final Map<String, BigDecimal> currencyConversionRates) {
        this.year = year;
        this.month = month;

        baseCurrency = baseCurrencyUnit.getCode();

        if (defaultTithingPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "tithingPercentage must be greater than or equal to zero");
        }
        this.defaultTithingPercentage = defaultTithingPercentage;

        assetsTotal = BigDecimal.ZERO;
        liabilitiesTotal = BigDecimal.ZERO;

        incomesTotal = BigDecimal.ZERO;
        investmentsTotal = BigDecimal.ZERO;
        donationsTotal = BigDecimal.ZERO;

        taxDeductibleDonationsTotal = BigDecimal.ZERO;

        this.currencyConversionRates.putAll(currencyConversionRates);

        consolidateTithingAccountsToBaseCurrency(baseCurrencyUnit, accounts);
        addNonTithingNonFutureTithingAccounts(accounts);
        addTransactions(year, month, transactions);
    }

    public void setDefaultTithingPercentage(@NonNull final BigDecimal newDefaultTithingPercentage) {
        if (newDefaultTithingPercentage.compareTo(BigDecimal.ZERO) < 0
                || newDefaultTithingPercentage.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("Invalid tithing percentage");
        }

        if (newDefaultTithingPercentage.compareTo(defaultTithingPercentage) == 0) {
            return;
        }

        final BigDecimal currentFutureTithing = computeFutureTithingTotal();

        defaultTithingPercentage = newDefaultTithingPercentage;

        final BigDecimal newFutureTithing = computeFutureTithingTotal();

        final BigDecimal futureTithingDiff = newFutureTithing.subtract(currentFutureTithing);

        updateNetWorth(AccountType.LIABILITY, getBaseCurrencyUnit(), futureTithingDiff);
    }

    private BigDecimal computeFutureTithingTotal() {
        return accounts.stream()
                .filter(account -> (account instanceof FutureTithingAccount))
                .map(account -> toBaseCurrency(account.getCurrencyUnit(), account.getBalance()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private void consolidateTithingAccountsToBaseCurrency(
            @NonNull final CurrencyUnit baseCurrencyUnit,
            @NonNull final SortedSet<Account> accounts) {
        accounts.stream()
                .filter(account -> (account instanceof TithingAccount))
                .map(account -> account.copy())
                .forEach(
                        account ->
                                updateTithingAmount(
                                        baseCurrencyUnit,
                                        toBaseCurrency(
                                                account.getCurrencyUnit(), account.getBalance())));
    }

    private void addNonTithingNonFutureTithingAccounts(@NonNull final SortedSet<Account> accounts) {
        accounts.stream()
                .filter(
                        account ->
                                !(account instanceof TithingAccount)
                                        && !(account instanceof FutureTithingAccount))
                .map(account -> account.copy())
                .forEach(account -> addAccount(account));
    }

    private void addTransactions(
            final int year, final int month, @NonNull final List<Transaction> transactions) {
        transactions.stream()
                .forEach(
                        transaction -> {
                            final Transaction transactionCopy = transaction.copy();

                            final LocalDate newDate =
                                    transactionCopy.getDate().withYear(year).withMonth(month);
                            transactionCopy.setDate(newDate);

                            addTransaction(transactionCopy);
                        });
    }

    public void setPrevious(final Snapshot previous) {
        this.previous = previous;
        previousId = previous == null ? null : previous.getId();
    }

    public void setNext(final Snapshot next) {
        this.next = next;
        nextId = next == null ? null : next.getId();
    }

    public SortedSet<Account> getAccounts() {
        return ImmutableSortedSet.copyOf(accounts);
    }

    public Map<AccountType, SortedSet<Account>> getAccountsByType() {
        return accounts.stream()
                .collect(
                        collectingAndThen(
                                groupingBy(
                                        accountData -> accountData.getAccountType(),
                                        collectingAndThen(toSet(), ImmutableSortedSet::copyOf)),
                                ImmutableMap::copyOf));
    }

    public Optional<Account> getAccountById(@NonNull final Long id) {
        return accounts.stream().filter(entry -> id.equals(entry.getId())).findFirst();
    }

    public void addAccount(@NonNull final Account account) {
        // Prevents infinite loop
        if (accounts.contains(account)) {
            return;
        }

        if (!supports(account.getCurrencyUnit())) {
            throw new IllegalArgumentException(
                    "Currency "
                            + account.getCurrencyUnit().toString()
                            + " not supported by Snapshot "
                            + id);
        }

        accounts.add(account);
        account.setSnapshot(this);

        if (account instanceof FutureTithingCapable) {
            updateFutureTithingAmount(
                    account.getCurrencyUnit(),
                    ((FutureTithingCapable) account).getFutureTithingReferenceAmount());
        }

        updateNetWorth(account.getAccountType(), account.getCurrencyUnit(), account.getBalance());
    }

    public void removeAccount(@NonNull final Account account) {
        // Prevents infinite loop
        if (!accounts.contains(account)) {
            return;
        }

        if (account instanceof FutureTithingCapable) {
            updateFutureTithingAmount(
                    account.getCurrencyUnit(),
                    ((FutureTithingCapable) account).getFutureTithingReferenceAmount().negate());
        }

        updateNetWorth(
                account.getAccountType(), account.getCurrencyUnit(), account.getBalance().negate());

        accounts.remove(account);
        account.setSnapshot(null);
    }

    public void updateNetWorth(
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal plusAmount) {
        if (plusAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (accountType.equals(AccountType.ASSET)) {
            final BigDecimal newAmount =
                    (assetsTotal == null)
                            ? getTotalFor(AccountType.ASSET)
                            : assetsTotal.add(toBaseCurrency(currencyUnit, plusAmount));

            assetsTotal = newAmount;

            log.debug("[SZTOKS] new assetsTotal = " + newAmount);

        } else if (accountType.equals(AccountType.LIABILITY)) {
            final BigDecimal newAmount =
                    (liabilitiesTotal == null)
                            ? getTotalFor(AccountType.LIABILITY)
                            : liabilitiesTotal.add(toBaseCurrency(currencyUnit, plusAmount));

            liabilitiesTotal = newAmount;

            log.debug("[SZTOKS] new liabilitiesTotal = " + newAmount);
        } else {
            throw new IllegalStateException("Unknown account type");
        }
    }

    public SortedSet<Transaction> getTransactions() {
        return ImmutableSortedSet.copyOf(transactions);
    }

    public Map<TransactionType, SortedSet<Transaction>> getTransactionsByType() {
        return transactions.stream()
                .collect(
                        collectingAndThen(
                                groupingBy(
                                        transaction -> transaction.getTransactionType(),
                                        collectingAndThen(toSet(), ImmutableSortedSet::copyOf)),
                                ImmutableMap::copyOf));
    }

    public List<Transaction> getRecurringTransactions() {
        return ImmutableList.copyOf(
                transactions.stream()
                        .filter(
                                transaction ->
                                        !transaction
                                                .getRecurrencePolicy()
                                                .equals(RecurrencePolicy.NONE))
                        .collect(Collectors.toCollection(() -> new ArrayList<>())));
    }

    public void addTransaction(@NonNull final Transaction transaction) {
        // Prevents infinite loop
        if (transactions.contains(transaction)) {
            return;
        }

        if (!supports(transaction.getCurrencyUnit())) {
            throw new IllegalArgumentException(
                    "Currency "
                            + transaction.getCurrencyUnit().toString()
                            + " not supported by Snapshot "
                            + id);
        }

        if (transaction instanceof IncomeTransaction) {
            final IncomeTransaction incomeTransaction = (IncomeTransaction) transaction;
            updateTithingAmount(
                    incomeTransaction.getCurrencyUnit(), incomeTransaction.getTithingAmount());
        } else if (transaction instanceof DonationTransaction) {
            updateTithingAmount(transaction.getCurrencyUnit(), transaction.getAmount().negate());
        }

        transactions.add(transaction);
        transaction.setSnapshot(this);

        updateTransactionsTotal(
                transaction.getTransactionType(),
                transaction.getCurrencyUnit(),
                transaction.getAmount(),
                isTaxDeductibleDonation(transaction));
    }

    public void removeTransaction(@NonNull final Transaction transaction) {
        // Prevents infinite loop
        if (!transactions.contains(transaction)) {
            return;
        }

        if (transaction instanceof IncomeTransaction) {
            final IncomeTransaction incomeTransaction = (IncomeTransaction) transaction;
            updateTithingAmount(
                    incomeTransaction.getCurrencyUnit(),
                    incomeTransaction.getTithingAmount().negate());
        } else if (transaction instanceof DonationTransaction) {
            updateTithingAmount(transaction.getCurrencyUnit(), transaction.getAmount());
        }

        transactions.remove(transaction);
        transaction.setSnapshot(null);

        updateTransactionsTotal(
                transaction.getTransactionType(),
                transaction.getCurrencyUnit(),
                transaction.getAmount().negate(),
                isTaxDeductibleDonation(transaction));
    }

    private boolean isTaxDeductibleDonation(@NonNull final Transaction transaction) {
        return (transaction instanceof DonationTransaction)
                && ((DonationTransaction) transaction).isTaxDeductible();
    }

    public void updateTithingAmount(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal plusAmount) {
        if (plusAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        final TithingAccount tithingAccount = getTithingAccount(currencyUnit);
        tithingAccount.setBalance(tithingAccount.getBalance().add(plusAmount));

        if (next != null) {
            next.updateTithingAmount(currencyUnit, plusAmount);
        }
    }

    public void updateFutureTithingAmount(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal plusAmount) {
        if (plusAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        final FutureTithingAccount futureTithingAccount = getFutureTithingAccount(currencyUnit);
        futureTithingAccount.setReferenceAmount(
                futureTithingAccount.getReferenceAmount().add(plusAmount));
    }

    public void updateTransactionsTotal(
            @NonNull final TransactionType transactionType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal amount,
            final boolean isTaxDeductibleDonation) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (transactionType == TransactionType.INCOME) {
            final BigDecimal newAmount =
                    (incomesTotal == null)
                            ? getTotalFor(TransactionType.INCOME)
                            : incomesTotal.add(toBaseCurrency(currencyUnit, amount));
            incomesTotal = newAmount;

            log.debug("[SZTOKS] new incomesTotal = " + newAmount);
        } else if (transactionType == TransactionType.INVESTMENT) {
            final BigDecimal newAmount =
                    (investmentsTotal == null)
                            ? getTotalFor(TransactionType.INVESTMENT)
                            : investmentsTotal.add(toBaseCurrency(currencyUnit, amount));
            investmentsTotal = newAmount;

            log.debug("[SZTOKS] new investmentsTotal = " + newAmount);
        } else if (transactionType == TransactionType.DONATION) {
            final BigDecimal newAmount =
                    (donationsTotal == null)
                            ? getTotalFor(TransactionType.DONATION)
                            : donationsTotal.add(toBaseCurrency(currencyUnit, amount));
            donationsTotal = newAmount;

            log.debug("[SZTOKS] new donationsTotal = " + newAmount);

            if (isTaxDeductibleDonation) {
                updateTaxDeductibleDonationsTotal(currencyUnit, amount);
            }
        } else {
            throw new IllegalStateException("Unknown transaction type");
        }
    }

    public void updateTaxDeductibleDonationsTotal(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal amount) {
        if (taxDeductibleDonationsTotal == null) {
            taxDeductibleDonationsTotal = getTaxDeductibleDonationsTotal();
        } else {
            taxDeductibleDonationsTotal =
                    taxDeductibleDonationsTotal.add(toBaseCurrency(currencyUnit, amount));
        }
    }

    public TithingAccount getTithingAccount(@NonNull final CurrencyUnit currencyUnit) {
        final Optional<Account> tithingAccountOpt =
                accounts.stream()
                        .filter(
                                account ->
                                        (account instanceof TithingAccount)
                                                && account.getCurrencyUnit().equals(currencyUnit))
                        .findFirst();

        if (tithingAccountOpt.isPresent()) {
            return (TithingAccount) tithingAccountOpt.get();
        }

        final TithingAccount tithingAccount =
                new TithingAccount(currencyUnit, LocalDate.now(), BigDecimal.ZERO);
        addAccount(tithingAccount);

        return tithingAccount;
    }

    public FutureTithingAccount getFutureTithingAccount(@NonNull final CurrencyUnit currencyUnit) {
        final Optional<Account> futureTithingAccountOpt =
                accounts.stream()
                        .filter(
                                account ->
                                        (account instanceof FutureTithingAccount)
                                                && account.getCurrencyUnit().equals(currencyUnit))
                        .findFirst();

        if (futureTithingAccountOpt.isPresent()) {
            return (FutureTithingAccount) futureTithingAccountOpt.get();
        }

        final FutureTithingAccount futureTithingAccount =
                new FutureTithingAccount(currencyUnit, LocalDate.now(), BigDecimal.ZERO);
        addAccount(futureTithingAccount);

        return futureTithingAccount;
    }

    public CurrencyUnit getBaseCurrencyUnit() {
        return CurrencyUnit.of(baseCurrency);
    }

    public void changeBaseCurrencyUnitTo(@NonNull final CurrencyUnit newBaseCurrency) {
        if (newBaseCurrency.equals(getBaseCurrencyUnit())) {
            // NOOP
            return;
        }

        if (!supports(newBaseCurrency)) {
            throw new IllegalArgumentException("Invalid currency: " + newBaseCurrency);
        }

        final String oldBaseCurrency = baseCurrency;

        // Remove direct conversion rate
        final BigDecimal oldToNewBaseCurrencyConversionRate =
                currencyConversionRates.get(newBaseCurrency.getCode());
        currencyConversionRates.remove(newBaseCurrency.getCode());

        // Update remaining currencies
        for (final String currency : currencyConversionRates.keySet()) {
            currencyConversionRates.put(
                    currency,
                    currencyConversionRates
                            .get(currency)
                            .divide(
                                    oldToNewBaseCurrencyConversionRate,
                                    DIVISION_SCALE,
                                    RoundingMode.HALF_UP));
        }

        // Add new direct conversion rate
        putCurrencyConversionRate(
                CurrencyUnit.of(oldBaseCurrency),
                BigDecimal.ONE.divide(
                        oldToNewBaseCurrencyConversionRate, DIVISION_SCALE, RoundingMode.HALF_UP));

        // Reset all
        baseCurrency = newBaseCurrency.getCode();
        updateCurrenciesInUse();

        resetTotals();
    }

    private void resetTotals() {
        resetAssetsTotal();
        resetLiabilitiesTotal();
        resetIncomesTotal();
        resetInvestmentsTotal();
        resetDonationsTotal();
    }

    private void resetAssetsTotal() {
        assetsTotal = BigDecimal.ZERO;

        accounts.stream()
                .filter(account -> account.getAccountType().equals(AccountType.ASSET))
                .forEach(
                        asset -> {
                            final BigDecimal amount =
                                    asset.getCurrencyUnit().equals(getBaseCurrencyUnit())
                                            ? asset.getBalance()
                                            : asset.getBalance()
                                                    .divide(
                                                            currencyConversionRates.get(
                                                                    asset.getCurrencyUnit()
                                                                            .getCode()),
                                                            DIVISION_SCALE,
                                                            RoundingMode.HALF_UP);

                            assetsTotal = assetsTotal.add(amount);
                        });
    }

    private void resetLiabilitiesTotal() {
        liabilitiesTotal = BigDecimal.ZERO;

        accounts.stream()
                .filter(account -> account.getAccountType().equals(AccountType.LIABILITY))
                .forEach(
                        liability -> {
                            final BigDecimal amount =
                                    liability.getCurrencyUnit().equals(getBaseCurrencyUnit())
                                            ? liability.getBalance()
                                            : liability
                                                    .getBalance()
                                                    .divide(
                                                            currencyConversionRates.get(
                                                                    liability
                                                                            .getCurrencyUnit()
                                                                            .getCode()),
                                                            DIVISION_SCALE,
                                                            RoundingMode.HALF_UP);

                            liabilitiesTotal = liabilitiesTotal.add(amount);
                        });
    }

    private void resetIncomesTotal() {
        incomesTotal = BigDecimal.ZERO;

        transactions.stream()
                .filter(
                        transaction ->
                                transaction.getTransactionType().equals(TransactionType.INCOME))
                .forEach(
                        income -> {
                            final BigDecimal amount =
                                    income.getCurrencyUnit().equals(getBaseCurrencyUnit())
                                            ? income.getAmount()
                                            : income.getAmount()
                                                    .divide(
                                                            currencyConversionRates.get(
                                                                    income.getCurrencyUnit()
                                                                            .getCode()),
                                                            DIVISION_SCALE,
                                                            RoundingMode.HALF_UP);

                            incomesTotal = incomesTotal.add(amount);
                        });
    }

    private void resetInvestmentsTotal() {
        investmentsTotal = BigDecimal.ZERO;

        transactions.stream()
                .filter(
                        transaction ->
                                transaction.getTransactionType().equals(TransactionType.INVESTMENT))
                .forEach(
                        investment -> {
                            final BigDecimal amount =
                                    investment.getCurrencyUnit().equals(getBaseCurrencyUnit())
                                            ? investment.getAmount()
                                            : investment
                                                    .getAmount()
                                                    .divide(
                                                            currencyConversionRates.get(
                                                                    investment
                                                                            .getCurrencyUnit()
                                                                            .getCode()),
                                                            DIVISION_SCALE,
                                                            RoundingMode.HALF_UP);

                            investmentsTotal = investmentsTotal.add(amount);
                        });
    }

    private void resetDonationsTotal() {
        donationsTotal = BigDecimal.ZERO;
        taxDeductibleDonationsTotal = BigDecimal.ZERO;

        transactions.stream()
                .filter(account -> account.getTransactionType().equals(TransactionType.DONATION))
                .forEach(
                        donation -> {
                            final BigDecimal amount =
                                    donation.getCurrencyUnit().equals(getBaseCurrencyUnit())
                                            ? donation.getAmount()
                                            : donation.getAmount()
                                                    .divide(
                                                            currencyConversionRates.get(
                                                                    donation.getCurrencyUnit()
                                                                            .getCode()),
                                                            DIVISION_SCALE,
                                                            RoundingMode.HALF_UP);

                            donationsTotal = donationsTotal.add(amount);

                            if (((DonationTransaction) donation).isTaxDeductible()) {
                                taxDeductibleDonationsTotal =
                                        taxDeductibleDonationsTotal.add(amount);
                            }
                        });
    }

    public Map<String, BigDecimal> getCurrencyConversionRates() {
        return ImmutableMap.copyOf(currencyConversionRates);
    }

    public SortedSet<String> getCurrenciesInUse() {
        return ImmutableSortedSet.copyOf(getCurrenciesInUseBaseFirst());
    }

    public boolean supports(@NonNull final CurrencyUnit currencyUnit) {
        final String currencyStr = currencyUnit.toString();
        return getCurrenciesInUse().contains(currencyStr);
    }

    public boolean hasConversionRate(@NonNull final CurrencyUnit currencyUnit) {
        return currencyConversionRates.containsKey(currencyUnit.toString());
    }

    public void putCurrencyConversionRate(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal conversionRate) {
        if (conversionRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Conversion rate can't be zero");
        }

        final BigDecimal previousValue =
                currencyConversionRates.put(currencyUnit.getCode(), conversionRate);

        if (previousValue == null) {
            updateCurrenciesInUse();
        } else {
            // Recompute totals if this currency was already present
            assetsTotal = computeTotalFor(AccountType.ASSET);
            liabilitiesTotal = computeTotalFor(AccountType.LIABILITY);
            incomesTotal = computeTotalFor(TransactionType.INCOME);
            investmentsTotal = computeTotalFor(TransactionType.INVESTMENT);
            donationsTotal = computeTotalFor(TransactionType.DONATION);
            taxDeductibleDonationsTotal = comupteTaxDeductibleDonationsTotal();
        }

        if (next != null && !next.hasConversionRate(currencyUnit)) {
            next.putCurrencyConversionRate(currencyUnit, conversionRate);
        }
    }

    public List<String> getCurrenciesInUseBaseFirst() {
        if (currenciesInUseBaseFirst == null) {
            updateCurrenciesInUse();
        }
        return currenciesInUseBaseFirst;
    }

    private void updateCurrenciesInUse() {
        currenciesInUseBaseFirst = new ArrayList<>();

        currenciesInUseBaseFirst.add(baseCurrency);

        for (final String currency : ImmutableSortedSet.copyOf(currencyConversionRates.keySet())) {
            currenciesInUseBaseFirst.add(currency);
        }
    }

    public void setUser(final User newUser) {
        // Prevents infinite loop
        if (UserUtils.equals(user, newUser)) {
            return;
        }

        final User oldUser = user;
        user = newUser;

        if (oldUser != null) {
            oldUser.removeSnapshot(this);
        }

        if (newUser != null) {
            newUser.addSnapshot(this);
        }
    }

    public BigDecimal getNetWorth() {
        return getTotalFor(AccountType.ASSET)
                .subtract(getTotalFor(AccountType.LIABILITY))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalFor(@NonNull final AccountType accountType) {
        if (accountType.equals(AccountType.ASSET)) {
            if (assetsTotal == null) {
                assetsTotal = computeTotalFor(AccountType.ASSET);
            }
            return assetsTotal;
        } else if (accountType.equals(AccountType.LIABILITY)) {
            if (liabilitiesTotal == null) {
                liabilitiesTotal = computeTotalFor(AccountType.LIABILITY);
            }
            return liabilitiesTotal;
        } else {
            throw new IllegalArgumentException("Unknown account type");
        }
    }

    public BigDecimal computeTotalFor(@NonNull final AccountType accountType) {
        return accounts.stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .map(account -> toBaseCurrency(account.getCurrencyUnit(), account.getBalance()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalFor(@NonNull final TransactionType transactionType) {
        if (transactionType.equals(TransactionType.INCOME)) {
            if (incomesTotal == null) {
                incomesTotal = computeTotalFor(TransactionType.INCOME);
            }
            return incomesTotal;
        } else if (transactionType.equals(TransactionType.INVESTMENT)) {
            if (investmentsTotal == null) {
                investmentsTotal = computeTotalFor(TransactionType.INVESTMENT);
            }
            return investmentsTotal;
        } else {
            if (donationsTotal == null) {
                donationsTotal = computeTotalFor(TransactionType.DONATION);
            }
            return donationsTotal;
        }
    }

    public BigDecimal computeTotalFor(@NonNull final TransactionType transactionType) {
        return transactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals(transactionType))
                .map(t -> toBaseCurrency(t.getCurrencyUnit(), t.getAmount()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTaxDeductibleDonationsTotal() {
        if (taxDeductibleDonationsTotal == null) {
            taxDeductibleDonationsTotal = comupteTaxDeductibleDonationsTotal();
        }
        return taxDeductibleDonationsTotal;
    }

    private BigDecimal comupteTaxDeductibleDonationsTotal() {
        return transactions.stream()
                .filter(
                        transaction ->
                                transaction.getTransactionType().equals(TransactionType.DONATION)
                                        && ((DonationTransaction) transaction).isTaxDeductible())
                .map(t -> toBaseCurrency(t.getCurrencyUnit(), t.getAmount()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTithingBalance() {
        return getTithingBalanceFor(TithingAccount.class);
    }

    public BigDecimal getFutureTithingBalance() {
        return getTithingBalanceFor(FutureTithingAccount.class);
    }

    private BigDecimal getTithingBalanceFor(
            @NonNull final Class<? extends Account> tithingAccountType) {
        return accounts.stream()
                .filter(account -> tithingAccountType.isInstance(account))
                .map(account -> toBaseCurrency(account.getCurrencyUnit(), account.getBalance()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal toBaseCurrency(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        if (currencyUnit.equals(getBaseCurrencyUnit())) {
            return amount;
        }

        return amount.divide(
                currencyConversionRates.get(currencyUnit.getCode()),
                DIVISION_SCALE,
                RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Snapshot)) {
            return false;
        }

        if (id == null) {
            return false;
        }

        final Snapshot otherSnapshot = (Snapshot) other;

        return id.equals(otherSnapshot.getId());
    }

    @Override
    public int hashCode() {
        return 43;
    }

    @Override
    public int compareTo(final Snapshot other) {
        if (other.getYear().compareTo(year) == 0) {
            return other.getMonth().compareTo(month);
        }
        return other.getYear().compareTo(year);
    }
}
