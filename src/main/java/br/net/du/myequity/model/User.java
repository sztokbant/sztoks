package br.net.du.myequity.model;

import com.google.common.collect.ImmutableSortedSet;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    // this should match Snapshot::compareTo()
    @OrderBy("name DESC")
    private final List<Snapshot> snapshots = new ArrayList<>();

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

        // Ensures uniqueness of Snapshot name
        for (final Snapshot existingSnapshot : snapshots) {
            if (existingSnapshot.getName().equals(snapshot.getName())) {
                throw new IllegalArgumentException(
                        "User " + id + " already has a Snapshot named " + snapshot.getName());
            }
        }

        insertSorted(snapshot);
        snapshot.setUser(this);
    }

    private void insertSorted(final Snapshot snapshot) {
        if (snapshots.isEmpty()) {
            snapshots.add(snapshot);
            return;
        }

        boolean inserted = false;
        for (int index = 0; !inserted && index < snapshots.size(); index++) {
            final Snapshot currentSnapshot = snapshots.get(index);

            // snapshot is sorted in decreasing order, so "Snapshot::compareTo < 0" means "in the
            // future of"
            if (snapshot.compareTo(currentSnapshot) < 0) {
                // Insert after the immediately more recent
                snapshot.setPrevious(currentSnapshot);
                snapshot.setNext(currentSnapshot.getNext());

                if (snapshot.getNext() != null) {
                    snapshot.getNext().setPrevious(snapshot);
                }

                currentSnapshot.setNext(snapshot);

                snapshots.add(index, snapshot);

                inserted = true;
            }
        }

        if (!inserted) {
            // Insert "in the past of the one most to the past"
            final Snapshot currentSnapshot = snapshots.get(snapshots.size() - 1);

            snapshot.setNext(currentSnapshot);
            currentSnapshot.setPrevious(snapshot);

            snapshots.add(snapshot);
        }
    }

    public void removeSnapshot(final Snapshot snapshot) {
        // Prevents infinite loop
        if (!snapshots.contains(snapshot)) {
            return;
        }

        final Snapshot next = snapshot.getNext();
        final Snapshot previous = snapshot.getPrevious();

        if (next != null) {
            next.setPrevious(previous);
        }

        if (previous != null) {
            previous.setNext(next);
        }

        snapshot.setNext(null);
        snapshot.setPrevious(null);

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
