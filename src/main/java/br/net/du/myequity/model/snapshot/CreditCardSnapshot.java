package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.CreditCardAccount;
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
@DiscriminatorValue(CreditCardAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CreditCardSnapshot extends AccountSnapshot {

    @Column @Getter @Setter private BigDecimal totalCredit;

    @Column @Getter @Setter private BigDecimal availableCredit;

    @Column @Getter @Setter private BigDecimal statement;

    public CreditCardSnapshot(
            @NonNull final Account account,
            @NonNull final BigDecimal totalCredit,
            @NonNull final BigDecimal availableCredit,
            @NonNull final BigDecimal statement) {
        super(account);
        this.totalCredit = totalCredit;
        this.availableCredit = availableCredit;
        this.statement = statement;
    }

    @Override
    public BigDecimal getTotal() {
        return totalCredit.subtract(availableCredit);
    }

    @Override
    public CreditCardSnapshot copy() {
        return new CreditCardSnapshot(account, totalCredit, availableCredit, statement);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CreditCardSnapshot)) {
            return false;
        }

        final CreditCardSnapshot otherCreditCardSnapshot = (CreditCardSnapshot) other;

        return account.equals(otherCreditCardSnapshot.getAccount())
                && (totalCredit.compareTo(otherCreditCardSnapshot.getTotalCredit()) == 0)
                && (availableCredit.compareTo(otherCreditCardSnapshot.getAvailableCredit()) == 0)
                && (statement.compareTo(otherCreditCardSnapshot.getStatement()) == 0);
    }

    public BigDecimal getUsedCreditPercentage() {
        if (totalCredit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        final BigDecimal oneHundred = new BigDecimal("100.00");
        return (totalCredit.subtract(availableCredit))
                .multiply(oneHundred)
                .divide(totalCredit, RoundingMode.HALF_UP);
    }

    public BigDecimal getRemainingBalance() {
        return getTotal().subtract(statement);
    }
}
