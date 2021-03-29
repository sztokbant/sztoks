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
@DiscriminatorValue(SimpleAssetSnapshot.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleAssetSnapshot extends AccountSnapshot implements AmountUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_ASSET";

    @Column @Getter @Setter private BigDecimal amount;

    public SimpleAssetSnapshot(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public SimpleAssetSnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, BigDecimal.ZERO);
    }

    public SimpleAssetSnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal amount) {
        this(name, currencyUnit, LocalDate.now(), amount);
    }

    public SimpleAssetSnapshot(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal amount) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public SimpleAssetSnapshot copy() {
        return new SimpleAssetSnapshot(name, CurrencyUnit.of(currency), amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SimpleAssetSnapshot)) {
            return false;
        }

        final SimpleAssetSnapshot otherSimpleAssetSnapshot = (SimpleAssetSnapshot) other;

        return (amount.compareTo(otherSimpleAssetSnapshot.getAmount()) == 0);
    }
}
