package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.SnapshotRemoveAccountJsonResponse;
import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import org.joda.money.CurrencyUnit;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveAccountController extends AccountUpdateControllerBase {

    @PostMapping("/snapshot/removeAccount")
    public SnapshotRemoveAccountJsonResponse post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final Account account = getAccount(model, valueUpdateJsonRequest);
        final Snapshot snapshot = account.getSnapshot();

        snapshot.removeAccount(account);
        snapshotService.save(snapshot);

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final UpdateableTotals updateableTotals = new UpdateableTotals(snapshot, account);

        return SnapshotRemoveAccountJsonResponse.builder()
                .accountId(account.getId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .netWorth(updateableTotals.getNetWorth())
                .accountType(updateableTotals.getAccountType())
                .totalForAccountType(updateableTotals.getTotalForAccountType())
                .accountSubtype(updateableTotals.getBalanceUpdateableSubtype())
                .totalForAccountSubtype(updateableTotals.getTotalForAccountSubtype())
                .investmentTotals(updateableTotals.getInvestmentTotals())
                .creditCardTotalsForCurrencyUnit(
                        updateableTotals.getCreditCardTotalsForCurrencyUnit())
                .build();
    }
}
