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
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(PayableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PayableAccount extends Account implements BalanceUpdateable, DueDateUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "PAYABLE";

    @Column(precision = 19, scale = 8)
    @Getter
    BigDecimal balance;

    @Column @Getter @Setter private LocalDate dueDate;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public PayableAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now(), LocalDate.now(), BigDecimal.ZERO);
    }

    public PayableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal balance) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
        this.dueDate = dueDate;
        this.balance = balance;
    }

    @Override
    public PayableAccount copy() {
        return new PayableAccount(
                name, CurrencyUnit.of(currency), LocalDate.now(), dueDate, balance);
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
