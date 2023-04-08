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
public abstract class SharedBillAccount extends BillAccount {

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
        super(name, accountType, currencyUnit, createDate, billAmount, isPaid);

        if (numberOfPartners < 1) {
            throw new IllegalArgumentException(
                    "numberOfPartners must be greater than or equal to 1");
        }

        if (dueDay < 1 || dueDay > 31) {
            throw new IllegalArgumentException("numberOfPartners must be between 1 and 31");
        }

        this.numberOfPartners = numberOfPartners;
        this.dueDay = dueDay;
    }

    public void setDueDay(final int dueDay) {
        if (dueDay < 1 || dueDay > 31) {
            throw new IllegalArgumentException("numberOfPartners must be between 1 and 31");
        }

        this.dueDay = dueDay;
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
