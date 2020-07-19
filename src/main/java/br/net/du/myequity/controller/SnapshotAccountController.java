package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsInSnapshot;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

@RestController
public class SnapshotAccountController extends BaseController {
    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/account")
    public AccountJsonResponse post(@RequestBody final AccountJsonRequest accountJsonRequest) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(accountJsonRequest.getSnapshotId());
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            // TODO Error message
            return AccountJsonResponse.builder().hasError(true).build();
        }

        final Snapshot snapshot = snapshotOpt.get();
        final Optional<Account> accountOpt = accountRepository.findById(accountJsonRequest.getAccountId());
        if (!accountBelongsToUser(user, accountOpt) || !accountBelongsInSnapshot(snapshot, accountOpt)) {
            // TODO Error message
            return AccountJsonResponse.builder().hasError(true).build();
        }

        final Account account = accountOpt.get();

        snapshot.putAccount(account, accountJsonRequest.getBalance());

        snapshotRepository.save(snapshot);

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();
        final AccountType accountType = account.getAccountType();

        return AccountJsonResponse.builder()
                                  .hasError(false)
                                  .balance(snapshot.getAccount(account).toString())
                                  .currencyUnit(currencyUnit.toString())
                                  .netWorth(snapshot.getNetWorth().get(currencyUnit).toString())
                                  .accountType(accountType.name())
                                  .totalForAccountType(snapshot.getTotalForAccountType(accountType)
                                                               .get(currencyUnit)
                                                               .toString())
                                  .build();
    }

    @Data
    @Builder
    public static class AccountJsonRequest {
        private Long snapshotId;
        private Long accountId;
        private BigDecimal balance;
    }

    @Data
    @Builder
    public static class AccountJsonResponse {
        private boolean hasError;

        private String balance;
        private String currencyUnit;
        private String netWorth;
        private String accountType;
        private String totalForAccountType;
    }
}
