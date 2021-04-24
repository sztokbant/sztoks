package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.SnapshotRemoveAccountJsonResponse;
import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.totals.BalanceUpdateableSubtype;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.SnapshotService;
import java.util.Optional;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveAccountController {

    @Autowired protected SnapshotService snapshotService;

    @Autowired protected AccountService accountService;

    @Autowired protected SnapshotUtils snapshotUtils;

    @PostMapping("/snapshot/removeAccount")
    @Transactional
    public SnapshotRemoveAccountJsonResponse post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot =
                snapshotUtils.validateSnapshot(model, valueUpdateJsonRequest.getSnapshotId());

        final Optional<Account> accountOpt =
                accountService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(),
                        valueUpdateJsonRequest.getSnapshotId());

        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("account not found");
        }

        final Account account = accountOpt.get();

        snapshot.removeAccount(account);
        snapshotService.save(snapshot);

        return buildJsonResponse(snapshot, account);
    }

    private SnapshotRemoveAccountJsonResponse buildJsonResponse(
            final Snapshot snapshot, final Account account) {
        final CurrencyUnit currencyUnit = account.getCurrencyUnit();
        final UpdateableTotals updateableTotals = new UpdateableTotals(snapshot);

        final BalanceUpdateableSubtype balanceUpdateableSubtype =
                BalanceUpdateableSubtype.forClass(account.getClass());

        return SnapshotRemoveAccountJsonResponse.builder()
                .accountId(account.getId())
                .currencyUnit(currencyUnit.getCode())
                .currencyUnitSymbol(currencyUnit.getSymbol())
                .netWorth(updateableTotals.getNetWorth())
                .accountType(account.getAccountType().name())
                .totalForAccountType(updateableTotals.getTotalFor(account.getAccountType()))
                .accountSubtype(
                        balanceUpdateableSubtype == null ? null : balanceUpdateableSubtype.name())
                .totalForAccountSubtype(
                        balanceUpdateableSubtype == null
                                ? null
                                : updateableTotals.getTotalForAccountSubtype(
                                        balanceUpdateableSubtype))
                .investmentTotals(updateableTotals.getInvestmentTotals())
                .creditCardTotalsForCurrencyUnit(
                        updateableTotals.getCreditCardTotalsForCurrencyUnit(
                                account.getCurrencyUnit()))
                .build();
    }
}
