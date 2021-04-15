package br.net.du.myequity.controller.transaction;

import br.net.du.myequity.controller.viewmodel.SnapshotRemoveTransactionJsonResponse;
import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import org.joda.money.CurrencyUnit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveTransactionController extends TransactionUpdateControllerBase {

    @PostMapping("/transaction/remove")
    @Transactional
    public SnapshotRemoveTransactionJsonResponse post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final Transaction transaction = getTransaction(model, valueUpdateJsonRequest);
        final Snapshot snapshot = transaction.getSnapshot();

        snapshot.removeTransaction(transaction);
        snapshotService.save(snapshot);

        final TransactionType transactionType = transaction.getTransactionType();
        final CurrencyUnit currencyUnit = transaction.getCurrencyUnit();

        final SnapshotRemoveTransactionJsonResponse.SnapshotRemoveTransactionJsonResponseBuilder
                builder = SnapshotRemoveTransactionJsonResponse.builder();

        final UpdateableTotals updateableTotals = new UpdateableTotals(snapshot);

        builder.totalForTransactionType(updateableTotals.getTotalFor(transactionType));

        if (transactionType.equals(TransactionType.INCOME)
                || transactionType.equals(TransactionType.DONATION)) {
            if (transactionType.equals(TransactionType.DONATION)) {
                builder.taxDeductibleDonationsTotal(
                        updateableTotals.getTaxDeductibleDonationsTotal());
            }

            builder.tithingBalance(updateableTotals.getTithingBalance())
                    .totalLiability(updateableTotals.getTotalFor(AccountType.LIABILITY))
                    .netWorth(updateableTotals.getNetWorth());
        }

        return builder.entityId(valueUpdateJsonRequest.getEntityId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .type(transactionType.name())
                .build();
    }
}
