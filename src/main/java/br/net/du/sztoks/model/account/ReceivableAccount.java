package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.RECEIVABLE;

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
import lombok.Setter;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(ReceivableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReceivableAccount extends BillAccount
        implements DueDateUpdatable, FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "RECEIVABLE";

    @Column @Getter @Setter private LocalDate dueDate;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final FutureTithingPolicy futureTithingPolicy) {
        this(
                name,
                currencyUnit,
                futureTithingPolicy,
                LocalDate.now(),
                LocalDate.now(),
                BigDecimal.ZERO,
                false);
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaid) {
        super(name, AccountType.ASSET, currencyUnit, createDate, billAmount, isPaid);
        this.futureTithingPolicy = futureTithingPolicy;
        this.dueDate = dueDate;
    }

    @Override
    public ReceivableAccount copy() {
        return new ReceivableAccount(
                name,
                CurrencyUnit.of(currency),
                futureTithingPolicy,
                LocalDate.now(),
                dueDate,
                billAmount,
                isPaid);
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return RECEIVABLE;
    }

    @Override
    public BigDecimal getBalance() {
        return isPaid ? BigDecimal.ZERO : getBillAmount();
    }

    @Override
    public FutureTithingPolicy getFutureTithingPolicy() {
        return futureTithingPolicy == null ? FutureTithingPolicy.NONE : futureTithingPolicy;
    }

    @Override
    public void setFutureTithingPolicy(@NonNull final FutureTithingPolicy futureTithingPolicy) {
        if (this.futureTithingPolicy.equals(futureTithingPolicy)) {
            return;
        }

        final BigDecimal oldReferenceAmount = getFutureTithingReferenceAmount();

        this.futureTithingPolicy = futureTithingPolicy;

        final BigDecimal newReferenceAmount = getFutureTithingReferenceAmount();

        final BigDecimal referenceAmountDiff = newReferenceAmount.subtract(oldReferenceAmount);
        getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), referenceAmountDiff);
    }

    @Override
    public BigDecimal getFutureTithingReferenceAmount() {
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            return BigDecimal.ZERO;
        }
        return getBalance();
    }
}
