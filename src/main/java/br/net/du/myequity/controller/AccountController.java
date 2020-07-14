package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class AccountController extends BaseController {
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/account/{id}")
    public String post(@PathVariable(value = "id") final Long accountId,
                       @RequestParam("balance_amount") final BigDecimal balanceAmount,
                       @RequestParam("workspace_id") final Long workspaceId) {
        final User user = getCurrentUser();

        final Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent() || !accountOpt.get().getWorkspace().getUser().equals(user)) {
            // TODO Error message
            return "redirect:/";
        }

        final Account account = accountOpt.get();
        account.setBalanceAmount(balanceAmount);

        accountRepository.save(account);

        return String.format("redirect:/workspace/%s", workspaceId);
    }
}
