package br.net.du.myequity.test;

import br.net.du.myequity.model.User;

public class ModelTestUtil {

    public static User buildUser() {
        final String email = TestConstants.EMAIL;
        final String firstName = TestConstants.FIRST_NAME;
        final String lastName = TestConstants.LAST_NAME;
        final Long id = 42L;

        final User user = new User(email, firstName, lastName);
        user.setId(id);

        return user;
    }
}
