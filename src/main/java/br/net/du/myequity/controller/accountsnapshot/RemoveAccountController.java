package br.net.du.myequity.controller.accountsnapshot;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

import br.net.du.myequity.controller.viewmodel.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.SnapshotRemoveAccountJsonResponse;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import org.joda.money.CurrencyUnit;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveAccountController extends UpdateControllerBase {

    @PostMapping("/snapshot/removeAccount")
    public SnapshotRemoveAccountJsonResponse post(
            final Model model,
            @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        final AccountSnapshot accountSnapshot =
                getAccountSnapshot(model, accountSnapshotUpdateJsonRequest);
        final Snapshot snapshot = accountSnapshot.getSnapshot();

        snapshot.removeAccountSnapshot(accountSnapshot);
        snapshotService.save(snapshot);

        final Account account = accountSnapshot.getAccount();
        final CurrencyUnit currencyUnit = account.getCurrencyUnit();
        final AccountType accountType = account.getAccountType();

        final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit =
                getCreditCardTotalsViewModelOutput(accountSnapshot, snapshot, currencyUnit);

        return SnapshotRemoveAccountJsonResponse.builder()
                .accountId(account.getId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .netWorth(formatAsDecimal(snapshot.getNetWorth().get(currencyUnit)))
                .accountType(accountType.name())
                .totalForAccountType(
                        formatAsDecimal(
                                snapshot.getTotalForAccountType(accountType).get(currencyUnit)))
                .creditCardTotalsForCurrencyUnit(creditCardTotalsForCurrencyUnit)
                .build();
    }

    public CreditCardTotalsViewModelOutput getCreditCardTotalsViewModelOutput(
            final AccountSnapshot accountSnapshot,
            final Snapshot snapshot,
            final CurrencyUnit currencyUnit) {
        final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit;
        if (accountSnapshot instanceof CreditCardSnapshot) {
            final CreditCardSnapshot creditCardTotalsSnapshot =
                    snapshot.getCreditCardTotalsForCurrencyUnit(currencyUnit);

            if (creditCardTotalsSnapshot != null) {
                creditCardTotalsForCurrencyUnit =
                        CreditCardTotalsViewModelOutput.of(creditCardTotalsSnapshot);
            } else {
                creditCardTotalsForCurrencyUnit =
                        CreditCardTotalsViewModelOutput.newEmptyInstance(currencyUnit);
            }
        } else {
            creditCardTotalsForCurrencyUnit = null;
        }
        return creditCardTotalsForCurrencyUnit;
    }
}
