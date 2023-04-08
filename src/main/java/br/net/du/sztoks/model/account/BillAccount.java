package br.net.du.sztoks.model.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Entity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class BillAccount extends Account {

    @Column(precision = 19, scale = 8)
    @Getter
    protected BigDecimal billAmount;

    @Column(nullable = true)
    @Getter
    protected boolean isPaid;

    public BillAccount(
            @NonNull final String name,
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaid) {
        super(name, accountType, currencyUnit, createDate);
        this.billAmount = billAmount;
        this.isPaid = isPaid;
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

    public void setBillAmount(@NonNull final BigDecimal newBillAmount) {
        if (billAmount.compareTo(newBillAmount) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        billAmount = newBillAmount;

        if (!isPaid) {
            final BigDecimal newBalance = getBalance();

            final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
            getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

            updateFutureTithingAmount(balanceDiff);
        }
    }

    protected void updateFutureTithingAmount(final BigDecimal balanceDiff) {}
}
