package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.AccountNameJsonRequest;
import br.net.du.myequity.controller.model.AccountNameJsonResponse;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

@RestController
public class AccountNameController {
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/updateAccountName")
    public AccountNameJsonResponse post(final Model model,
                                        @RequestBody final AccountNameJsonRequest accountNameJsonRequest) {
        final Account account = getAccount(model, accountNameJsonRequest);

        account.setName(accountNameJsonRequest.getNewValue().trim());
        accountRepository.save(account);

        return AccountNameJsonResponse.builder().name(account.getName()).build();
    }

    private Account getAccount(final Model model, final AccountNameJsonRequest accountNameJsonRequest) {
        final Optional<Account> accountOpt = accountRepository.findById(accountNameJsonRequest.getAccountId());
        if (!accountBelongsToUser(getLoggedUser(model), accountOpt)) {
            throw new IllegalArgumentException();
        }

        return accountOpt.get();
    }
}
