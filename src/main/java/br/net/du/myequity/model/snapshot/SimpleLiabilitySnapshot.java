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
@DiscriminatorValue(SimpleLiabilitySnapshot.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleLiabilitySnapshot extends AccountSnapshot implements AmountUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_LIABILITY";

    @Column @Getter @Setter private BigDecimal amount;

    public SimpleLiabilitySnapshot(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public SimpleLiabilitySnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, BigDecimal.ZERO);
    }

    public SimpleLiabilitySnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal amount) {
        this(name, currencyUnit, LocalDate.now(), amount);
    }

    public SimpleLiabilitySnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal amount) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public SimpleLiabilitySnapshot copy() {
        return new SimpleLiabilitySnapshot(name, CurrencyUnit.of(currency), amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SimpleLiabilitySnapshot)) {
            return false;
        }

        final SimpleLiabilitySnapshot otherSimpleLiabilitySnapshot =
                (SimpleLiabilitySnapshot) other;

        return (amount.compareTo(otherSimpleLiabilitySnapshot.getAmount()) == 0);
    }
}
