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
public class ReceivableAccount extends Account
        implements BalanceUpdateable, DueDateUpdateable, FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "RECEIVABLE";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal balance;

    @Column @Getter @Setter private LocalDate dueDate;

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final FutureTithingPolicy futureTithingPolicy) {
        this(name, currencyUnit, futureTithingPolicy, LocalDate.now());
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate) {
        this(name, currencyUnit, futureTithingPolicy, createDate, LocalDate.now(), BigDecimal.ZERO);
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal balance) {
        this(name, currencyUnit, futureTithingPolicy, LocalDate.now(), dueDate, balance);
    }

    public ReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal balance) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.futureTithingPolicy = futureTithingPolicy;
        this.dueDate = dueDate;
        this.balance = balance;
    }

    @Override
    public ReceivableAccount copy() {
        return new ReceivableAccount(
                name, CurrencyUnit.of(currency), futureTithingPolicy, dueDate, balance);
    }

    @Override
    public void setBalance(final BigDecimal newBalance) {
        final BigDecimal oldBalance = balance;

        balance = newBalance;

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        if (!getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            getSnapshot()
                    .updateTithingAmount(
                            getCurrencyUnit(), balanceDiff, FutureTithingAccount.class);
        }
    }

    @Override
    public FutureTithingPolicy getFutureTithingPolicy() {
        return futureTithingPolicy == null ? FutureTithingPolicy.NONE : futureTithingPolicy;
    }

    @Override
    public BigDecimal getFutureTithingReferenceAmount() {
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            return BigDecimal.ZERO;
        }
        return getBalance();
    }
}
