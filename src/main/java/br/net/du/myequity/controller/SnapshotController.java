package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.CREDIT_CARD_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.INVESTMENT_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.PAYABLE_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.RECEIVABLE_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SIMPLE_ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SIMPLE_LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.myequity.controller.util.ViewModelOutputUtils.getViewModelOutputFactoryMethod;
import static java.util.stream.Collectors.toList;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.InvestmentViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.PayableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.ReceivableViewModelOutput;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@WebController
public class SnapshotController {
    private static final String SNAPSHOT = "snapshot";

    @Autowired private SnapshotUtils snapshotUtils;

    @GetMapping("/snapshot/{id}")
    public String get(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotUtils.getSnapshot(model, snapshotId);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));

        addAccountsToModel(model, snapshot);

        return SNAPSHOT;
    }

    private void addAccountsToModel(final Model model, final Snapshot snapshot) {
        final Map<AccountType, List<AccountSnapshotViewModelOutput>> accountSnapshotViewModels =
                getAccountSnapshotViewModelOutputs(snapshot);

        final Map<String, List<AccountSnapshotViewModelOutput>> assetsByType =
                breakDownAccountsByType(accountSnapshotViewModels.get(AccountType.ASSET));
        model.addAttribute(
                SIMPLE_ASSET_ACCOUNTS_KEY,
                assetsByType.get(AccountSnapshotViewModelOutput.class.getSimpleName()));
        model.addAttribute(
                RECEIVABLE_ACCOUNTS_KEY,
                assetsByType.get(ReceivableViewModelOutput.class.getSimpleName()));
        model.addAttribute(
                INVESTMENT_ACCOUNTS_KEY,
                assetsByType.get(InvestmentViewModelOutput.class.getSimpleName()));

        final Map<String, List<AccountSnapshotViewModelOutput>> liabilitiesByType =
                breakDownAccountsByType(accountSnapshotViewModels.get(AccountType.LIABILITY));
        model.addAttribute(
                SIMPLE_LIABILITY_ACCOUNTS_KEY,
                liabilitiesByType.get(AccountSnapshotViewModelOutput.class.getSimpleName()));
        model.addAttribute(
                PAYABLE_ACCOUNTS_KEY,
                liabilitiesByType.get(PayableViewModelOutput.class.getSimpleName()));
        model.addAttribute(
                CREDIT_CARD_ACCOUNTS_KEY,
                liabilitiesByType.get(CreditCardViewModelOutput.class.getSimpleName()));
    }

    private static Map<AccountType, List<AccountSnapshotViewModelOutput>>
            getAccountSnapshotViewModelOutputs(final Snapshot snapshot) {
        final Map<AccountType, SortedSet<AccountSnapshot>> accountSnapshotsByType =
                snapshot.getAccountSnapshotsByType();

        final SortedSet<AccountSnapshot> assetAccountSnapshots =
                accountSnapshotsByType.get(AccountType.ASSET);
        final SortedSet<AccountSnapshot> liabilityAccountSnapshots =
                accountSnapshotsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(
                AccountType.ASSET,
                (assetAccountSnapshots == null)
                        ? ImmutableList.of()
                        : getViewModelOutputs(assetAccountSnapshots),
                AccountType.LIABILITY,
                (liabilityAccountSnapshots == null)
                        ? ImmutableList.of()
                        : getViewModelOutputs(liabilityAccountSnapshots));
    }

    private static List<AccountSnapshotViewModelOutput> getViewModelOutputs(
            final SortedSet<AccountSnapshot> accountSnapshots) {
        return accountSnapshots.stream()
                .map(
                        accountSnapshot -> {
                            try {
                                final Method factoryMethod =
                                        getViewModelOutputFactoryMethod(accountSnapshot.getClass());
                                return (AccountSnapshotViewModelOutput)
                                        factoryMethod.invoke(null, accountSnapshot);
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                .sorted()
                .collect(toList());
    }

    private Map<String, List<AccountSnapshotViewModelOutput>> breakDownAccountsByType(
            final List<AccountSnapshotViewModelOutput> accounts) {
        final Map<String, List<AccountSnapshotViewModelOutput>> accountsByType = new HashMap<>();

        for (final AccountSnapshotViewModelOutput account : accounts) {
            final String key = account.getClass().getSimpleName();
            if (!accountsByType.containsKey(key)) {
                accountsByType.put(key, new ArrayList<>());
            }
            accountsByType.get(key).add(account);
        }

        return accountsByType;
    }
}
