package br.net.du.myequity.model.account;

import static br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup.SHARED_BILL_RECEIVABLE;
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
@DiscriminatorValue(SharedBillReceivableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SharedBillReceivableAccount extends SharedBillAccount implements FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "SHARED_BILL_RECEIVABLE";

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public SharedBillReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final FutureTithingPolicy futureTithingPolicy) {
        super(name, AccountType.ASSET, currencyUnit, LocalDate.now(), BigDecimal.ZERO, false, 1, 1);
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public SharedBillReceivableAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal billAmount,
            final boolean isPaid,
            final int numberOfPartners,
            final int dueDay) {
        this(name, currencyUnit, futureTithingPolicy);

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
    public SharedBillReceivableAccount copy() {
        return new SharedBillReceivableAccount(
                name,
                CurrencyUnit.of(currency),
                futureTithingPolicy,
                LocalDate.now(),
                getBillAmount(),
                isPaid(),
                getNumberOfPartners(),
                getDueDay());
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return SHARED_BILL_RECEIVABLE;
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

    @Override
    protected void updateFutureTithingAmount(final BigDecimal balanceDiff) {
        if (!getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        }
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
