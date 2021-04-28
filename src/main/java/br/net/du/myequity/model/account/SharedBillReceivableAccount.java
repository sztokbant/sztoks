package br.net.du.myequity.model.account;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;

import java.math.BigDecimal;
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
@DiscriminatorValue(SharedBillReceivableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SharedBillReceivableAccount extends Account implements FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "SHARED_BILL_RECEIVABLE";

    @Column(precision = 19, scale = 2)
    @Getter
    private BigDecimal billAmount;

    @Column(nullable = true)
    @Getter
    private boolean isPaymentReceived;

    @Column @Getter private Integer numberOfPartners;

    @Column @Getter private Integer dueDay;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public SharedBillReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final FutureTithingPolicy futureTithingPolicy) {
        this(
                name,
                currencyUnit,
                futureTithingPolicy,
                LocalDate.now(),
                BigDecimal.ZERO,
                false,
                1,
                1);
    }

    public SharedBillReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaymentReceived,
            final int numberOfPartners,
            final int dueDay) {
        super(name, AccountType.ASSET, currencyUnit, createDate);

        if (numberOfPartners < 1) {
            throw new IllegalArgumentException(
                    "numberOfPartners must be greater than or equal to 1");
        }

        if (dueDay < 1 || dueDay > 31) {
            throw new IllegalArgumentException("numberOfPartners must be between 1 and 31");
        }

        this.futureTithingPolicy = futureTithingPolicy;
        this.billAmount = billAmount;
        this.isPaymentReceived = isPaymentReceived;
        this.numberOfPartners = numberOfPartners;
        this.dueDay = dueDay;
    }

    @Override
    public SharedBillReceivableAccount copy() {
        return new SharedBillReceivableAccount(
                name,
                CurrencyUnit.of(currency),
                futureTithingPolicy,
                LocalDate.now(),
                billAmount,
                isPaymentReceived,
                numberOfPartners,
                dueDay);
    }

    @Override
    public BigDecimal getBalance() {
        if (isPaymentReceived) {
            return BigDecimal.ZERO;
        }

        final BigDecimal numberOfPartners = new BigDecimal(this.numberOfPartners);
        return numberOfPartners
                .multiply(billAmount)
                .divide(
                        numberOfPartners.add(BigDecimal.ONE),
                        DIVISION_SCALE,
                        BigDecimal.ROUND_HALF_UP);
    }

    public void setBillAmount(@NonNull final BigDecimal newBillAmount) {
        if (billAmount.compareTo(newBillAmount) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        billAmount = newBillAmount;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        if (!getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        }
    }

    public void setPaymentReceived(final boolean isPaymentReceived) {
        if (this.isPaymentReceived == isPaymentReceived) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        this.isPaymentReceived = isPaymentReceived;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        if (!getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        }
    }

    public void setNumberOfPartners(final int newNumberOfPartners) {
        if (newNumberOfPartners < 1) {
            throw new IllegalArgumentException(
                    "numberOfPartners must be greater than or equal to 1");
        }

        if (numberOfPartners == newNumberOfPartners) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        numberOfPartners = newNumberOfPartners;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        if (!getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        }
    }

    public void setDueDay(final int dueDay) {
        if (dueDay < 1 || dueDay > 31) {
            throw new IllegalArgumentException("numberOfPartners must be between 1 and 31");
        }

        this.dueDay = dueDay;
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
        return getBalance();
    }
}
