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
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(InvestmentAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "INVESTMENT";

    @Column(precision = 19, scale = 8) // Allow Satoshi scale
    @Getter
    private BigDecimal shares;

    @Column @Getter @Setter private BigDecimal amountInvested;

    @Column @Getter private BigDecimal currentShareValue;

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
            @NonNull final BigDecimal amountInvested,
            @NonNull final BigDecimal currentShareValue) {
        this(name, currencyUnit, LocalDate.now(), shares, amountInvested, currentShareValue);
    }

    public InvestmentAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal amountInvested,
            @NonNull final BigDecimal currentShareValue) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.shares = shares;
        this.amountInvested = amountInvested;
        this.currentShareValue = currentShareValue;
    }

    @Override
    public BigDecimal getBalance() {
        return shares.multiply(currentShareValue);
    }

    @Override
    public InvestmentAccount copy() {
        return new InvestmentAccount(
                name, CurrencyUnit.of(currency), shares, amountInvested, currentShareValue);
    }

    public BigDecimal getProfitPercentage() {
        final BigDecimal averagePurchasePrice = getAveragePurchasePrice();

        if (averagePurchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return (currentShareValue
                        .multiply(ONE_HUNDRED)
                        .divide(averagePurchasePrice, DIVISION_SCALE, RoundingMode.HALF_UP))
                .subtract(ONE_HUNDRED);
    }

    public BigDecimal getAveragePurchasePrice() {
        if (shares.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amountInvested.divide(shares, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    public void setShares(final BigDecimal shares) {
        final BigDecimal oldBalance = getBalance();

        this.shares = shares;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);
    }

    public void setCurrentShareValue(final BigDecimal currentShareValue) {
        final BigDecimal oldBalance = getBalance();

        this.currentShareValue = currentShareValue;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);
    }
}
