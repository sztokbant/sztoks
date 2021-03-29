package br.net.du.myequity.model.account;

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
@DiscriminatorValue(PayableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PayableAccount extends Account implements AmountUpdateable, DueDateUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "PAYABLE";

    @Column @Getter @Setter private BigDecimal amount;

    @Column @Getter @Setter private LocalDate dueDate;

    public PayableAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public PayableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, LocalDate.now(), BigDecimal.ZERO);
    }

    public PayableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal amount) {
        this(name, currencyUnit, LocalDate.now(), dueDate, amount);
    }

    public PayableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal amount) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
        this.dueDate = dueDate;
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public PayableAccount copy() {
        return new PayableAccount(name, CurrencyUnit.of(currency), dueDate, amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PayableAccount)) {
            return false;
        }

        final PayableAccount otherPayableSnapshot = (PayableAccount) other;

        return (amount.compareTo(otherPayableSnapshot.getAmount()) == 0);
    }
}
