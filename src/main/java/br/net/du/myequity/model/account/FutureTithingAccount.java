package br.net.du.myequity.model.account;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.myequity.model.util.ModelConstants.ONE_HUNDRED;

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
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(FutureTithingAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class FutureTithingAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "FUTURE_TITHING";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal referenceAmount;

    public FutureTithingAccount(@NonNull final CurrencyUnit currencyUnit) {
        this(currencyUnit, LocalDate.now());
    }

    public FutureTithingAccount(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final LocalDate createDate) {
        this(currencyUnit, createDate, BigDecimal.ZERO);
    }

    public FutureTithingAccount(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal referenceAmount) {
        this(currencyUnit, LocalDate.now(), referenceAmount);
    }

    public FutureTithingAccount(
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal referenceAmount) {
        super(getAccountNameFor(currencyUnit), AccountType.LIABILITY, currencyUnit, createDate);
        this.referenceAmount = referenceAmount;
    }

    public static String getAccountNameFor(final CurrencyUnit currencyUnit) {
        return String.format("FUTURE TITHING %s", currencyUnit.getCode());
    }

    @Override
    public FutureTithingAccount copy() {
        return new FutureTithingAccount(CurrencyUnit.of(currency), referenceAmount);
    }

    public void setReferenceAmount(final BigDecimal newReferenceAmount) {
        final BigDecimal oldBalance = getBalance();

        referenceAmount = newReferenceAmount;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);
    }

    @Override
    public BigDecimal getBalance() {
        return referenceAmount
                .multiply(getSnapshot().getDefaultTithingPercentage())
                .divide(ONE_HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP);
    }
}
