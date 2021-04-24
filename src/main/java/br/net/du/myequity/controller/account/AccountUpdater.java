package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Component
public class AccountUpdater {
    private static Logger LOG = Logger.getLogger(AccountUpdater.class.getName());
    private static Level LEVEL = Level.INFO;

    @Autowired protected SnapshotService snapshotService;

    @Autowired protected AccountService accountService;

    @Autowired protected SnapshotUtils snapshotUtils;

    @Transactional
    public AccountViewModelOutput updateField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput> function) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                snapshotUtils.validateLockAndRefreshSnapshot(
                        model, valueUpdateJsonRequest.getSnapshotId());

        LOG.log(
                LEVEL,
                "[SZTOKS] Locked snapshot, assetsTotal = "
                        + snapshot.getTotalFor(AccountType.ASSET)
                        + ", liabilitiesTotal = "
                        + snapshot.getTotalFor(AccountType.LIABILITY));

        final Optional<Account> accountOpt =
                accountService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(),
                        valueUpdateJsonRequest.getSnapshotId());

        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("account not found");
        }

        final Account account = accountOpt.get();

        if (!clazz.isInstance(account)) {
            throw new IllegalArgumentException("account not found");
        }

        final AccountViewModelOutput jsonResponse = function.apply(valueUpdateJsonRequest, account);

        LOG.log(LEVEL, "[SZTOKS] Saving account...");
        accountService.save(account);

        // DEBUG
        //        try {
        //            Thread.sleep(3000);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

        LOG.log(LEVEL, "[SZTOKS] Saving future tithing account...");
        accountService.save(snapshot.getFutureTithingAccount(account.getCurrencyUnit()));

        LOG.log(LEVEL, "[SZTOKS] Saving snapshot...");
        snapshotService.save(snapshot);

        return jsonResponse;
    }
}
