package br.net.du.myequity.model.account;

import com.google.common.annotations.VisibleForTesting;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
@DiscriminatorValue(InvestmentAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "INVESTMENT";

    @Column @Getter @Setter private BigDecimal shares;

    @Column @Getter @Setter private BigDecimal originalShareValue;

    @Column @Getter @Setter private BigDecimal currentShareValue;

    public InvestmentAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public InvestmentAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public InvestmentAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal originalShareValue,
            @NonNull final BigDecimal currentShareValue) {
        this(name, currencyUnit, LocalDate.now(), shares, originalShareValue, currentShareValue);
    }

    public InvestmentAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal originalShareValue,
            @NonNull final BigDecimal currentShareValue) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
    }

    @Override
    public BigDecimal getTotal() {
        return shares.multiply(currentShareValue);
    }

    @Override
    public InvestmentAccount copy() {
        return new InvestmentAccount(
                name, CurrencyUnit.of(currency), shares, originalShareValue, currentShareValue);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof InvestmentAccount)) {
            return false;
        }

        final InvestmentAccount otherInvestmentSnapshot = (InvestmentAccount) other;

        return (shares.compareTo(otherInvestmentSnapshot.getShares()) == 0)
                && (originalShareValue.compareTo(otherInvestmentSnapshot.getOriginalShareValue())
                        == 0)
                && (currentShareValue.compareTo(otherInvestmentSnapshot.getCurrentShareValue())
                        == 0);
    }

    public BigDecimal getProfitPercentage() {
        if (originalShareValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        final BigDecimal oneHundred = new BigDecimal("100.00");
        return (currentShareValue
                        .multiply(oneHundred)
                        .divide(originalShareValue, RoundingMode.HALF_UP))
                .subtract(oneHundred);
    }
}
