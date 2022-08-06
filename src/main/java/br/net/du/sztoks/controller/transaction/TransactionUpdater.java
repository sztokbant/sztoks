package br.net.du.sztoks.controller.transaction;

import static br.net.du.sztoks.controller.util.TransactionUtils.hasTithingImpact;

import br.net.du.sztoks.controller.util.SnapshotUtils;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.transaction.Transaction;
import br.net.du.sztoks.model.transaction.TransactionType;
import br.net.du.sztoks.service.AccountService;
import br.net.du.sztoks.service.SnapshotService;
import br.net.du.sztoks.service.TransactionService;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Component
public class TransactionUpdater {
    private static Logger LOG = Logger.getLogger(TransactionUpdater.class.getName());
    private static Level LEVEL = Level.INFO;

    @Autowired private SnapshotService snapshotService;

    @Autowired private TransactionService transactionService;

    @Autowired private AccountService accountService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Transactional
    public Object updateField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Transaction, Object> function,
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
                    "[SZTOKS] Locked snapshot, incomesTotal = "
                            + snapshot.getTotalFor(TransactionType.INCOME)
                            + ", investmentsTotal = "
                            + snapshot.getTotalFor(TransactionType.INVESTMENT)
                            + ", donationsTotal = "
                            + snapshot.getTotalFor(TransactionType.DONATION));
        }

        final Optional<Transaction> transactionOpt =
                transactionService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(), snapshotId);

        if (!transactionOpt.isPresent()) {
            throw new IllegalArgumentException("transaction not found");
        }

        final Transaction transaction = transactionOpt.get();

        if (!clazz.isInstance(transaction)) {
            throw new IllegalArgumentException("transaction not found");
        }

        final Optional<Account> tithingAccountOpt =
                isSnapshotImpactingField && hasTithingImpact(transaction)
                        ? Optional.of(snapshot.getTithingAccount(transaction.getCurrencyUnit()))
                        : Optional.empty();

        final Object jsonResponse = function.apply(valueUpdateJsonRequest, transaction);

        LOG.log(LEVEL, "[SZTOKS] Saving transaction...");
        transactionService.save(transaction);

        // DEBUG
        //        try {
        //            Thread.sleep(3000);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

        if (isSnapshotImpactingField) {
            if (tithingAccountOpt.isPresent()) {
                LOG.log(LEVEL, "[SZTOKS] Saving account...");
                accountService.save(tithingAccountOpt.get());
            }

            LOG.log(LEVEL, "[SZTOKS] Saving snapshot...");
            snapshotService.save(snapshot);
        }
        return jsonResponse;
    }
}
