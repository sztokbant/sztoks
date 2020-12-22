package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.PayableAccount;
import com.google.common.annotations.VisibleForTesting;
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

@Entity
@DiscriminatorValue(PayableAccount.ACCOUNT_SUB_TYPE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PayableSnapshot extends AccountSnapshot
        implements AmountUpdateable, DueDateUpdateable {

    @Column @Getter @Setter private BigDecimal amount;

    @Column @Getter @Setter private LocalDate dueDate;

    public PayableSnapshot(
            @NonNull final Account account,
            @NonNull final LocalDate dueDate,
            @NonNull final BigDecimal amount) {
        super(account);
        this.dueDate = dueDate;
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public PayableSnapshot copy() {
        return new PayableSnapshot(account, dueDate, amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PayableSnapshot)) {
            return false;
        }

        final PayableSnapshot otherPayableSnapshot = (PayableSnapshot) other;
        return account.equals(otherPayableSnapshot.getAccount())
                && amount.compareTo(otherPayableSnapshot.getAmount()) == 0;
    }
}
