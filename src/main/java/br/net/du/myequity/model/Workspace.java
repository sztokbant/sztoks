package br.net.du.myequity.model;

import br.net.du.myequity.util.NetWorthUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "workspaces")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PACKAGE) // for testing
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private User user;

    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false)
    private String defaultCurrency;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Snapshot> snapshots = new ArrayList<>();

    public Workspace(final String name, final CurrencyUnit defaultCurrency) {
        this.name = name;
        setDefaultCurrency(defaultCurrency);
    }

    public void setUser(final User user) {
        // Prevents infinite loop
        if (sameAsFormer(user)) {
            return;
        }

        final User oldUser = this.user;
        this.user = user;

        if (oldUser != null) {
            oldUser.removeWorkspace(this);
        }

        if (user != null) {
            user.addWorkspace(this);
        }
    }

    private boolean sameAsFormer(final User newUser) {
        return user == null ?
                newUser == null :
                user.equals(newUser);
    }

    public CurrencyUnit getDefaultCurrency() {
        return CurrencyUnit.of(defaultCurrency);
    }

    public void setDefaultCurrency(final CurrencyUnit defaultCurrency) {
        this.defaultCurrency = defaultCurrency.getCode();
    }

    /**
     * Ref.: https://meri-stuff.blogspot.com/2012/03/jpa-tutorial
     * .html#RelationshipsBidirectionalOneToManyManyToOneConsistency
     *
     * @return Defensive copy to prevent it from being modified from the outside.
     */
    public Set<Account> getAccounts() {
        return ImmutableSet.copyOf(accounts);
    }

    public void addAccount(final Account account) {
        // Prevents infinite loop
        if (accounts.contains(account)) {
            return;
        }
        accounts.add(account);
        account.setWorkspace(this);
    }

    public void removeAccount(final Account account) {
        // Prevents infinite loop
        if (!accounts.contains(account)) {
            return;
        }
        accounts.remove(account);
        account.setWorkspace(null);
    }

    /**
     * Ref.: https://meri-stuff.blogspot.com/2012/03/jpa-tutorial
     * .html#RelationshipsBidirectionalOneToManyManyToOneConsistency
     *
     * @return Defensive copy to prevent it from being modified from the outside.
     */
    public List<Snapshot> getSnapshots() {
        return ImmutableList.copyOf(snapshots);
    }

    public void addSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (snapshots.contains(snapshot)) {
            return;
        }
        snapshots.add(snapshot);
        snapshot.setWorkspace(this);
    }

    public void removeSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (!snapshots.contains(snapshot)) {
            return;
        }
        snapshots.remove(snapshot);
        snapshot.setWorkspace(null);
    }

    public void generateSnapshot(final LocalDate date) {
        addSnapshot(new Snapshot(date, accounts));
    }

    public Map<CurrencyUnit, BigDecimal> getNetWorth() {
        return NetWorthUtil.computeByCurrency(accounts);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Workspace)) {
            return false;
        }

        return id != null && id.equals(((Workspace) other).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
