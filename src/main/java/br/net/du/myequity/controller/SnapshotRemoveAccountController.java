package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
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

import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;

@RestController
public class SnapshotRemoveAccountController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/removeAccountFromSnapshot")
    public SnapshotRemoveAccountJsonResponse removeAccountFromSnapshot(final Model model,
                                                                       @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final Optional<AccountSnapshot> accountSnapshotOpt = accountSnapshotRepository.findBySnapshotAndAccountId(
                snapshot,
                snapshotAccountUpdateJsonRequest.getAccountId());
        if (!accountSnapshotOpt.isPresent() || !snapshot.getAccountSnapshots().contains(accountSnapshotOpt.get())) {
            throw new IllegalArgumentException();
        }

        final AccountSnapshot accountSnapshot = accountSnapshotOpt.get();
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
