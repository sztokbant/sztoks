package br.net.du.myequity.controller.accountsnapshot;

import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

import br.net.du.myequity.controller.viewmodel.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class UpdateControllerBase {
    @Autowired SnapshotService snapshotService;

    @Autowired AccountService accountService;

    @Autowired AccountSnapshotRepository accountSnapshotRepository;

    AccountSnapshotViewModelOutput updateAccountSnapshotField(
            final Model model,
            final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest,
            final Class clazz,
            final BiFunction<
                            AccountSnapshotUpdateJsonRequest,
                            AccountSnapshot,
                            AccountSnapshotViewModelOutput>
                    function) {
        final AccountSnapshot accountSnapshot =
                getAccountSnapshot(model, accountSnapshotUpdateJsonRequest);

        if (!clazz.isInstance(accountSnapshot)) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        final AccountSnapshotViewModelOutput jsonResponse =
                function.apply(accountSnapshotUpdateJsonRequest, accountSnapshot);

        accountSnapshotRepository.save(accountSnapshot);

        return jsonResponse;
    }

    AccountSnapshot getAccountSnapshot(
            final Model model,
            final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        final User user = getLoggedUser(model);

        final Optional<Snapshot> snapshotOpt =
                snapshotService.findById(accountSnapshotUpdateJsonRequest.getSnapshotId());
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            throw new IllegalArgumentException();
        }

        final Optional<AccountSnapshot> accountSnapshotOpt =
                accountSnapshotRepository.findBySnapshotIdAndAccountId(
                        accountSnapshotUpdateJsonRequest.getSnapshotId(),
                        accountSnapshotUpdateJsonRequest.getAccountId());

        if (!accountSnapshotOpt.isPresent()) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        return accountSnapshotOpt.get();
    }
}
