package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.User;

import java.util.Optional;

public class ControllerUtils {
    public static boolean accountBelongsToUser(final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getWorkspace().getUser().equals(user);
    }
}
