package br.net.du.myequity.model;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PACKAGE) // for testing
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Workspace> workspaces = new HashSet<>();

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
    public Set<Workspace> getWorkspaces() {
        return ImmutableSet.copyOf(workspaces);
    }

    public void addWorkspace(final Workspace workspace) {
        // Prevents infinite loop
        if (workspaces.contains(workspace)) {
            return;
        }
        workspaces.add(workspace);
        workspace.setUser(this);
    }

    public void removeWorkspace(final Workspace workspace) {
        // Prevents infinite loop
        if (!workspaces.contains(workspace)) {
            return;
        }
        workspaces.remove(workspace);
        workspace.setUser(null);
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
