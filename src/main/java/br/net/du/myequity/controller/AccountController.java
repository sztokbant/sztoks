package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import br.net.du.myequity.persistence.AccountRepository;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class AccountController extends BaseController {
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/account")
    public AccountJsonResponse post(@RequestBody final AccountJsonRequest accountJsonRequest) {
        final User user = getCurrentUser();

        final Optional<Account> accountOpt = accountRepository.findById(accountJsonRequest.getId());
        if (!accountOpt.isPresent() || !accountOpt.get().getWorkspace().getUser().equals(user)) {
            // TODO Error message
            return AccountJsonResponse.builder().hasError(true).build();
        }

        final Account account = accountOpt.get();
        account.setBalanceAmount(accountJsonRequest.getBalance());

        accountRepository.save(account);

        final CurrencyUnit currencyUnit = account.getBalance().getCurrencyUnit();
        final AccountType accountType = account.getAccountType();
        final Workspace workspace = account.getWorkspace();

        return AccountJsonResponse.builder()
                                  .hasError(false)
                                  .id(account.getId())
                                  .balance(account.getBalance().getAmount().toString())
                                  .currencyUnit(currencyUnit.toString())
                                  .netWorth(workspace.getNetWorth().get(currencyUnit).toString())
                                  .accountType(accountType.name())
                                  .totalForAccountType(workspace.getTotalForAccountType(accountType)
                                                                .get(currencyUnit)
                                                                .toString())
                                  .build();
    }

    @Data
    @Builder
    public static class AccountJsonRequest {
        private Long id;
        private BigDecimal balance;
    }

    @Data
    @Builder
    public static class AccountJsonResponse {
        private boolean hasError;

        private Long id;
        private String balance;
        private String currencyUnit;
        private String netWorth;
        private String accountType;
        private String totalForAccountType;
    }
}
