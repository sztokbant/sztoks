package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import br.net.du.myequity.service.TransactionService;
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

    @Autowired protected SnapshotService snapshotService;

    @Autowired protected TransactionService transactionService;

    @Autowired protected AccountService accountService;

    @Autowired protected SnapshotUtils snapshotUtils;

    @Transactional
    public TransactionViewModelOutput updateField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                    function) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                snapshotUtils.validateLockAndRefreshSnapshot(
                        model, valueUpdateJsonRequest.getSnapshotId());

        LOG.log(
                LEVEL,
                "[SZTOKS] Locked snapshot, incomesTotal = "
                        + snapshot.getTotalFor(TransactionType.INCOME)
                        + ", investmentsTotal = "
                        + snapshot.getTotalFor(TransactionType.INVESTMENT)
                        + ", donationsTotal = "
                        + snapshot.getTotalFor(TransactionType.DONATION));

        final Optional<Transaction> transactionOpt =
                transactionService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(),
                        valueUpdateJsonRequest.getSnapshotId());

        if (!transactionOpt.isPresent()) {
            throw new IllegalArgumentException("transaction not found");
        }

        final Transaction transaction = transactionOpt.get();

        if (!clazz.isInstance(transaction)) {
            throw new IllegalArgumentException("transaction not found");
        }

        final TransactionViewModelOutput jsonResponse =
                function.apply(valueUpdateJsonRequest, transaction);

        LOG.log(LEVEL, "[SZTOKS] Saving transaction...");
        transactionService.save(transaction);

        // DEBUG
        //        try {
        //            Thread.sleep(3000);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

        LOG.log(LEVEL, "[SZTOKS] Saving account...");
        accountService.save(snapshot.getTithingAccount(transaction.getCurrencyUnit()));

        LOG.log(LEVEL, "[SZTOKS] Saving snapshot...");
        snapshotService.save(snapshot);

        return jsonResponse;
    }
}
