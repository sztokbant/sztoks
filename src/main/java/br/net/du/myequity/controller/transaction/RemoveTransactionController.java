package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

import br.net.du.myequity.controller.viewmodel.SnapshotRemoveTransactionJsonResponse;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import org.joda.money.CurrencyUnit;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveTransactionController extends TransactionUpdateControllerBase {

    @PostMapping("/snapshot/removeTransaction")
    public SnapshotRemoveTransactionJsonResponse post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final Transaction transaction = getTransaction(model, valueUpdateJsonRequest);
        final Snapshot snapshot = transaction.getSnapshot();

        snapshot.removeTransaction(transaction);
        snapshotService.save(snapshot);

        final TransactionType transactionType = transaction.getTransactionType();
        final CurrencyUnit currencyUnit = transaction.getCurrencyUnit();

        return SnapshotRemoveTransactionJsonResponse.builder()
                .entityId(valueUpdateJsonRequest.getEntityId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .type(transactionType.name())
                .totalForType(
                        formatAsDecimal(
                                snapshot.getTotalForTransactionType(transactionType)
                                        .get(currencyUnit)))
                .build();
    }
}
