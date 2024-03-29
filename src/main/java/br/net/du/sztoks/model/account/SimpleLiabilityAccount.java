package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.SIMPLE_LIABILITY;

import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(SimpleLiabilityAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleLiabilityAccount extends Account implements BalanceUpdatable {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_LIABILITY";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal balance;

    // Used by {@link br.net.du.sztoks.controller.viewmodel.account.AccountFactory}
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
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return SIMPLE_LIABILITY;
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
