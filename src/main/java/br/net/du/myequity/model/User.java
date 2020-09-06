package br.net.du.myequity.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SortNatural;
import org.joda.money.CurrencyUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter // for testing
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String firstName;

    @Column(nullable = false)
    @Getter
    @Setter
    private String lastName;

    @Column(nullable = false)
    @Getter
    @Setter
    private String password;

    @Transient
    @Getter
    @Setter
    private String passwordConfirm;

    @Column(nullable = true)
    private String defaultCurrency;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural // Ref.: https://thorben-janssen.com/ordering-vs-sorting-hibernate-use/
    private final SortedSet<Account> accounts = new TreeSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural // Ref.: https://thorben-janssen.com/ordering-vs-sorting-hibernate-use/
    private final SortedSet<Snapshot> snapshots = new TreeSet<>();

    public User(final String email, final String firstName, final String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    /**
     * Ref.: https://meri-stuff.blogspot.com/2012/03/jpa-tutorial
     * .html#RelationshipsBidirectionalOneToManyManyToOneConsistency
     *
     * @return Immutable copy to prevent it from being modified from the outside.
     */
    public Map<AccountType, SortedSet<Account>> getAccounts() {
        return accounts.stream()
                       .collect(collectingAndThen(groupingBy(Account::getAccountType,
                                                             collectingAndThen(toSet(), ImmutableSortedSet::copyOf)),
                                                  ImmutableMap::copyOf));
    }

    public void addAccount(final Account account) {
        // Prevents infinite loop
        if (accounts.contains(account)) {
            return;
        }
        accounts.add(account);
        account.setUser(this);
    }

    public void removeAccount(final Account account) {
        // Prevents infinite loop
        if (!accounts.contains(account)) {
            return;
        }
        accounts.remove(account);
        account.setUser(null);
    }

    /**
     * Ref.: https://meri-stuff.blogspot.com/2012/03/jpa-tutorial
     * .html#RelationshipsBidirectionalOneToManyManyToOneConsistency
     *
     * @return Immutable copy to prevent it from being modified from the outside.
     */
    public SortedSet<Snapshot> getSnapshots() {
        return ImmutableSortedSet.copyOf(snapshots);
    }

    public void addSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (snapshots.contains(snapshot)) {
            return;
        }
        snapshots.add(snapshot);
        snapshot.setUser(this);
    }

    public void removeSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (!snapshots.contains(snapshot)) {
            return;
        }
        snapshots.remove(snapshot);
        snapshot.setUser(null);
    }

    public CurrencyUnit getDefaultCurrency() {
        return CurrencyUnit.of(defaultCurrency);
    }

    public void setDefaultCurrency(final CurrencyUnit defaultCurrency) {
        this.defaultCurrency = defaultCurrency.getCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof User)) {
            return false;
        }

        return id != null && id.equals(((User) other).getId());
    }

    @Override
    public int hashCode() {
        return 37;
    }
}
