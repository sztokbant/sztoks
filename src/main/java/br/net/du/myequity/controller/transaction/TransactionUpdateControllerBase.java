package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import br.net.du.myequity.service.TransactionService;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

public class TransactionUpdateControllerBase {
    @Autowired protected SnapshotService snapshotService;

    @Autowired private TransactionService transactionService;

    @Autowired private AccountService accountService;

    @Autowired private SnapshotUtils snapshotUtils;

    TransactionViewModelOutput updateTransactionField(
            final Model model,
            final ValueUpdateJsonRequest valueUpdateJsonRequest,
            final Class clazz,
            final BiFunction<ValueUpdateJsonRequest, Transaction, TransactionViewModelOutput>
                    function) {
        final Transaction transaction = getTransaction(model, valueUpdateJsonRequest);

        if (!clazz.isInstance(transaction)) {
            throw new IllegalArgumentException("transaction not found");
        }

        final TransactionViewModelOutput jsonResponse =
                function.apply(valueUpdateJsonRequest, transaction);

        transactionService.save(transaction);

        return jsonResponse;
    }

    Transaction getTransaction(
            final Model model, final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        // Ensure snapshot belongs to logged user
        snapshotUtils.validateSnapshot(model, valueUpdateJsonRequest.getSnapshotId());

        final Optional<Transaction> transactionOpt =
                transactionService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(),
                        valueUpdateJsonRequest.getSnapshotId());

        if (!transactionOpt.isPresent()) {
            throw new IllegalArgumentException("transaction not found");
        }

        return transactionOpt.get();
    }
}
