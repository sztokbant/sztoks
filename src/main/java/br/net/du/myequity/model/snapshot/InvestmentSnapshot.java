package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import com.google.common.annotations.VisibleForTesting;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@DiscriminatorValue(InvestmentAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class InvestmentSnapshot extends AccountSnapshot {

    @Column @Getter @Setter private BigDecimal shares;

    @Column @Getter @Setter private BigDecimal originalShareValue;

    @Column @Getter @Setter private BigDecimal currentShareValue;

    public InvestmentSnapshot(
            @NonNull final Account account,
            @NonNull final BigDecimal shares,
            @NonNull final BigDecimal originalShareValue,
            @NonNull final BigDecimal currentShareValue) {
        super(account);
        this.shares = shares;
        this.originalShareValue = originalShareValue;
        this.currentShareValue = currentShareValue;
    }

    @Override
    public BigDecimal getTotal() {
        return shares.multiply(currentShareValue);
    }

    @Override
    public InvestmentSnapshot copy() {
        return new InvestmentSnapshot(account, shares, originalShareValue, currentShareValue);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof InvestmentSnapshot)) {
            return false;
        }

        final InvestmentSnapshot otherInvestmentSnapshot = (InvestmentSnapshot) other;
        return account.equals(otherInvestmentSnapshot.getAccount())
                && (shares.compareTo(otherInvestmentSnapshot.shares) == 0)
                && (originalShareValue.compareTo(otherInvestmentSnapshot.originalShareValue) == 0)
                && (currentShareValue.compareTo(otherInvestmentSnapshot.currentShareValue) == 0);
    }

    public BigDecimal getProfitPercentage() {
        if (originalShareValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        final BigDecimal oneHundred = new BigDecimal("100.00");
        return (currentShareValue
                        .multiply(oneHundred)
                        .divide(originalShareValue, RoundingMode.HALF_UP))
                .subtract(oneHundred);
    }
}
