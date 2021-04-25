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
@DiscriminatorValue(InvestmentAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentAccount extends Account implements FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "INVESTMENT";

    @Column(precision = 19, scale = 8) // Allow Satoshi scale
    @Getter
    private BigDecimal shares;

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal amountInvested;

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal currentShareValue;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public InvestmentAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final FutureTithingPolicy futureTithingPolicy) {
        this(
                name,
                currencyUnit,
                futureTithingPolicy,
                LocalDate.now(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    public InvestmentAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal amountInvested,
            @NonNull final BigDecimal currentShareValue) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.futureTithingPolicy = futureTithingPolicy;
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
                name,
                CurrencyUnit.of(currency),
                futureTithingPolicy,
                LocalDate.now(),
                shares,
                amountInvested,
                currentShareValue);
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

    public void setAmountInvested(@NonNull final BigDecimal newAmountInvested) {
        if (amountInvested.compareTo(newAmountInvested) == 0) {
            return;
        }

        final BigDecimal balance = getBalance();
        final BigDecimal oldProfit = balance.subtract(amountInvested);

        amountInvested = newAmountInvested;

        final BigDecimal newProfit = balance.subtract(amountInvested);

        final BigDecimal profitDiff = newProfit.subtract(oldProfit);
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.PROFITS_ONLY)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), profitDiff);
        }
    }

    public void setShares(@NonNull final BigDecimal shares) {
        if (this.shares.compareTo(shares) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();
        final BigDecimal oldProfit = oldBalance.subtract(amountInvested);

        this.shares = shares;

        final BigDecimal newBalance = getBalance();
        final BigDecimal newProfit = newBalance.subtract(amountInvested);

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        final BigDecimal profitDiff = newProfit.subtract(oldProfit);
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.ALL)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        } else if (getFutureTithingPolicy().equals(FutureTithingPolicy.PROFITS_ONLY)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), profitDiff);
        }
    }

    public void setCurrentShareValue(@NonNull final BigDecimal currentShareValue) {
        if (this.currentShareValue.compareTo(currentShareValue) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();
        final BigDecimal oldProfit = oldBalance.subtract(amountInvested);

        this.currentShareValue = currentShareValue;

        final BigDecimal newBalance = getBalance();
        final BigDecimal newProfit = newBalance.subtract(amountInvested);

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        final BigDecimal profitDiff = newProfit.subtract(oldProfit);
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.ALL)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        } else if (getFutureTithingPolicy().equals(FutureTithingPolicy.PROFITS_ONLY)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), profitDiff);
        }
    }

    @Override
    public FutureTithingPolicy getFutureTithingPolicy() {
        return futureTithingPolicy == null ? FutureTithingPolicy.NONE : futureTithingPolicy;
    }

    @Override
    public BigDecimal getFutureTithingReferenceAmount() {
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            return BigDecimal.ZERO;
        }

        final BigDecimal balance = getBalance();
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.ALL)) {
            return balance;
        }

        return balance.subtract(amountInvested);
    }
}
