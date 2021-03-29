package br.net.du.myequity.model;

import com.google.common.collect.ImmutableSortedSet;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SortNatural;
import org.joda.money.CurrencyUnit;

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

    @Column(nullable = true)
    private String defaultCurrency;

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

        if (id == null) {
            return false;
        }

        final User otherUser = (User) other;

        return id.equals(otherUser.getId());
    }

    @Override
    public int hashCode() {
        return 37;
    }
}
