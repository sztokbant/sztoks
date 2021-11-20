package br.net.du.myequity.controller.account;

import static br.net.du.myequity.controller.util.AccountUtils.hasFutureTithingImpact;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.SnapshotRemoveAccountJsonResponse;
import br.net.du.myequity.controller.viewmodel.UpdatableTotals;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.CreditCardAccount;
import br.net.du.myequity.model.account.FutureTithingAccount;
import br.net.du.myequity.model.account.FutureTithingCapable;
import br.net.du.myequity.model.account.FutureTithingPolicy;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.account.TithingAccount;
import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
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
                snapshotUtils.validateLockAndRefreshSnapshot(
                        model, valueUpdateJsonRequest.getSnapshotId());

        final Optional<Account> accountOpt =
                accountService.findByIdAndSnapshotId(
                        valueUpdateJsonRequest.getEntityId(),
                        valueUpdateJsonRequest.getSnapshotId());

        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("account not found");
        }

        final Account account = accountOpt.get();

        if (account instanceof TithingAccount || account instanceof FutureTithingAccount) {
            throw new IllegalArgumentException("account not found");
        }

        final Optional<Account> futureTithingAccountOpt =
                hasFutureTithingImpact(account)
                        ? Optional.of(snapshot.getFutureTithingAccount(account.getCurrencyUnit()))
                        : Optional.empty();

        snapshot.removeAccount(account);

        if (futureTithingAccountOpt.isPresent()) {
            accountService.save(futureTithingAccountOpt.get());
        }

        snapshotService.save(snapshot);

        return buildJsonResponse(snapshot, account);
    }

    private SnapshotRemoveAccountJsonResponse buildJsonResponse(
            final Snapshot snapshot, final Account account) {
        final CurrencyUnit currencyUnit = account.getCurrencyUnit();
        final UpdatableTotals updatableTotals = new UpdatableTotals(snapshot);

        final String totalForAccountType = updatableTotals.getTotalFor(account.getAccountType());

        final SnapshotRemoveAccountJsonResponse.SnapshotRemoveAccountJsonResponseBuilder builder =
                SnapshotRemoveAccountJsonResponse.builder()
                        .accountId(account.getId())
                        .currencyUnit(currencyUnit.getCode())
                        .currencyUnitSymbol(currencyUnit.getSymbol())
                        .netWorth(updatableTotals.getNetWorth())
                        .accountType(account.getAccountType().name())
                        .totalForAccountType(totalForAccountType);

        final AccountSubtypeDisplayGroup accountSubtypeDisplayGroup =
                AccountSubtypeDisplayGroup.forClass(account.getClass());
        if (accountSubtypeDisplayGroup != null) {
            builder.accountSubtype(accountSubtypeDisplayGroup.name())
                    .totalForAccountSubtype(
                            updatableTotals.getTotalForAccountSubtype(accountSubtypeDisplayGroup));
        }

        if (account instanceof InvestmentAccount) {
            builder.investmentTotals(updatableTotals.getInvestmentTotals());
        } else if (account instanceof CreditCardAccount) {
            builder.creditCardTotalsForCurrencyUnit(
                    updatableTotals.getCreditCardTotalsForCurrencyUnit(account.getCurrencyUnit()));
        }

        if (account instanceof FutureTithingCapable
                && !((FutureTithingCapable) account)
                        .getFutureTithingPolicy()
                        .equals(FutureTithingPolicy.NONE)) {
            final String totalLiability =
                    account.getAccountType().equals(AccountType.LIABILITY)
                            ? totalForAccountType
                            : updatableTotals.getTotalFor(AccountType.LIABILITY);
            builder.futureTithingBalance(updatableTotals.getFutureTithingBalance())
                    .totalTithingBalance(
                            updatableTotals.getTotalForAccountSubtype(
                                    AccountSubtypeDisplayGroup.TITHING))
                    .totalLiability(totalLiability);
        }

        return builder.build();
    }
}
