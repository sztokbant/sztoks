package br.net.du.myequity.model.account;

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
@DiscriminatorValue(SimpleLiabilityAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleLiabilityAccount extends Account implements BalanceUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_LIABILITY";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal balance;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public SimpleLiabilityAccount(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now(), BigDecimal.ZERO);
    }

    public SimpleLiabilityAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal balance) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
        this.balance = balance;
    }

    @Override
    public SimpleLiabilityAccount copy() {
        return new SimpleLiabilityAccount(
                name, CurrencyUnit.of(currency), LocalDate.now(), balance);
    }

    @Override
    public void setBalance(@NonNull final BigDecimal newBalance) {
        if (balance.compareTo(newBalance) == 0) {
            return;
        }

        final BigDecimal oldBalance = balance;

        balance = newBalance;

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);
    }
}
