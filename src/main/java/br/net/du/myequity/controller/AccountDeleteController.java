package br.net.du.myequity.controller;

import br.net.du.myequity.controller.util.AccountUtils;
import br.net.du.myequity.controller.viewmodel.AccountDeleteJsonRequest;
import br.net.du.myequity.controller.viewmodel.AccountDeleteJsonResponse;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountDeleteController {

    @Autowired private AccountService accountService;

    @Autowired private AccountUtils accountUtils;

    @PostMapping("/account/delete")
    public AccountDeleteJsonResponse post(
            final Model model,
            @RequestBody final AccountDeleteJsonRequest accountDeleteJsonRequest) {
        final Account account =
                accountUtils.getAccount(model, accountDeleteJsonRequest.getAccountId());

        accountService.deleteAccount(account);

        return AccountDeleteJsonResponse.builder()
                .accountId(accountDeleteJsonRequest.getAccountId())
                .build();
    }
}
