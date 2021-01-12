package br.net.du.myequity.model.snapshot;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.util.SnapshotUtils;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "account_snapshots")
@IdClass(AccountSnapshot.PK.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Account.DISCRIMINATOR_COLUMN)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AccountSnapshot implements Comparable<AccountSnapshot> {
    public static class PK implements Serializable {
        private Long account;
        private Long snapshot;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    Account account;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private Snapshot snapshot;

    public AccountSnapshot(@NonNull final Account account) {
        this.account = account;
    }

    public abstract BigDecimal getTotal();

    public abstract AccountSnapshot copy();

    @VisibleForTesting
    public abstract boolean equalsIgnoreId(final Object other);

    public void setSnapshot(final Snapshot newSnapshot) {
        // Prevents infinite loop
        if (SnapshotUtils.equals(snapshot, newSnapshot)) {
            return;
        }

        final Snapshot oldSnapshot = snapshot;
        snapshot = newSnapshot;

        if (oldSnapshot != null) {
            oldSnapshot.removeAccountSnapshot(this);
        }

        if (newSnapshot != null) {
            newSnapshot.addAccountSnapshot(this);
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AccountSnapshot)) {
            return false;
        }

        final AccountSnapshot otherAccountSnapshot = (AccountSnapshot) other;

        return (account != null)
                && account.equals(otherAccountSnapshot.getAccount())
                && (snapshot != null)
                && snapshot.equals(otherAccountSnapshot.getSnapshot());
    }

    @Override
    public int hashCode() {
        return 53;
    }

    @Override
    public int compareTo(final AccountSnapshot other) {
        return account.compareTo(other.getAccount());
    }
}
