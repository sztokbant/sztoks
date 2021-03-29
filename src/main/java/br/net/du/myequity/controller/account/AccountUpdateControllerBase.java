package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class AccountUpdateControllerBase {
    @Autowired protected SnapshotService snapshotService;

    @Autowired private AccountService accountService;

    @Autowired private SnapshotUtils snapshotUtils;

    AccountViewModelOutput updateAccountField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput> function) {
        final Account account = getAccount(model, valueUpdateJsonRequest);

        if (!clazz.isInstance(account)) {
            throw new IllegalArgumentException("account not found");
        }

        final AccountViewModelOutput jsonResponse = function.apply(valueUpdateJsonRequest, account);

        accountService.save(account);

        return jsonResponse;
    }

    Account getAccount(final Model model, final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final Snapshot snapshot =
                snapshotUtils.validateSnapshot(model, valueUpdateJsonRequest.getSnapshotId());
        final Optional<Account> accountOpt =
                snapshot.getAccountById(valueUpdateJsonRequest.getEntityId());

        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("account not found");
        }

        return accountOpt.get();
    }
}
