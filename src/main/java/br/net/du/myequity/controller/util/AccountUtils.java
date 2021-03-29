package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.model.User;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.service.AccountSnapshotService;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class AccountUtils {
    @Autowired private AccountSnapshotService accountService;

    public AccountSnapshot getAccount(@NonNull final Model model, @NonNull final Long accountId) {
        final Optional<AccountSnapshot> accountOpt = accountService.findById(accountId);
        if (!accountBelongsToUser(getLoggedUser(model), accountOpt)) {
            throw new IllegalArgumentException();
        }

        return accountOpt.get();
    }

    private boolean accountBelongsToUser(
            final User user, final Optional<AccountSnapshot> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getSnapshot().getUser().equals(user);
    }
}
