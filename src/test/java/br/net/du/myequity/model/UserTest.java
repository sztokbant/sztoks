package br.net.du.myequity.model;

import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User user;
    private Workspace workspace;

    @BeforeEach
    public void setUp() {
        user = new User("example@example.com", "Bill", "Gates");
        user.setId(1L);

        workspace = new Workspace("My Workspace", CurrencyUnit.USD);
        workspace.setId(42L);
    }

    @Test
    public void constructor() {
        // THEN
        assertEquals("example@example.com", user.getEmail());
    }

    @Test
    public void addWorkspace() {
        // GIVEN
        assertTrue(user.getWorkspaces().isEmpty());

        // WHEN
        user.addWorkspace(workspace);

        // THEN
        final Set<Workspace> workspaces = user.getWorkspaces();
        assertEquals(1, workspaces.size());
        assertEquals(user, workspace.getUser());
    }

    @Test
    public void addWorkspace_addSameTwice() {
        // GIVEN
        assertTrue(user.getWorkspaces().isEmpty());
        user.addWorkspace(workspace);

        // WHEN
        user.addWorkspace(workspace);

        // THEN
        final Set<Workspace> workspaces = user.getWorkspaces();
        assertEquals(1, workspaces.size());
        assertEquals(user, workspace.getUser());
    }

    @Test
    public void removeWorkspace() {
        // GIVEN
        assertTrue(user.getWorkspaces().isEmpty());
        user.addWorkspace(workspace);

        // WHEN
        user.removeWorkspace(workspace);

        // THEN
        final Set<Workspace> workspaces = user.getWorkspaces();
        assertTrue(workspaces.isEmpty());
        assertNull(workspace.getUser());
    }

    @Test
    public void removeWorkspace_removeSameTwice() {
        // GIVEN
        assertTrue(user.getWorkspaces().isEmpty());
        user.addWorkspace(workspace);
        user.removeWorkspace(workspace);

        // WHEN
        user.removeWorkspace(workspace);

        // THEN
        final Set<Workspace> workspaces = user.getWorkspaces();
        assertTrue(workspaces.isEmpty());
        assertNull(workspace.getUser());
    }

    @Test
    public void equals() {
        // Itself
        assertTrue(user.equals(user));

        // Not instance of User
        assertFalse(this.user.equals(null));
        assertFalse(this.user.equals("Another type of object"));

        // Same Id null
        final User anotherUser = new User();
        user.setId(null);
        anotherUser.setId(null);
        assertFalse(user.equals(anotherUser));
        assertFalse(anotherUser.equals(user));

        // Same Id not null
        user.setId(42L);
        anotherUser.setId(42L);
        assertTrue(user.equals(anotherUser));
        assertTrue(anotherUser.equals(user));
    }
}