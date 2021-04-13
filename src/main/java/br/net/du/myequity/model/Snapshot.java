package br.net.du.myequity.model;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import br.net.du.myequity.model.util.UserUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.sun.istack.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.SortNatural;
import org.joda.money.CurrencyUnit;

@Entity
@Table(
        name = "snapshots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "month"}))
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Snapshot implements Comparable<Snapshot> {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[0-9]{4}-[0-9]{2}$");

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
    @Setter
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
    @Setter
    private Snapshot previous;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_id", nullable = true)
    @Getter
    @Setter
    private Snapshot next;

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
    @Column(name = "conversion_rate", precision = 19, scale = 4)
    private Map<String, BigDecimal> currencyConversionRates = new HashMap<>();

    public Snapshot(
            final int year,
            final int month,
            @NonNull final CurrencyUnit baseCurrencyUnit,
            @NonNull final BigDecimal defaultTithingPercentage,
            @NotNull final SortedSet<Account> accounts,
            @NonNull final List<Transaction> transactions,
            @NonNull final Map<String, BigDecimal> currencyConversionRates) {
        this.year = year;
        this.month = month;

        this.baseCurrency = baseCurrencyUnit.getCode();

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

        accounts.stream().forEach(account -> addAccount(account.copy()));

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

    private String validateName(@NonNull final String name) {
        final Matcher matcher = VALID_NAME_PATTERN.matcher(name);
        if (!matcher.find()) {
            throw new IllegalArgumentException(String.format("Invalid Snapshot name: %s", name));
        }
        return name;
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

        updateNetWorth(account.getAccountType(), account.getCurrencyUnit(), account.getBalance());
    }

    public void removeAccount(@NonNull final Account account) {
        // Prevents infinite loop
        if (!accounts.contains(account)) {
            return;
        }

        accounts.remove(account);
        account.setSnapshot(null);

        updateNetWorth(
                account.getAccountType(), account.getCurrencyUnit(), account.getBalance().negate());
    }

    public void updateNetWorth(
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal plusAmount) {
        if (plusAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (accountType.equals(AccountType.ASSET)) {
            if (assetsTotal == null) {
                assetsTotal = getTotalFor(AccountType.ASSET);
            } else {
                assetsTotal = assetsTotal.add(toBaseCurrency(currencyUnit, plusAmount));
            }
        } else { // AccountType.LIABILITY
            if (liabilitiesTotal == null) {
                liabilitiesTotal = getTotalFor(AccountType.LIABILITY);
            } else {
                liabilitiesTotal = liabilitiesTotal.add(toBaseCurrency(currencyUnit, plusAmount));
            }
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
                        .filter(Transaction::isRecurring)
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

        final TithingAccount tithingAccount = getTithingAccountFor(currencyUnit);
        tithingAccount.setBalance(tithingAccount.getBalance().add(plusAmount));

        if (next != null) {
            next.updateTithingAmount(currencyUnit, plusAmount);
        }
    }

    public void updateTransactionsTotal(
            @NonNull final TransactionType transactionType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal amount) {
        updateTransactionsTotal(transactionType, currencyUnit, amount, false);
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
            if (incomesTotal == null) {
                incomesTotal = getTotalFor(TransactionType.INCOME);
            } else {
                incomesTotal = incomesTotal.add(toBaseCurrency(currencyUnit, amount));
            }
        } else if (transactionType == TransactionType.INVESTMENT) {
            if (investmentsTotal == null) {
                investmentsTotal = getTotalFor(TransactionType.INVESTMENT);
            } else {
                investmentsTotal = investmentsTotal.add(toBaseCurrency(currencyUnit, amount));
            }
        } else {
            if (donationsTotal == null) {
                donationsTotal = getTotalFor(TransactionType.DONATION);
            } else {
                donationsTotal = donationsTotal.add(toBaseCurrency(currencyUnit, amount));
            }

            if (isTaxDeductibleDonation) {
                updateTaxDeductibleDonationsTotal(currencyUnit, amount);
            }
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

    private TithingAccount getTithingAccountFor(final CurrencyUnit currencyUnit) {
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

        final TithingAccount tithingAccount = new TithingAccount(currencyUnit);
        addAccount(tithingAccount);

        return tithingAccount;
    }

    public CurrencyUnit getBaseCurrencyUnit() {
        return CurrencyUnit.of(baseCurrency);
    }

    public Map<String, BigDecimal> getCurrencyConversionRates() {
        return ImmutableMap.copyOf(currencyConversionRates);
    }

    public SortedSet<String> getCurrenciesInUse() {
        final SortedSet<String> availableCurrencies = new TreeSet<>();

        availableCurrencies.add(baseCurrency);
        availableCurrencies.addAll(currencyConversionRates.keySet());

        return availableCurrencies;
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
        final BigDecimal previousValue =
                currencyConversionRates.put(currencyUnit.getCode(), conversionRate);

        if (previousValue != null) {
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
        } else {
            if (liabilitiesTotal == null) {
                liabilitiesTotal = computeTotalFor(AccountType.LIABILITY);
            }
            return liabilitiesTotal;
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
        return accounts.stream()
                .filter(account -> (account instanceof TithingAccount))
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
