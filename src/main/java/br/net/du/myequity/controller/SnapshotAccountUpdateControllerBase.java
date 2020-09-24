package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonResponse;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.function.BiFunction;

import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

public class SnapshotAccountUpdateControllerBase {
    @Autowired
    SnapshotRepository snapshotRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountSnapshotRepository accountSnapshotRepository;

    AccountSnapshotUpdateJsonResponse updateAccountSnapshotField(final Model model,
                                                                 final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                 final Class clazz,
                                                                 final BiFunction<SnapshotAccountUpdateJsonRequest,
                                                                         AccountSnapshot,
                                                                         AccountSnapshotUpdateJsonResponse> function) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final AccountSnapshot accountSnapshot = getAccountSnapshot(snapshotAccountUpdateJsonRequest);

        if (!clazz.isInstance(accountSnapshot)) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        final AccountSnapshotUpdateJsonResponse jsonResponse =
                function.apply(snapshotAccountUpdateJsonRequest, accountSnapshot);

        accountSnapshotRepository.save(accountSnapshot);

        return jsonResponse;
    }

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

    private AccountSnapshot getAccountSnapshot(@RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Optional<AccountSnapshot> accountSnapshotOpt = accountSnapshotRepository.findBySnapshotIdAndAccountId(
                snapshotAccountUpdateJsonRequest.getSnapshotId(),
                snapshotAccountUpdateJsonRequest.getAccountId());

        if (!accountSnapshotOpt.isPresent()) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        return accountSnapshotOpt.get();
    }
}
