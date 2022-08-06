package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.TITHING;

import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
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
@DiscriminatorValue(TithingAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TithingAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "TITHING";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal balance;

    public TithingAccount(
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal balance) {
        super(getAccountNameFor(currencyUnit), AccountType.LIABILITY, currencyUnit, createDate);
        this.balance = balance;
    }

    public static String getAccountNameFor(final CurrencyUnit currencyUnit) {
        return String.format("TITHING %s", currencyUnit.getCode());
    }

    @Override
    public TithingAccount copy() {
        return new TithingAccount(CurrencyUnit.of(currency), LocalDate.now(), balance);
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return TITHING;
    }

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
