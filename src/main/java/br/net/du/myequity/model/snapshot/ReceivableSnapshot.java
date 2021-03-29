package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.AccountType;
import com.google.common.annotations.VisibleForTesting;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(ReceivableSnapshot.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReceivableSnapshot extends AccountSnapshot
        implements AmountUpdateable, DueDateUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "RECEIVABLE";

    @Column @Getter @Setter private BigDecimal amount;

    @Column @Getter @Setter private LocalDate dueDate;

    public ReceivableSnapshot(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public ReceivableSnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, LocalDate.now(), BigDecimal.ZERO);
    }

    public ReceivableSnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal amount) {
        this(name, currencyUnit, LocalDate.now(), dueDate, amount);
        this.dueDate = dueDate;
        this.amount = amount;
    }

    public ReceivableSnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal amount) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.dueDate = dueDate;
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public ReceivableSnapshot copy() {
        return new ReceivableSnapshot(name, CurrencyUnit.of(currency), dueDate, amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ReceivableSnapshot)) {
            return false;
        }

        final ReceivableSnapshot otherReceivableSnapshot = (ReceivableSnapshot) other;

        return (amount.compareTo(otherReceivableSnapshot.getAmount()) == 0);
    }
}
