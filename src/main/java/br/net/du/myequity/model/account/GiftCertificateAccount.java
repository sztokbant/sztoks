package br.net.du.myequity.model.account;

import static br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup.GIFT_CERTIFICATE;

import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Entity
@DiscriminatorValue(GiftCertificateAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GiftCertificateAccount extends Account
        implements SharesUpdatable, FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "GIFT_CERTIFICATE";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal shares;

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal currentShareValue;

    // Used by {@link br.net.du.myequity.controller.viewmodel.account.AccountFactory}
    public GiftCertificateAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            @NonNull final FutureTithingPolicy futureTithingPolicy) {
        this(
                name,
                currencyUnit,
                futureTithingPolicy,
                LocalDate.now(),
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    public GiftCertificateAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal currentShareValue) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        this.futureTithingPolicy = futureTithingPolicy;
        this.shares = shares;
        this.currentShareValue = currentShareValue;
    }

    @Override
    public BigDecimal getBalance() {
        return shares.multiply(currentShareValue);
    }

    @Override
    public GiftCertificateAccount copy() {
        return new GiftCertificateAccount(
                name,
                CurrencyUnit.of(currency),
                futureTithingPolicy,
                LocalDate.now(),
                shares,
                currentShareValue);
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return GIFT_CERTIFICATE;
    }

    @Override
    public void setShares(@NonNull final BigDecimal shares) {
        if (shares.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("shares must not be negative");
        }

        if (this.shares.compareTo(shares) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        this.shares = shares;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        if (!getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        }
    }

    @Override
    public void setCurrentShareValue(@NonNull final BigDecimal currentShareValue) {
        if (shares.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("currentShareValue must not be negative");
        }

        if (this.currentShareValue.compareTo(currentShareValue) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();

        this.currentShareValue = currentShareValue;

        final BigDecimal newBalance = getBalance();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

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
