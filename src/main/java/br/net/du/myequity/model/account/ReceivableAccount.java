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
@DiscriminatorValue(ReceivableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReceivableAccount extends Account implements AmountUpdateable, DueDateUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "RECEIVABLE";

    @Column @Getter @Setter private BigDecimal balance;

    @Column @Getter @Setter private LocalDate dueDate;

    public ReceivableAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now());
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, createDate, LocalDate.now(), BigDecimal.ZERO);
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal balance) {
        this(name, currencyUnit, LocalDate.now(), dueDate, balance);
        this.dueDate = dueDate;
        this.balance = balance;
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal balance) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.dueDate = dueDate;
        this.balance = balance;
    }

    @Override
    public ReceivableAccount copy() {
        return new ReceivableAccount(name, CurrencyUnit.of(currency), dueDate, balance);
    }
}
