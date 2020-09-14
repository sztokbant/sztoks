package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.AccountNameJsonRequest;
import br.net.du.myequity.controller.model.AccountNameJsonResponse;
import br.net.du.myequity.model.account.Account;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountNameController extends AccountControllerBase {

    @PostMapping("/account/updateName")
    public AccountNameJsonResponse post(final Model model,
                                        @RequestBody final AccountNameJsonRequest accountNameJsonRequest) {
        final Account account = getAccount(model, accountNameJsonRequest.getAccountId());

        account.setName(accountNameJsonRequest.getNewValue().trim());
        accountRepository.save(account);

        return AccountNameJsonResponse.builder().name(account.getName()).build();
    }
}
