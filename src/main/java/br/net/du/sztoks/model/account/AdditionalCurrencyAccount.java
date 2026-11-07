package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup.ADDITIONAL_CURRENCY;
import static br.net.du.sztoks.model.util.ModelConstants.DIVISION_SCALE;
import static br.net.du.sztoks.model.util.ModelConstants.ONE_HUNDRED;

import br.net.du.sztoks.model.totals.AccountSubtypeDisplayGroup;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

/**
 * Holding of an additional (non-base) currency unit — fiat or crypto.
 *
 * <ul>
 *   <li>{@code currencyUnit} — the additional currency (must have a snapshot conversion rate)
 *   <li>{@code shares} — quantity of that unit (scale 8)
 *   <li>{@code amountInvested} — cost basis in the <em>snapshot base</em> currency
 * </ul>
 *
 * <p>Market value in base is {@code snapshot.toBaseCurrency(currencyUnit, shares)}. There is no
 * stored share price; the conversion rate table is the source of truth.
 */
@Entity
@DiscriminatorValue(AdditionalCurrencyAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AdditionalCurrencyAccount extends Account implements FutureTithingCapable {

    public static final String ACCOUNT_SUB_TYPE = "ADDITIONAL_CURRENCY";

    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal shares;

    /** Cost basis in snapshot base currency (not {@link #currency}). */
    @Column(precision = 19, scale = 8)
    @Getter
    private BigDecimal amountInvested;

    public AdditionalCurrencyAccount(
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

    public AdditionalCurrencyAccount(
            @NonNull final String name,
            @NonNull final CurrencyUnit currencyUnit,
            final FutureTithingPolicy futureTithingPolicy,
            @NonNull final LocalDate createDate,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal amountInvested) {
        super(name, AccountType.ASSET, currencyUnit, createDate);
        if (shares.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("shares must not be negative");
        }
        this.futureTithingPolicy = futureTithingPolicy;
        this.shares = shares;
        this.amountInvested = amountInvested;
    }

    /** Balance in account currency units (quantity held). */
    @Override
    public BigDecimal getBalance() {
        return shares;
    }

    public BigDecimal getValueInBaseCurrency() {
        return getSnapshot().toBaseCurrency(getCurrencyUnit(), shares);
    }

    /** Average purchase price in base currency per 1 share unit. */
    public BigDecimal getAveragePurchasePrice() {
        if (shares.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amountInvested.divide(shares, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal getProfitPercentage() {
        if (amountInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        final BigDecimal valueInBase = getValueInBaseCurrency();
        return valueInBase
                .divide(amountInvested, DIVISION_SCALE, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(ONE_HUNDRED);
    }

    @Override
    public AdditionalCurrencyAccount copy() {
        return new AdditionalCurrencyAccount(
                name,
                CurrencyUnit.of(currency),
                futureTithingPolicy,
                LocalDate.now(),
                shares,
                amountInvested);
    }

    @Override
    public AccountSubtypeDisplayGroup getAccountSubtypeDisplayGroup() {
        return ADDITIONAL_CURRENCY;
    }

    public void setShares(@NonNull final BigDecimal shares) {
        if (shares.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("shares must not be negative");
        }
        if (this.shares.compareTo(shares) == 0) {
            return;
        }

        final BigDecimal oldBalance = getBalance();
        final BigDecimal oldProfitInAccountCurrency = profitInAccountCurrency();

        this.shares = shares;

        final BigDecimal newBalance = getBalance();
        final BigDecimal newProfitInAccountCurrency = profitInAccountCurrency();

        final BigDecimal balanceDiff = newBalance.subtract(oldBalance);
        getSnapshot().updateNetWorth(getAccountType(), getCurrencyUnit(), balanceDiff);

        final BigDecimal profitDiff =
                newProfitInAccountCurrency.subtract(oldProfitInAccountCurrency);
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.ALL)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), balanceDiff);
        } else if (getFutureTithingPolicy().equals(FutureTithingPolicy.PROFITS_ONLY)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), profitDiff);
        }
    }

    public void setAmountInvested(@NonNull final BigDecimal newAmountInvested) {
        if (amountInvested.compareTo(newAmountInvested) == 0) {
            return;
        }

        final BigDecimal oldProfitInAccountCurrency = profitInAccountCurrency();

        amountInvested = newAmountInvested;

        final BigDecimal newProfitInAccountCurrency = profitInAccountCurrency();
        final BigDecimal profitDiff =
                newProfitInAccountCurrency.subtract(oldProfitInAccountCurrency);
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.PROFITS_ONLY)) {
            getSnapshot().updateFutureTithingAmount(getCurrencyUnit(), profitDiff);
        }
    }

    /**
     * Profit expressed in account currency: {@code shares - amountInvested converted to account
     * currency}.
     */
    private BigDecimal profitInAccountCurrency() {
        return shares.subtract(amountInvestedToAccountCurrency());
    }

    private BigDecimal amountInvestedToAccountCurrency() {
        if (getCurrencyUnit().equals(getSnapshot().getBaseCurrencyUnit())) {
            return amountInvested;
        }
        final BigDecimal rate =
                getSnapshot().getCurrencyConversionRates().get(getCurrencyUnit().getCode());
        if (rate == null) {
            return amountInvested;
        }
        // toBase = amount / rate  =>  amount = base * rate
        return amountInvested.multiply(rate);
    }

    /**
     * After snapshot base change: scale cost basis into the new base and, if this holding's unit is
     * now the base, invert shares into {@code previousBase}.
     *
     * @param previousBase previous snapshot base
     * @param unitsOfNewBasePerOldBase how many units of the new base equal 1 unit of previousBase
     *     (the old conversion rate entry for the new base currency)
     */
    public void onSnapshotBaseCurrencyChanged(
            @NonNull final CurrencyUnit previousBase,
            @NonNull final CurrencyUnit newBase,
            @NonNull final BigDecimal unitsOfNewBasePerOldBase) {
        if (unitsOfNewBasePerOldBase.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("conversion rate must not be zero");
        }

        // amountInvested was in previousBase; express in newBase
        amountInvested =
                amountInvested
                        .multiply(unitsOfNewBasePerOldBase)
                        .setScale(DIVISION_SCALE, RoundingMode.HALF_UP);

        if (getCurrencyUnit().equals(newBase)) {
            // Holding was denominated in what is now base → invert into previous base units.
            // Rate R means 1 previousBase = R newBase, so shares_newBase / R = shares_previousBase.
            shares = shares.divide(unitsOfNewBasePerOldBase, DIVISION_SCALE, RoundingMode.HALF_UP);
            setCurrencyUnit(previousBase);
        }
    }

    @Override
    public FutureTithingPolicy getFutureTithingPolicy() {
        return futureTithingPolicy == null ? FutureTithingPolicy.NONE : futureTithingPolicy;
    }

    @Override
    public void setFutureTithingPolicy(@NonNull final FutureTithingPolicy futureTithingPolicy) {
        if (this.futureTithingPolicy != null
                && this.futureTithingPolicy.equals(futureTithingPolicy)) {
            return;
        }

        final BigDecimal oldReferenceAmount = getFutureTithingReferenceAmount();
        this.futureTithingPolicy = futureTithingPolicy;
        final BigDecimal newReferenceAmount = getFutureTithingReferenceAmount();
        getSnapshot()
                .updateFutureTithingAmount(
                        getCurrencyUnit(), newReferenceAmount.subtract(oldReferenceAmount));
    }

    @Override
    public BigDecimal getFutureTithingReferenceAmount() {
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.NONE)) {
            return BigDecimal.ZERO;
        }
        if (getFutureTithingPolicy().equals(FutureTithingPolicy.ALL)) {
            return getBalance();
        }
        // PROFITS_ONLY
        return profitInAccountCurrency();
    }
}
