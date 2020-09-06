package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;

@RestController
public class AccountNameController extends BaseController {
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/accountname")
    public AccountNameResponse post(@RequestBody final AccountNameJsonRequest accountNameJsonRequest) {
        final User user = getCurrentUser();

        final Optional<Account> accountOpt = accountRepository.findById(accountNameJsonRequest.getAccountId());
        if (!accountBelongsToUser(user, accountOpt)) {
            // TODO Error message
            return AccountNameResponse.builder().hasError(true).build();
        }

        final Account account = accountOpt.get();

        account.setName(accountNameJsonRequest.getName().trim());
        accountRepository.save(account);

        return AccountNameResponse.builder().hasError(false).name(account.getName()).build();
    }

    @Data
    @Builder
    public static class AccountNameJsonRequest {
        private Long accountId;
        private String name;
    }

    @Data
    @Builder
    public static class AccountNameResponse {
        private boolean hasError;
        private String name;
    }
}
