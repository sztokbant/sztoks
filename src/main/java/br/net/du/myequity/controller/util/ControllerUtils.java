package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;

import java.util.Optional;

public class ControllerUtils {
    public static boolean accountBelongsToUser(final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getUser().equals(user);
    }

    public static boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }

    public static boolean accountBelongsInSnapshot(final Snapshot snapshot, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && snapshot.getAccount(accountOpt.get()) != null;
    }
}
