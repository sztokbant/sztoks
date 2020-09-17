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

@Entity
@Table(name = "simple_asset_snapshots")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleAssetSnapshot extends AccountSnapshot implements AmountUpdateable {

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    public SimpleAssetSnapshot(@NonNull final Account account, @NonNull final BigDecimal amount) {
        super(account);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }

    @Override
    public SimpleAssetSnapshot copy() {
        return new SimpleAssetSnapshot(account, amount);
    }

    @Override
    @VisibleForTesting
    public boolean equalsIgnoreId(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SimpleAssetSnapshot)) {
            return false;
        }

        final SimpleAssetSnapshot otherSimpleAssetSnapshot = (SimpleAssetSnapshot) other;
        return account.equals(otherSimpleAssetSnapshot.getAccount()) &&
                amount.compareTo(otherSimpleAssetSnapshot.getAmount()) == 0;
    }
}
