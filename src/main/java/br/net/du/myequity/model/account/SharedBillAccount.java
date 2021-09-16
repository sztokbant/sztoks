package br.net.du.myequity.model.account;

import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Entity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class SharedBillAccount extends Account {

    @Column(precision = 19, scale = 2)
    @Getter
    protected BigDecimal billAmount;

    @Column(nullable = true)
    @Getter
    protected boolean isPaid;

    @Column @Getter protected Integer numberOfPartners;

    @Column @Getter protected Integer dueDay;

    public SharedBillAccount(
            @NonNull final String name,
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaid,
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

        this.billAmount = billAmount;
        this.isPaid = isPaid;
        this.numberOfPartners = numberOfPartners;
        this.dueDay = dueDay;
    }

    @Override
    public BigDecimal getBalance() {
        if (isPaid) {
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

    public void setDueDay(final int dueDay) {
        if (dueDay < 1 || dueDay > 31) {
            throw new IllegalArgumentException("numberOfPartners must be between 1 and 31");
        }

        this.dueDay = dueDay;
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

        updateFutureTithingAmount(balanceDiff);
    }

    public void setIsPaid(final boolean isPaid) {
        if (this.isPaid == isPaid) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        this.isPaid = isPaid;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        updateFutureTithingAmount(balanceDiff);
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

        updateFutureTithingAmount(balanceDiff);
    }

    protected void updateFutureTithingAmount(final BigDecimal balanceDiff) {}
}
