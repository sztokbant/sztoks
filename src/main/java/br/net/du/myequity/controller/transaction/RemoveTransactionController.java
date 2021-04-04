package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.SnapshotRemoveTransactionJsonResponse;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import org.joda.money.CurrencyUnit;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveTransactionController extends TransactionUpdateControllerBase {

    @PostMapping("/transaction/remove")
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

        if (transactionType.equals(TransactionType.INCOME)
                || transactionType.equals(TransactionType.DONATION)) {
            final CurrencyUnit baseCurrencyUnit = snapshot.getBaseCurrencyUnit();
            final String tithingBalance =
                    format(baseCurrencyUnit, toDecimal(snapshot.getTithingBalance()));

            final String netWorth = format(baseCurrencyUnit, toDecimal(snapshot.getNetWorth()));

            final String totalLiability =
                    format(
                            baseCurrencyUnit,
                            toDecimal(snapshot.getTotalFor(AccountType.LIABILITY)));

            builder.tithingBalance(tithingBalance)
                    .netWorth(netWorth)
                    .totalLiability(totalLiability);
        }

        return builder.entityId(valueUpdateJsonRequest.getEntityId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .type(transactionType.name())
                .totalForTransactionType(formatAsDecimal(snapshot.getTotalFor(transactionType)))
                .build();
    }
}
