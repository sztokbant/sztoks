package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.account.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class SimpleAssetSnapshot extends AccountSnapshot {

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    public SimpleAssetSnapshot(final Account account, final BigDecimal amount) {
        super(account);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }
}
