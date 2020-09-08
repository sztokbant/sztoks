package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerConstants.DECIMAL_FORMAT;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

@RestController
public class SnapshotAccountUpdateControllerBase {
    @Autowired
    SnapshotRepository snapshotRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountSnapshotRepository accountSnapshotRepository;

    Snapshot getSnapshot(final Model model, final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final User user = getLoggedUser(model);

        final Optional<Snapshot> snapshotOpt =
                snapshotRepository.findById(snapshotAccountUpdateJsonRequest.getSnapshotId());
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            throw new IllegalArgumentException();
        }

        final Snapshot snapshot = snapshotOpt.get();
        final Optional<Account> accountOpt =
                accountRepository.findById(snapshotAccountUpdateJsonRequest.getAccountId());
        if (!accountBelongsToUser(user, accountOpt) || !accountBelongsInSnapshot(snapshot, accountOpt)) {
            throw new IllegalArgumentException();
        }

        return snapshot;
    }

    private static boolean accountBelongsInSnapshot(final Snapshot snapshot, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && snapshot.getAccountSnapshotFor(accountOpt.get()).isPresent();
    }

    SnapshotAccountUpdateJsonResponse.SnapshotAccountUpdateJsonResponseBuilder getDefaultResponseBuilder(final Snapshot snapshot,
                                                                                                         final AccountSnapshot accountSnapshot) {
        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();
        final AccountType accountType = accountSnapshot.getAccount().getAccountType();

        return SnapshotAccountUpdateJsonResponse.builder()
                                                .balance(DECIMAL_FORMAT.format(accountSnapshot.getTotal().setScale(2)))
                                                .currencyUnit(currencyUnit.toString())
                                                .netWorth(DECIMAL_FORMAT.format(snapshot.getNetWorth()
                                                                                        .get(currencyUnit)
                                                                                        .setScale(2)))
                                                .accountType(accountType.name())
                                                .totalForAccountType(DECIMAL_FORMAT.format(snapshot.getTotalForAccountType(
                                                        accountType).get(currencyUnit).setScale(2)));
    }
}
