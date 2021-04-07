package br.net.du.myequity.controller.account;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

import br.net.du.myequity.controller.viewmodel.SnapshotRemoveAccountJsonResponse;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.CreditCardTotalsViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentTotalsViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.totals.SnapshotTotalsCalculator;
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
        final SnapshotTotalsCalculator snapshotTotalsCalculator =
                new SnapshotTotalsCalculator(snapshot);

        snapshot.removeAccount(account);
        snapshotService.save(snapshot);

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();
        final AccountType accountType = account.getAccountType();

        final InvestmentTotalsViewModelOutput investmentTotals =
                (account instanceof InvestmentAccount)
                        ? InvestmentTotalsViewModelOutput.of(
                                snapshotTotalsCalculator.getInvestmentTotals())
                        : null;

        final CreditCardTotalsViewModelOutput creditCardTotalsForCurrencyUnit =
                (account instanceof CreditCardAccount)
                        ? CreditCardTotalsViewModelOutput.of(
                                snapshotTotalsCalculator.getCreditCardTotalsForCurrency(
                                        currencyUnit))
                        : null;

        return SnapshotRemoveAccountJsonResponse.builder()
                .accountId(account.getId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .netWorth(formatAsDecimal(snapshot.getNetWorth()))
                .accountType(accountType.name())
                .totalForAccountType(formatAsDecimal(snapshot.getTotalFor(accountType)))
                .creditCardTotalsForCurrencyUnit(creditCardTotalsForCurrencyUnit)
                .build();
    }
}
