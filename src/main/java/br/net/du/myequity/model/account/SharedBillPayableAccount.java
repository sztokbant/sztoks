package br.net.du.myequity.model.account;

import static br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup.SHARED_BILL_PAYABLE;
import static br.net.du.myequity.model.util.ModelConstants.DIVISION_SCALE;

import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(SharedBillPayableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SharedBillPayableAccount extends SharedBillAccount {

    public static final String ACCOUNT_SUB_TYPE = "SHARED_BILL_PAYABLE";

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public SharedBillPayableAccount(
            @NonNull final String name, @NonNull final CurrencyUnit currencyUnit) {
        super(
                name,
                AccountType.LIABILITY,
                currencyUnit,
                LocalDate.now(),
                BigDecimal.ZERO,
                false,
                1,
                1);
    }

    public SharedBillPayableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaid,
            final int numberOfPartners,
            final int dueDay) {
        this(name, currencyUnit);

        if (numberOfPartners < 1) {
            throw new IllegalArgumentException(
                    "numberOfPartners must be greater than or equal to 1");
        }

        if (dueDay < 1 || dueDay > 31) {
            throw new IllegalArgumentException("numberOfPartners must be between 1 and 31");
        }

        setCreateDate(createDate);
        this.billAmount = billAmount;
        this.isPaid = isPaid;
        this.numberOfPartners = numberOfPartners;
        this.dueDay = dueDay;
    }

    @Override
    public SharedBillPayableAccount copy() {
        return new SharedBillPayableAccount(
                name,
                CurrencyUnit.of(currency),
                LocalDate.now(),
                getBillAmount(),
                isPaid(),
                getNumberOfPartners(),
                getDueDay());
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return SHARED_BILL_PAYABLE;
    }

    @Override
    public BigDecimal getBalance() {
        if (isPaid) {
            return BigDecimal.ZERO;
        }

        final BigDecimal numberOfPartners = new BigDecimal(this.numberOfPartners);
        return numberOfPartners
                .multiply(billAmount)
                .divide(
                        numberOfPartners.add(BigDecimal.ONE),
                        DIVISION_SCALE,
                        BigDecimal.ROUND_HALF_UP);
    }
}
