package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payable_snapshots")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PayableSnapshot extends AccountSnapshot {

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate dueDate;

    public PayableSnapshot(@NonNull final Account account,
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
        return account.equals(otherPayableSnapshot.getAccount()) &&
                amount.compareTo(otherPayableSnapshot.getAmount()) == 0;
    }
}
