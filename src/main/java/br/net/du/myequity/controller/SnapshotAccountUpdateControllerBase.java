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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

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

    AccountSnapshot getAccountSnapshot(@RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                       final Class<? extends AccountSnapshot>... classes) {
        final Optional<AccountSnapshot> accountSnapshotOpt = accountSnapshotRepository.findBySnapshotIdAndAccountId(
                snapshotAccountUpdateJsonRequest.getSnapshotId(),
                snapshotAccountUpdateJsonRequest.getAccountId());

        if (!accountSnapshotOpt.isPresent() || !isInstance(accountSnapshotOpt.get(), classes)) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        return accountSnapshotOpt.get();
    }

    private boolean isInstance(final AccountSnapshot accountSnapshot,
                               final Class<? extends AccountSnapshot>... classes) {
        for (final Class clazz : classes) {
            if (clazz.isInstance(accountSnapshot)) {
                return true;
            }
        }
        return false;
    }

    // TODO Simplify use-cases that don't need all of these attributes
    SnapshotAccountUpdateJsonResponse.SnapshotAccountUpdateJsonResponseBuilder getDefaultResponseBuilder(final Snapshot snapshot,
                                                                                                         final AccountSnapshot accountSnapshot) {
        final CurrencyUnit currencyUnit = accountSnapshot.getAccount().getCurrencyUnit();
        final AccountType accountType = accountSnapshot.getAccount().getAccountType();

        return SnapshotAccountUpdateJsonResponse.builder()
                                                .balance(formatAsDecimal(accountSnapshot.getTotal()))
                                                .currencyUnit(currencyUnit.getCode())
                                                .currencyUnitSymbol(currencyUnit.getSymbol())
                                                .netWorth(formatAsDecimal(snapshot.getNetWorth().get(currencyUnit)))
                                                .accountType(accountType.name())
                                                .totalForAccountType(formatAsDecimal(snapshot.getTotalForAccountType(
                                                        accountType).get(currencyUnit)));
    }
}
