package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import com.google.common.annotations.VisibleForTesting;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@DiscriminatorValue(SimpleLiabilityAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleLiabilitySnapshot extends AccountSnapshot implements AmountUpdateable {

    @Column @Getter @Setter private BigDecimal amount;

    public SimpleLiabilitySnapshot(
            @NonNull final Account account, @NonNull final BigDecimal amount) {
        super(account);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public SimpleLiabilitySnapshot copy() {
        return new SimpleLiabilitySnapshot(account, amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SimpleLiabilitySnapshot)) {
            return false;
        }

        final SimpleLiabilitySnapshot otherSimpleLiabilitySnapshot =
                (SimpleLiabilitySnapshot) other;

        return account.equals(otherSimpleLiabilitySnapshot.getAccount())
                && (amount.compareTo(otherSimpleLiabilitySnapshot.getAmount()) == 0);
    }
}
