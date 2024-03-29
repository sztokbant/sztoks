package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.PAYABLE;

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
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(PayableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PayableAccount extends BillAccount implements DueDateUpdatable {

    public static final String ACCOUNT_SUB_TYPE = "PAYABLE";

    @Column @Getter @Setter private LocalDate dueDate;

    // Used by {@link br.net.du.sztoks.controller.viewmodel.account.AccountFactory}
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
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return PAYABLE;
    }

    @Override
    public BigDecimal getBalance() {
        return isPaid ? BigDecimal.ZERO : getBillAmount();
    }
}
