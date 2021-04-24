package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.FutureTithingCapable;
import br.net.du.myequity.model.account.FutureTithingPolicy;
import br.net.du.myequity.service.AccountService;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class AccountUtils {
    @Autowired private AccountService accountService;

    public Account getAccount(@NonNull final Model model, @NonNull final Long accountId) {
        final Optional<Account> accountOpt = accountService.findById(accountId);
        if (!accountBelongsToUser(getLoggedUser(model), accountOpt)) {
            throw new IllegalArgumentException();
        }

        return accountOpt.get();
    }

    private boolean accountBelongsToUser(final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getSnapshot().getUser().equals(user);
    }

    public static boolean hasFutureTithingImpact(@NonNull final Account account) {
        return account instanceof FutureTithingCapable
                && !((FutureTithingCapable) account)
                        .getFutureTithingPolicy()
                        .equals(FutureTithingPolicy.NONE);
    }
}
