package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.service.SnapshotService;
import br.net.du.myequity.service.TransactionService;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class TransactionUpdateControllerBase {
    @Autowired protected SnapshotService snapshotService;

    @Autowired protected TransactionService transactionService;

    @Autowired protected SnapshotUtils snapshotUtils;

    TransactionViewModelOutput updateTransactionField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                    function) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                snapshotUtils.validateSnapshot(model, valueUpdateJsonRequest.getSnapshotId());

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

        transactionService.save(transaction);
        snapshotService.save(snapshot);

        return jsonResponse;
    }
}
