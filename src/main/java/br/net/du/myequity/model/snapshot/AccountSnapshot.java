package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "account_snapshots", uniqueConstraints = @UniqueConstraint(columnNames = {"snapshot_id", "account_id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Account.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AccountSnapshot implements Comparable<AccountSnapshot> {
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
    Account account;

    public AccountSnapshot(@NonNull final Account account) {
        this.account = account;
    }

    public abstract BigDecimal getTotal();

    public abstract AccountSnapshot copy();

    @VisibleForTesting
    public abstract boolean equalsIgnoreId(final Object other);

    public void setSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (sameAsFormer(snapshot)) {
            return;
        }

        final Snapshot oldSnapshot = this.snapshot;
        this.snapshot = snapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeAccountSnapshot(this);
        }

        if (snapshot != null) {
            snapshot.addAccountSnapshot(this);
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

        if (!(other instanceof AccountSnapshot)) {
            return false;
        }

        return id != null && id.equals(((AccountSnapshot) other).getId());
    }

    @Override
    public int hashCode() {
        return 53;
    }

    @Override
    public int compareTo(final AccountSnapshot other) {
        return this.getAccount().compareTo(other.getAccount());
    }
}
