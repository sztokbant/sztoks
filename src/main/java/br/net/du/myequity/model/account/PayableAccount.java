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
public class PayableAccount extends BillAccount implements DueDateUpdateable {

    public static final String ACCOUNT_SUB_TYPE = "PAYABLE";

    @Column @Getter @Setter private LocalDate dueDate;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public PayableAccount(@NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        this(name, currencyUnit, LocalDate.now(), LocalDate.now(), BigDecimal.ZERO, false);
    }

    public PayableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaid) {
        super(name, AccountType.LIABILITY, currencyUnit, createDate, billAmount, isPaid);
        this.dueDate = dueDate;
    }

    @Override
    public PayableAccount copy() {
        return new PayableAccount(
                name, CurrencyUnit.of(currency), LocalDate.now(), dueDate, billAmount, isPaid);
    }

    @Override
    public BigDecimal getBalance() {
        return isPaid ? BigDecimal.ZERO : getBillAmount();
    }
}
