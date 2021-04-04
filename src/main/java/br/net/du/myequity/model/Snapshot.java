package br.net.du.myequity.model;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.CreditCardAccount;
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
@Table(name = "snapshots", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
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
    private String name;

    @Column(nullable = false)
    protected String baseCurrency;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal defaultTithingPercentage;

    @OneToOne
    @JoinColumn(name = "previous_id", nullable = true)
    @Getter
    @Setter
    private Snapshot previous;

    @OneToOne
    @JoinColumn(name = "next_id", nullable = true)
    @Getter
    @Setter
    private Snapshot next;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural // Ref.: https://thorben-janssen.com/ordering-vs-sorting-hibernate-use/
    private final SortedSet<Account> accounts = new TreeSet<>();

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    // this should match Transaction::compareTo()
    @OrderBy("currency ASC, date ASC, description ASC, id ASC")
    private final List<Transaction> transactions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "snapshot_currency_conversion_rates",
            joinColumns = {@JoinColumn(name = "snapshot_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "to_currency")
    @Column(name = "conversion_rate")
    private Map<String, BigDecimal> currencyConversionRates = new HashMap<>();

    public Snapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit baseCurrencyUnit,
            @NonNull final BigDecimal defaultTithingPercentage,
            @NotNull final SortedSet<Account> accounts,
            @NonNull final List<Transaction> transactions,
            @NonNull final Map<String, BigDecimal> currencyConversionRates) {
        setName(name);
        this.baseCurrency = baseCurrencyUnit.getCode();
        this.defaultTithingPercentage = defaultTithingPercentage;

        this.currencyConversionRates.putAll(currencyConversionRates);

        accounts.stream().forEach(account -> addAccount(account.copy()));

        final String[] nameParts = name.split("-");
        final int year = Integer.parseInt(nameParts[0]);
        final int month = Integer.parseInt(nameParts[1]);

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

    public void setName(@NonNull final String name) {
        this.name = validateName(name.trim());
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
    }

    public void removeAccount(@NonNull final Account account) {
        // Prevents infinite loop
        if (!accounts.contains(account)) {
            return;
        }
        accounts.remove(account);
        account.setSnapshot(null);
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
    }

    public void updateTithingAmount(final CurrencyUnit currencyUnit, final BigDecimal plusAmount) {
        final TithingAccount tithingAccount = getTithingAccountFor(currencyUnit);
        tithingAccount.setBalance(tithingAccount.getBalance().add(plusAmount));

        if (next != null) {
            next.updateTithingAmount(currencyUnit, plusAmount);
        }
    }

    public TithingAccount getTithingAccountFor(final CurrencyUnit currencyUnit) {
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

    public boolean supports(@NonNull final CurrencyUnit currencyUnit) {
        final String currencyStr = currencyUnit.toString();
        return currencyStr.equals(baseCurrency) || currencyConversionRates.containsKey(currencyStr);
    }

    public void putCurrencyConversionRate(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal conversionRate) {
        currencyConversionRates.put(currencyUnit.getCode(), conversionRate);
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
        return accounts.stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .map(account -> toBaseCurrency(account.getCurrencyUnit(), account.getBalance()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalFor(@NonNull final TransactionType transactionType) {
        return transactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals(transactionType))
                .map(t -> toBaseCurrency(t.getCurrencyUnit(), t.getAmount()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal toBaseCurrency(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        if (currencyUnit.equals(getBaseCurrencyUnit())) {
            return amount;
        }

        return amount.divide(
                        currencyConversionRates.get(currencyUnit.getCode()), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Create transient CreditCardSnapshot objects aggregated by currency unit. */
    public Map<CurrencyUnit, CreditCardAccount> getCreditCardTotals() {
        final Map<CurrencyUnit, CreditCardAccount> creditCardTotals = new HashMap<>();

        for (final Account account : accounts) {
            if (!(account instanceof CreditCardAccount)) {
                continue;
            }

            final CreditCardAccount creditCardSnapshot = (CreditCardAccount) account;
            final CurrencyUnit currencyUnit = creditCardSnapshot.getCurrencyUnit();

            if (!creditCardTotals.containsKey(currencyUnit)) {
                creditCardTotals.put(currencyUnit, creditCardSnapshot.copy());
            } else {
                final CreditCardAccount creditCardSnapshotForCurrency =
                        creditCardTotals.get(currencyUnit);
                updateCreditCardSnapshotForCurrency(
                        creditCardSnapshot, creditCardSnapshotForCurrency);
                creditCardTotals.put(currencyUnit, creditCardSnapshotForCurrency);
            }
        }

        return creditCardTotals;
    }

    /** Create transient CreditCardSnapshot object for specified currency unit. */
    public CreditCardAccount getCreditCardTotalsForCurrencyUnit(
            @NonNull final CurrencyUnit currencyUnit) {
        CreditCardAccount creditCardTotalsForCurrencyUnit = null;

        for (final Account account : accounts) {
            if (!(account instanceof CreditCardAccount)
                    || !account.getCurrencyUnit().equals(currencyUnit)) {
                continue;
            }

            final CreditCardAccount creditCardSnapshot = (CreditCardAccount) account;

            if (creditCardTotalsForCurrencyUnit == null) {
                creditCardTotalsForCurrencyUnit = creditCardSnapshot.copy();
            } else {
                updateCreditCardSnapshotForCurrency(
                        creditCardSnapshot, creditCardTotalsForCurrencyUnit);
            }
        }

        return creditCardTotalsForCurrencyUnit;
    }

    private void updateCreditCardSnapshotForCurrency(
            final CreditCardAccount creditCardSnapshot,
            final CreditCardAccount creditCardSnapshotForCurrency) {
        creditCardSnapshotForCurrency.setAvailableCredit(
                creditCardSnapshotForCurrency
                        .getAvailableCredit()
                        .add(creditCardSnapshot.getAvailableCredit()));
        creditCardSnapshotForCurrency.setTotalCredit(
                creditCardSnapshotForCurrency
                        .getTotalCredit()
                        .add(creditCardSnapshot.getTotalCredit()));
        creditCardSnapshotForCurrency.setStatement(
                creditCardSnapshotForCurrency
                        .getStatement()
                        .add(creditCardSnapshot.getStatement()));
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
        return other.getName().compareTo(name);
    }
}
