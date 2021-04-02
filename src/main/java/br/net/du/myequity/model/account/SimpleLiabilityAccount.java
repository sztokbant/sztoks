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
@DiscriminatorValue(SimpleLiabilityAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleLiabilityAccount extends Account implements AmountUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_LIABILITY";

    @Column @Getter @Setter private BigDecimal amount;

    public SimpleLiabilityAccount(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public SimpleLiabilityAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, BigDecimal.ZERO);
    }

    public SimpleLiabilityAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal amount) {
        this(name, currencyUnit, LocalDate.now(), amount);
    }

    public SimpleLiabilityAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal amount) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public SimpleLiabilityAccount copy() {
        return new SimpleLiabilityAccount(name, CurrencyUnit.of(currency), amount);
    }
}
