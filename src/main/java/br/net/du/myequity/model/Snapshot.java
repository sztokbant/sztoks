package br.net.du.myequity.model;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.util.NetWorthUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.sun.istack.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "[index]"}))
@NoArgsConstructor(access = AccessLevel.PACKAGE)
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
    @Setter
    private String name;

    @Column(name = "[index]", nullable = false)
    @Getter
    @Setter
    private Long index;

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
    private final SortedSet<AccountSnapshot> accountSnapshots = new TreeSet<>();

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural // Ref.: https://thorben-janssen.com/ordering-vs-sorting-hibernate-use/
    private final SortedSet<Transaction> transactions = new TreeSet<>();

    public Snapshot(
            final Long index,
            final String name,
            @NotNull final SortedSet<AccountSnapshot> accountSnapshots,
            @NonNull final SortedSet<Transaction> transactions) {
        this.index = index;

        this.name = name;

        accountSnapshots.stream()
                .forEach(accountSnapshot -> addAccountSnapshot(accountSnapshot.copy()));

        transactions.stream().forEach(transaction -> addTransaction(transaction.copy()));
    }

    public SortedSet<AccountSnapshot> getAccountSnapshots() {
        return ImmutableSortedSet.copyOf(accountSnapshots);
    }

    public Map<AccountType, SortedSet<AccountSnapshot>> getAccountSnapshotsByType() {
        return accountSnapshots.stream()
                .collect(
                        collectingAndThen(
                                groupingBy(
                                        accountSnapshotData ->
                                                accountSnapshotData.getAccount().getAccountType(),
                                        collectingAndThen(toSet(), ImmutableSortedSet::copyOf)),
                                ImmutableMap::copyOf));
    }

    public Optional<AccountSnapshot> getAccountSnapshotFor(@NonNull final Account account) {
        return accountSnapshots.stream()
                .filter(entry -> account.equals(entry.getAccount()))
                .findFirst();
    }

    public void addAccountSnapshot(@NonNull final AccountSnapshot accountSnapshot) {
        // Prevents infinite loop
        if (accountSnapshots.contains(accountSnapshot)) {
            return;
        }
        accountSnapshots.add(accountSnapshot);
        accountSnapshot.setSnapshot(this);
    }

    public void removeAccountSnapshotFor(@NonNull final Account account) {
        // Prevents infinite loop
        final Optional<AccountSnapshot> accountSnapshotOpt = getAccountSnapshotFor(account);
        if (!accountSnapshotOpt.isPresent()) {
            return;
        }
        removeAccountSnapshot(accountSnapshotOpt.get());
    }

    public void removeAccountSnapshot(@NonNull final AccountSnapshot accountSnapshot) {
        // Prevents infinite loop
        if (!accountSnapshots.contains(accountSnapshot)) {
            return;
        }
        accountSnapshots.remove(accountSnapshot);
        accountSnapshot.setSnapshot(null);
    }

    public SortedSet<Transaction> getTransactions() {
        return ImmutableSortedSet.copyOf(transactions);
    }

    public SortedSet<Transaction> getRecurringTransactions() {
        return ImmutableSortedSet.copyOf(
                transactions.stream()
                        .filter(Transaction::isRecurring)
                        .collect(Collectors.toCollection(() -> new TreeSet<>())));
    }

    public void addTransaction(@NonNull final Transaction transaction) {
        // Prevents infinite loop
        if (transactions.contains(transaction)) {
            return;
        }
        transactions.add(transaction);
        transaction.setSnapshot(this);
    }

    public void removeTransaction(@NonNull final Transaction transaction) {
        // Prevents infinite loop
        if (!transactions.contains(transaction)) {
            return;
        }
        transactions.remove(transaction);
        transaction.setSnapshot(null);
    }

    public void setUser(final User user) {
        // Prevents infinite loop
        if (sameAsFormer(user)) {
            return;
        }

        final User oldUser = this.user;
        this.user = user;

        if (oldUser != null) {
            oldUser.removeSnapshot(this);
        }

        if (user != null) {
            user.addSnapshot(this);
        }
    }

    private boolean sameAsFormer(final User newUser) {
        return (user == null) ? (newUser == null) : user.equals(newUser);
    }

    public Map<CurrencyUnit, BigDecimal> getNetWorth() {
        return NetWorthUtils.computeByCurrency(accountSnapshots);
    }

    public Map<CurrencyUnit, BigDecimal> getTotalForAccountType(
            @NonNull final AccountType accountType) {
        return NetWorthUtils.computeByCurrency(
                accountSnapshots.stream()
                        .filter(entry -> entry.getAccount().getAccountType().equals(accountType))
                        .collect(Collectors.toSet()));
    }

    /** Create transient CreditCardSnapshot objects aggregated by currency unit. */
    public Map<CurrencyUnit, CreditCardSnapshot> getCreditCardTotals() {
        final Map<CurrencyUnit, CreditCardSnapshot> creditCardTotals = new HashMap<>();

        for (final AccountSnapshot accountSnapshot : accountSnapshots) {
            if (!(accountSnapshot instanceof CreditCardSnapshot)) {
                continue;
            }

            final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;
            final CurrencyUnit currencyUnit = creditCardSnapshot.getAccount().getCurrencyUnit();

            if (!creditCardTotals.containsKey(currencyUnit)) {
                creditCardTotals.put(currencyUnit, creditCardSnapshot.copy());
            } else {
                final CreditCardSnapshot creditCardSnapshotForCurrency =
                        creditCardTotals.get(currencyUnit);
                updateCreditCardSnapshotForCurrency(
                        creditCardSnapshot, creditCardSnapshotForCurrency);
                creditCardTotals.put(currencyUnit, creditCardSnapshotForCurrency);
            }
        }

        return creditCardTotals;
    }

    /** Create transient CreditCardSnapshot object for specified currency unit. */
    public CreditCardSnapshot getCreditCardTotalsForCurrencyUnit(
            @NonNull final CurrencyUnit currencyUnit) {
        CreditCardSnapshot creditCardTotalsForCurrencyUnit = null;

        for (final AccountSnapshot accountSnapshot : accountSnapshots) {
            if (!(accountSnapshot instanceof CreditCardSnapshot)
                    || !accountSnapshot.getAccount().getCurrencyUnit().equals(currencyUnit)) {
                continue;
            }

            final CreditCardSnapshot creditCardSnapshot = (CreditCardSnapshot) accountSnapshot;

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
            final CreditCardSnapshot creditCardSnapshot,
            final CreditCardSnapshot creditCardSnapshotForCurrency) {
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
        return other.getIndex().compareTo(index);
    }
}
