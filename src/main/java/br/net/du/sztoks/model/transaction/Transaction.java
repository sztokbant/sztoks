package br.net.du.sztoks.model.transaction;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.util.SnapshotUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Transaction.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Transaction implements Comparable<Transaction> {
    static final String DISCRIMINATOR_COLUMN = "transaction_type";

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
    protected BigDecimal amount;

    @Column(nullable = false)
    @Getter
    @Setter
    protected String description;

    @Column(nullable = false)
    @Getter
    @Setter
    protected RecurrencePolicy recurrencePolicy;

    @Column protected String category;

    public Transaction(
            @NonNull final LocalDate date,
            @NonNull final String currency,
            @NonNull final BigDecimal amount,
            @NonNull final String description,
            @NonNull final RecurrencePolicy recurrencePolicy) {
        this.date = date;
        this.currency = currency;
        this.amount = amount;
        this.description = description;
        this.recurrencePolicy = recurrencePolicy;
    }

    public abstract Transaction copy();

    public abstract TransactionType getTransactionType();

    public abstract void setAmount(BigDecimal amount);

    public CurrencyUnit getCurrencyUnit() {
        return CurrencyUnit.of(currency);
    }

    public void setSnapshot(final Snapshot newSnapshot) {
        // Prevents infinite loop
        if (SnapshotUtils.equals(snapshot, newSnapshot)) {
            return;
        }

        final Snapshot oldSnapshot = snapshot;
        snapshot = newSnapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeTransaction(this);
        }

        if (newSnapshot != null) {
            newSnapshot.addTransaction(this);
        }
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
    public int compareTo(@NonNull final Transaction other) {
        if (currency.equals(other.getCurrency())) {
            if (date.equals(other.getDate())) {
                if (description.equals(other.getDescription())) {
                    return id.compareTo(other.getId());
                }
                return description.compareTo(other.getDescription());
            }
            return date.compareTo(other.getDate());
        }
        return currency.compareTo(other.getCurrency());
    }

    protected void updateSnapshotTransactionTotal(
            @NonNull final BigDecimal newAmount, @NonNull final BigDecimal oldAmount) {
        updateSnapshotTransactionTotal(newAmount, oldAmount, false);
    }

    protected void updateSnapshotTransactionTotal(
            @NonNull final BigDecimal newAmount,
            @NonNull final BigDecimal oldAmount,
            final boolean isTaxDeductibleDonation) {
        final BigDecimal diffAmount = newAmount.subtract(oldAmount);
        snapshot.updateTransactionsTotal(
                getTransactionType(), getCurrencyUnit(), diffAmount, isTaxDeductibleDonation);
    }
}
