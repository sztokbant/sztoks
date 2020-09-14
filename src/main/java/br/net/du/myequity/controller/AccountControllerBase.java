package br.net.du.myequity.controller;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.service.AccountService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

public class AccountControllerBase {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    Account getAccount(@NonNull final Model model, @NonNull final Long accountId) {
        final Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountBelongsToUser(getLoggedUser(model), accountOpt)) {
            throw new IllegalArgumentException();
        }

        return accountOpt.get();
    }
}
