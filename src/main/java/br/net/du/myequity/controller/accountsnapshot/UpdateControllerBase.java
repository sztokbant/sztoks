package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
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
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<
                            ValueUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                    function) {
        final AccountSnapshot accountSnapshot = getAccountSnapshot(model, valueUpdateJsonRequest);

        if (!clazz.isInstance(accountSnapshot)) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        final AccountSnapshotViewModelOutput jsonResponse =
                function.apply(valueUpdateJsonRequest, accountSnapshot);

        accountSnapshotService.save(accountSnapshot);

        return jsonResponse;
    }

    AccountSnapshot getAccountSnapshot(
            final Model model, final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        // Ensure snapshot belongs to logged user
        snapshotUtils.getSnapshot(model, valueUpdateJsonRequest.getSnapshotId());

        final Optional<AccountSnapshot> accountSnapshotOpt =
                accountSnapshotService.findBySnapshotIdAndAccountId(
                        valueUpdateJsonRequest.getSnapshotId(),
                        valueUpdateJsonRequest.getEntityId());

        if (!accountSnapshotOpt.isPresent()) {
            throw new IllegalArgumentException("accountSnapshot not found");
        }

        return accountSnapshotOpt.get();
    }
}
