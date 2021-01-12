package br.net.du.myequity.model.transaction;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
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

@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Account.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Transaction implements Comparable<Transaction> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private Snapshot snapshot;

    @Column @Getter @Setter protected LocalDate date;

    @Column(nullable = false)
    @Getter
    protected String currency;

    @Column(nullable = false)
    @Getter
    @Setter
    protected BigDecimal amount;

    @Column(nullable = false)
    @Getter
    @Setter
    protected String description;

    @Column @Getter @Setter protected boolean isRecurring;

    public Transaction(
            final LocalDate date,
            final String currency,
            final BigDecimal amount,
            final String description,
            final boolean isRecurring) {
        this.date = date;
        this.currency = currency;
        this.amount = amount;
        this.description = description;
        this.isRecurring = isRecurring;
    }

    public abstract Transaction copy();

    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Transaction)) {
            return false;
        }

        final Transaction otherTransaction = (Transaction) other;

        return description.equals(otherTransaction.getDescription())
                && date.equals(otherTransaction.getDate())
                && currency.equals(otherTransaction.getCurrency())
                && amount.equals(otherTransaction.getAmount())
                && isRecurring == otherTransaction.isRecurring();
    }

    public void setSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (sameAsFormer(snapshot)) {
            return;
        }

        final Snapshot oldSnapshot = this.snapshot;
        this.snapshot = snapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeTransaction(this);
        }

        if (snapshot != null) {
            snapshot.addTransaction(this);
        }
    }

    private boolean sameAsFormer(final Snapshot newSnapshot) {
        return (snapshot == null) ? (newSnapshot == null) : snapshot.equals(newSnapshot);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Transaction)) {
            return false;
        }

        final Transaction otherTransaction = (Transaction) other;

        return (id != null) && id.equals(otherTransaction.getId());
    }

    @Override
    public int hashCode() {
        return 61;
    }

    @Override
    public int compareTo(final Transaction other) {
        if (currency.equals(other.getCurrency())) {
            if (date.equals(other.getDate())) {
                return description.compareTo(other.getDescription());
            }
            return date.compareTo(other.getDate());
        }
        return currency.compareTo(other.getCurrency());
    }
}
