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
@DiscriminatorValue(SimpleAssetAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleAssetAccount extends Account implements AmountUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "SIMPLE_ASSET";

    @Column @Getter @Setter private BigDecimal balance;

    public SimpleAssetAccount(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public SimpleAssetAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, BigDecimal.ZERO);
    }

    public SimpleAssetAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final BigDecimal balance) {
        this(name, currencyUnit, LocalDate.now(), balance);
    }

    public SimpleAssetAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal balance) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.balance = balance;
    }

    @Override
    public SimpleAssetAccount copy() {
        return new SimpleAssetAccount(name, CurrencyUnit.of(currency), balance);
    }
}
