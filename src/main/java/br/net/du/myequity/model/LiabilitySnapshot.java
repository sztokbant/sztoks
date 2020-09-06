package br.net.du.myequity.model;

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
@Table(name = "liability_snapshots")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LiabilitySnapshot extends AccountSnapshotMetadata {

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    public LiabilitySnapshot(final Account account, final BigDecimal amount) {
        super(account);
        this.amount = amount;
    }

    @Override
    public BigDecimal getTotal() {
        return amount;
    }
}
