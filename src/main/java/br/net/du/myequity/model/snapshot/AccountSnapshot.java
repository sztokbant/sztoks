package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.util.SnapshotUtils;
import com.google.common.annotations.VisibleForTesting;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@Table(name = "account_snapshots")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = AccountSnapshot.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AccountSnapshot implements Comparable<AccountSnapshot> {
    public static final String DISCRIMINATOR_COLUMN = "account_sub_type";

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

    AccountSnapshot(
            final String name,
            final AccountType accountType,
            final CurrencyUnit currencyUnit,
            final LocalDate createDate) {
        this.name = name;
        this.accountType = accountType;
        this.currency = currencyUnit.getCode();
        this.createDate = createDate;
    }

    AccountSnapshot(
            final String name, final AccountType accountType, final CurrencyUnit currencyUnit) {
        this(name, accountType, currencyUnit, LocalDate.now());
    }

    public CurrencyUnit getCurrencyUnit() {
        return CurrencyUnit.of(currency);
    }

    public void setCurrencyUnit(final CurrencyUnit currencyUnit) {
        currency = currencyUnit.getCode();
    }

    public abstract BigDecimal getTotal();

    public abstract AccountSnapshot copy();

    @VisibleForTesting
    public abstract boolean equalsIgnoreId(final Object other);

    public void setSnapshot(final Snapshot newSnapshot) {
        // Prevents infinite loop
        if (SnapshotUtils.equals(snapshot, newSnapshot)) {
            return;
        }

        final Snapshot oldSnapshot = snapshot;
        snapshot = newSnapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeAccountSnapshot(this);
        }

        if (newSnapshot != null) {
            newSnapshot.addAccountSnapshot(this);
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AccountSnapshot)) {
            return false;
        }

        if (id == null) {
            return false;
        }

        final AccountSnapshot otherAccountSnapshot = (AccountSnapshot) other;

        return id.equals(otherAccountSnapshot.getId());
    }

    @Override
    public int hashCode() {
        return 53;
    }

    @Override
    public int compareTo(final AccountSnapshot other) {
        return name.compareTo(other.getName());
    }
}
