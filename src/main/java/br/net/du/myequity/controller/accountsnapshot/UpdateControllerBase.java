package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.AccountSnapshotService;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class UpdateControllerBase {
    @Autowired protected SnapshotService snapshotService;

    @Autowired private AccountService accountService;

    @Autowired private AccountSnapshotService accountSnapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

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

        accountSnapshotService.save(accountSnapshot);

        return jsonResponse;
    }

    AccountSnapshot getAccountSnapshot(
            final Model model,
            final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        // Ensure snapshot belongs to logged user
        snapshotUtils.getSnapshot(model, accountSnapshotUpdateJsonRequest.getSnapshotId());

        final Optional<AccountSnapshot> accountSnapshotOpt =
                accountSnapshotService.findBySnapshotIdAndAccountId(
                        accountSnapshotUpdateJsonRequest.getSnapshotId(),
                        accountSnapshotUpdateJsonRequest.getAccountId());

        if (!accountSnapshotOpt.isPresent()) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        return accountSnapshotOpt.get();
    }
}
