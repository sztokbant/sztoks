package br.net.du.sztoks.model.account;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
import br.net.du.sztoks.model.util.SnapshotUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@Table(
        name = "accounts",
        uniqueConstraints =
                @UniqueConstraint(
                        columnNames = {"snapshot_id", Account.DISCRIMINATOR_COLUMN, "name"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Account.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Account implements Comparable<Account> {
    public static final String DISCRIMINATOR_COLUMN = "account_subtype";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter // for testing
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private Snapshot snapshot;

    @Column(nullable = false)
    @Getter
    @Setter
    protected String name;

    @Column(nullable = false)
    @Getter
    @Setter
    private AccountType accountType;

    @Column(nullable = false)
    protected String currency;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate createDate;

    @Column(nullable = true)
    protected FutureTithingPolicy futureTithingPolicy;

    Account(
            @NonNull final String name,
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this.name = name;
        this.accountType = accountType;
        setCurrencyUnit(currencyUnit);
        this.createDate = createDate;
    }

    Account(
            @NonNull final String name,
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit) {
        this(name, accountType, currencyUnit, LocalDate.now());
    }

    public CurrencyUnit getCurrencyUnit() {
        return CurrencyUnit.of(currency);
    }

    public void setCurrencyUnit(@NonNull final CurrencyUnit currencyUnit) {
        currency = currencyUnit.getCode();
    }

    public abstract BigDecimal getBalance();

    public abstract Account copy();

    public void setSnapshot(final Snapshot newSnapshot) {
        // Prevents infinite loop
        if (SnapshotUtils.equals(snapshot, newSnapshot)) {
            return;
        }

        final Snapshot oldSnapshot = snapshot;
        snapshot = newSnapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeAccount(this);
        }

        if (newSnapshot != null) {
            newSnapshot.addAccount(this);
        }
    }

    public abstract AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup();

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Account)) {
            return false;
        }

        if (id == null) {
            return false;
        }

        final Account otherAccount = (Account) other;

        return id.equals(otherAccount.getId());
    }

    @Override
    public int hashCode() {
        return 53;
    }

    @Override
    public int compareTo(@NonNull final Account other) {
        final String thisClass = getClass().getSimpleName();
        final String otherClass = other.getClass().getSimpleName();

        if (thisClass.equals(otherClass)) {
            return name.compareTo(other.getName());
        }

        return thisClass.compareTo(otherClass);
    }
}
