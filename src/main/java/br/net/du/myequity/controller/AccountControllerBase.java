package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.service.AccountService;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class AccountControllerBase {
    @Autowired AccountService accountService;

    Account getAccount(@NonNull final Model model, @NonNull final Long accountId) {
        final Optional<Account> accountOpt = accountService.findById(accountId);
        if (!accountBelongsToUser(getLoggedUser(model), accountOpt)) {
            throw new IllegalArgumentException();
        }

        return accountOpt.get();
    }
}
