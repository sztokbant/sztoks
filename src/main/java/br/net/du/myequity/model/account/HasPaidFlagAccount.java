package br.net.du.myequity.model.account;

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
public abstract class HasPaidFlagAccount extends Account {

    @Column(nullable = true)
    @Getter
    protected boolean isPaid;

    public HasPaidFlagAccount(
            @NonNull final String name,
            @NonNull final AccountType accountType,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            final boolean isPaid) {
        super(name, accountType, currencyUnit, createDate);
        this.isPaid = isPaid;
    }

    @Override
    public BigDecimal getBalance() {
        if (isPaid) {
            return BigDecimal.ZERO;
        }

        return getNominalBalance();
    }

    protected abstract BigDecimal getNominalBalance();

    public void setIsPaid(final boolean isPaid) {
        // TODO: Why is this.isPaid nullable? It appears that this would not behave correctly if
        // this.isPaid == null, and isPaid == false.
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

    protected void updateFutureTithingAmount(final BigDecimal balanceDiff) {}
}
