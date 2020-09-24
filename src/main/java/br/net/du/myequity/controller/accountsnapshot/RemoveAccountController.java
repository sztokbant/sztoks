package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotRemoveAccountJsonResponse;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import org.joda.money.CurrencyUnit;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

@RestController
public class RemoveAccountController extends UpdateControllerBase {

    @PostMapping("/snapshot/removeAccount")
    public SnapshotRemoveAccountJsonResponse post(final Model model,
                                                  @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        final AccountSnapshot accountSnapshot = getAccountSnapshot(model, accountSnapshotUpdateJsonRequest);
        final Snapshot snapshot = accountSnapshot.getSnapshot();

        snapshot.removeAccountSnapshot(accountSnapshot);
        snapshotRepository.save(snapshot);

        final Account account = accountSnapshot.getAccount();
        final CurrencyUnit currencyUnit = account.getCurrencyUnit();
        final AccountType accountType = account.getAccountType();

        return SnapshotRemoveAccountJsonResponse.builder()
                                                .accountId(account.getId())
                                                .currencyUnit(currencyUnit.getCode())
                                                .currencyUnitSymbol(currencyUnit.getSymbol())
                                                .netWorth(formatAsDecimal(snapshot.getNetWorth().get(currencyUnit)))
                                                .accountType(accountType.name())
                                                .totalForAccountType(formatAsDecimal(snapshot.getTotalForAccountType(
                                                        accountType).get(currencyUnit)))
                                                .build();
    }
}
