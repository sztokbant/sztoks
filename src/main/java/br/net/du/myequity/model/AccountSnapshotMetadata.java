package br.net.du.myequity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "account_snapshot_metadata",
        uniqueConstraints = @UniqueConstraint(columnNames = {"snapshot_id", "account_id"}))
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AccountSnapshotMetadata implements Comparable<AccountSnapshotMetadata> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter // for testing
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private Snapshot snapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private Account account;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    // ASSET PROPERTIES

    @Column(nullable = true)
    @Getter
    @Setter
    private BigDecimal shares;

    @Column(nullable = true)
    @Getter
    @Setter
    private BigDecimal initialInvestment;

    // LIABILITY PROPERTIES

    @Column(nullable = true)
    @Getter
    @Setter
    private BigDecimal totalCreditAvailable;

    @Column(nullable = true)
    @Getter
    @Setter
    private BigDecimal currentCreditAvailable;

    @Column(nullable = true)
    @Getter
    @Setter
    private BigDecimal lastStatementBalance;

    public AccountSnapshotMetadata(final Account account, final BigDecimal amount) {
        this.account = account;
        this.amount = amount;
    }

    public BigDecimal getTotal() {
        if (shares == null && (totalCreditAvailable == null || currentCreditAvailable == null)) {
            return amount;
        } else if (shares != null) {
            return amount.multiply(shares);
        } else {
            return totalCreditAvailable.subtract(currentCreditAvailable);
        }
    }

    public void setSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (sameAsFormer(snapshot)) {
            return;
        }

        final Snapshot oldSnapshot = this.snapshot;
        this.snapshot = snapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeAccountSnapshotMetadataFor(this.getAccount());
        }

        if (snapshot != null) {
            snapshot.addAccountSnapshotMetadata(this);
        }
    }

    private boolean sameAsFormer(final Snapshot newSnapshot) {
        return snapshot == null ?
                newSnapshot == null :
                snapshot.equals(newSnapshot);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AccountSnapshotMetadata)) {
            return false;
        }

        return id != null && id.equals(((AccountSnapshotMetadata) other).getId());
    }

    @Override
    public int hashCode() {
        return 53;
    }

    @Override
    public int compareTo(final AccountSnapshotMetadata other) {
        return this.getAccount().compareTo(other.getAccount());
    }
}
