package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.util.AccountUtils.hasFutureTithingImpact;

import br.net.du.sztoks.controller.util.SnapshotUtils;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.service.AccountService;
import br.net.du.sztoks.service.SnapshotService;
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

    @Autowired private SnapshotService snapshotService;

    @Autowired private AccountService accountService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Transactional
    public Object updateField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Account, Object> function,
            final boolean isSnapshotImpactingField) {
        final Long snapshotId = valueUpdateJsonRequest.getSnapshotId();

        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                isSnapshotImpactingField
                        ? snapshotUtils.validateLockAndRefreshSnapshot(model, snapshotId)
                        : snapshotUtils.validateSnapshot(model, snapshotId);

        if (isSnapshotImpactingField) {
            LOG.log(
                    LEVEL,
                    "[SZTOKS] Locked snapshot, assetsTotal = "
                            + snapshot.getTotalFor(AccountType.ASSET)
                            + ", liabilitiesTotal = "
                            + snapshot.getTotalFor(AccountType.LIABILITY));
        }

        final Optional<Account> accountOpt =
                accountService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(), snapshotId);

        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("account not found");
        }

        final Account account = accountOpt.get();

        if (!clazz.isInstance(account)) {
            throw new IllegalArgumentException("account not found");
        }

        final Optional<Account> futureTithingAccountOpt =
                isSnapshotImpactingField && hasFutureTithingImpact(account)
                        ? Optional.of(snapshot.getFutureTithingAccount(account.getCurrencyUnit()))
                        : Optional.empty();

        final Object jsonResponse = function.apply(valueUpdateJsonRequest, account);

        LOG.log(LEVEL, "[SZTOKS] Saving account...");
        accountService.save(account);

        // DEBUG
        //        try {
        //            Thread.sleep(3000);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

        if (isSnapshotImpactingField) {
            if (futureTithingAccountOpt.isPresent()) {
                LOG.log(LEVEL, "[SZTOKS] Saving future tithing account...");
                accountService.save(futureTithingAccountOpt.get());
            }

            LOG.log(LEVEL, "[SZTOKS] Saving snapshot...");
            snapshotService.save(snapshot);
        }

        return jsonResponse;
    }
}
