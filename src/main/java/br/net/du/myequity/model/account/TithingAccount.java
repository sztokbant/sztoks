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
@DiscriminatorValue(TithingAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TithingAccount extends Account {

    public static final String ACCOUNT_SUB_TYPE = "TITHING";

    @Column @Getter @Setter private BigDecimal balance;

    public TithingAccount(@NonNull final CurrencyUnit currencyUnit) {
        this(currencyUnit, LocalDate.now());
    }

    public TithingAccount(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final LocalDate createDate) {
        this(currencyUnit, createDate, BigDecimal.ZERO);
    }

    public TithingAccount(
            @NonNull final CurrencyUnit currencyUnit, @NonNull final BigDecimal balance) {
        this(currencyUnit, LocalDate.now(), balance);
    }

    public TithingAccount(
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal balance) {
        super(
                getTithingAccountNameFor(currencyUnit),
                AccountType.LIABILITY,
                currencyUnit,
                createDate);
        this.balance = balance;
    }

    public static String getTithingAccountNameFor(final CurrencyUnit currencyUnit) {
        return String.format("TITHING %s", currencyUnit.getCode());
    }

    @Override
    public TithingAccount copy() {
        return new TithingAccount(CurrencyUnit.of(currency), balance);
    }
}
