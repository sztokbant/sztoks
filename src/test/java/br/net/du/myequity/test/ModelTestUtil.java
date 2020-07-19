package br.net.du.myequity.test;

import br.net.du.myequity.model.User;

public class ModelTestUtil {

    public static User buildUser() {
        final String email = "example@example.com";
        final String firstName = "Bill";
        final String lastName = "Gates";
        final Long id = 42L;

        final User user = new User(email, firstName, lastName);
        user.setId(id);

        return user;
    }
}
