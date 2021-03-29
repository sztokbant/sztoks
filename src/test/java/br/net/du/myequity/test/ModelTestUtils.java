package br.net.du.myequity.test;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public class ModelTestUtils {

    public static final long SNAPSHOT_ID = 99L;

    public static User buildUser() {
        final String email = TestConstants.EMAIL;
        final String firstName = TestConstants.FIRST_NAME;
        final String lastName = TestConstants.LAST_NAME;
        final Long id = 42L;

        final User user = new User(email, firstName, lastName);
        user.setId(id);

        final Snapshot snapshot =
                new Snapshot(1L, "First Snapshot", ImmutableSortedSet.of(), ImmutableList.of());
        snapshot.setId(SNAPSHOT_ID);

        user.addSnapshot(snapshot);

        return user;
    }
}
