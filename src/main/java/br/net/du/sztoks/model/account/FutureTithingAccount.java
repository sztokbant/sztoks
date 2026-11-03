package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.TITHING;
import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    public static final BigDecimal MIN_REFERENCE =
            BigDecimal.ONE.movePointLeft(DIVISION_SCALE); // 0.00000001

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal referenceAmount;

    public FutureTithingAccount(
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal referenceAmount) {
        super(getAccountNameFor(currencyUnit), AccountType.LIABILITY, currencyUnit, createDate);
        this.referenceAmount = referenceAmount;
    }

    public static String getAccountNameFor(@NonNull final CurrencyUnit currencyUnit) {
        return String.format("FUTURE TITHING %s", currencyUnit.getCode());
    }

    @Override
    public FutureTithingAccount copy() {
        return new FutureTithingAccount(
                CurrencyUnit.of(currency), LocalDate.now(), referenceAmount);
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return TITHING;
    }

    private static BigDecimal normalizeReference(@NonNull final BigDecimal amount) {
        final BigDecimal scaled = amount.setScale(DIVISION_SCALE, RoundingMode.HALF_UP);
        return scaled.abs().compareTo(MIN_REFERENCE) < 0 ? BigDecimal.ZERO : scaled;
    }

    public void setReferenceAmount(@NonNull final BigDecimal newReferenceAmount) {
        final BigDecimal normalized = normalizeReference(newReferenceAmount);

        if (referenceAmount.compareTo(normalized) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        referenceAmount = normalized;

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
