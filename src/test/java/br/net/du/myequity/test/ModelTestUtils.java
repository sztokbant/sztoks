package br.net.du.myequity.test;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public class ModelTestUtils {

    public static User buildUser() {
        final String email = TestConstants.EMAIL;
        final String firstName = TestConstants.FIRST_NAME;
        final String lastName = TestConstants.LAST_NAME;
        final Long id = 42L;

        final User user = new User(email, firstName, lastName);
        user.setId(id);

        user.addSnapshot(
                new Snapshot(1L, "First Snapshot", ImmutableSortedSet.of(), ImmutableList.of()));

        return user;
    }
}
