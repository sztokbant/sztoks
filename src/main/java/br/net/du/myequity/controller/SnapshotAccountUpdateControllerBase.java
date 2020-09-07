package br.net.du.myequity.controller;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerConstants.DECIMAL_FORMAT;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsInSnapshot;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

@RestController
public class SnapshotAccountUpdateControllerBase extends BaseController {
    @Autowired
    SnapshotRepository snapshotRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountSnapshotRepository accountSnapshotRepository;

    // TODO Create new Exception
    Snapshot loadSnapshot(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest)
            throws IllegalAccessException {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt =
                snapshotRepository.findById(snapshotAccountUpdateJsonRequest.getSnapshotId());
        if (!snapshotBelongsToUser(user, snapshotOpt)) {// TODO Error message
            throw new IllegalAccessException();
        }

        final Snapshot snapshot = snapshotOpt.get();
        final Optional<Account> accountOpt =
                accountRepository.findById(snapshotAccountUpdateJsonRequest.getAccountId());
        if (!accountBelongsToUser(user, accountOpt) || !accountBelongsInSnapshot(snapshot, accountOpt)) {
            throw new IllegalAccessException();
        }

        return snapshot;
    }

    SnapshotAccountUpdateResponse.SnapshotAccountUpdateResponseBuilder getDefaultResponseBuilder(final Snapshot snapshot,
                                                                                                 final AccountSnapshot accountSnapshot) {
        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();
        final AccountType accountType = accountSnapshot.getAccount().getAccountType();

        return SnapshotAccountUpdateResponse.builder()
                                            .hasError(false)
                                            .balance(DECIMAL_FORMAT.format(accountSnapshot.getTotal().setScale(2)))
                                            .currencyUnit(currencyUnit.toString())
                                            .netWorth(DECIMAL_FORMAT.format(snapshot.getNetWorth()
                                                                                    .get(currencyUnit)
                                                                                    .setScale(2)))
                                            .accountType(accountType.name())
                                            .totalForAccountType(DECIMAL_FORMAT.format(snapshot.getTotalForAccountType(
                                                    accountType).get(currencyUnit).setScale(2)));
    }

    @Data
    @Builder
    public static class SnapshotAccountUpdateJsonRequest {
        private Long snapshotId;
        private Long accountId;
        private BigDecimal newValue;
    }

    @Data
    @Builder
    public static class SnapshotAccountUpdateResponse {
        private boolean hasError;

        private String balance;
        private String currencyUnit;
        private String netWorth;
        private String accountType;
        private String totalForAccountType;

        // Credit Card only
        private String totalCredit;
        private String availableCredit;
    }
}
