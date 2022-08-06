package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.CREDIT_CARD;
import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
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
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(CreditCardAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CreditCardAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "CREDIT_CARD";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal totalCredit;

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal availableCredit;

    @Column(precision = 19, scale = 8)
    @Getter
    @Setter
    private BigDecimal statement;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public CreditCardAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(
                name,
                currencyUnit,
                LocalDate.now(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    public CreditCardAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal totalCredit,
            @NonNull final BigDecimal availableCredit,
            @NonNull final BigDecimal statement) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
        this.statement = statement;
    }

    @Override
    public BigDecimal getBalance() {
        return getBalance(totalCredit, availableCredit);
    }

    public static BigDecimal getBalance(
            @NonNull final BigDecimal totalCredit, @NonNull final BigDecimal availableCredit) {
        return totalCredit.subtract(availableCredit);
    }

    @Override
    public CreditCardAccount copy() {
        return new CreditCardAccount(
                name,
                CurrencyUnit.of(currency),
                LocalDate.now(),
                totalCredit,
                availableCredit,
                statement);
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return CREDIT_CARD;
    }

    public BigDecimal getUsedCreditPercentage() {
        return getUsedCreditPercentage(totalCredit, availableCredit);
    }

    public static BigDecimal getUsedCreditPercentage(
            @NonNull final BigDecimal totalCredit, @NonNull final BigDecimal availableCredit) {
        if (totalCredit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return (totalCredit.subtract(availableCredit))
                .multiply(ONE_HUNDRED)
                .divide(totalCredit, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal getRemainingBalance() {
        return getRemainingBalance(getBalance(), statement);
    }

    public static BigDecimal getRemainingBalance(
            @NonNull final BigDecimal balance, @NonNull final BigDecimal statement) {
        return balance.subtract(statement);
    }

    public void setTotalCredit(@NonNull final BigDecimal totalCredit) {
        if (this.totalCredit.compareTo(totalCredit) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        this.totalCredit = totalCredit;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);
    }

    public void setAvailableCredit(@NonNull final BigDecimal availableCredit) {
        if (this.availableCredit.compareTo(availableCredit) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        this.availableCredit = availableCredit;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);
    }
}
