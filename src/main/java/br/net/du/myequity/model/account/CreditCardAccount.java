package br.net.du.myequity.model.account;

import com.google.common.annotations.VisibleForTesting;
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

    @Column @Getter @Setter private BigDecimal totalCredit;

    @Column @Getter @Setter private BigDecimal availableCredit;

    @Column @Getter @Setter private BigDecimal statement;

    public CreditCardAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public CreditCardAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public CreditCardAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal totalCredit,
            @NonNull final BigDecimal availableCredit,
            @NonNull final BigDecimal statement) {
        this(name, currencyUnit, LocalDate.now(), totalCredit, availableCredit, statement);
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
    public BigDecimal getTotal() {
        return totalCredit.subtract(availableCredit);
    }

    @Override
    public CreditCardAccount copy() {
        return new CreditCardAccount(
                name, CurrencyUnit.of(currency), totalCredit, availableCredit, statement);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CreditCardAccount)) {
            return false;
        }

        final CreditCardAccount otherCreditCardSnapshot = (CreditCardAccount) other;

        return (totalCredit.compareTo(otherCreditCardSnapshot.getTotalCredit()) == 0)
                && (availableCredit.compareTo(otherCreditCardSnapshot.getAvailableCredit()) == 0)
                && (statement.compareTo(otherCreditCardSnapshot.getStatement()) == 0);
    }

    public BigDecimal getUsedCreditPercentage() {
        if (totalCredit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        final BigDecimal oneHundred = new BigDecimal("100.00");
        return (totalCredit.subtract(availableCredit))
                .multiply(oneHundred)
                .divide(totalCredit, RoundingMode.HALF_UP);
    }

    public BigDecimal getRemainingBalance() {
        return getTotal().subtract(statement);
    }
}
