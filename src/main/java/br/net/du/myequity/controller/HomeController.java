package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;
import static java.util.stream.Collectors.toList;

import br.net.du.myequity.controller.viewmodel.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String get(final Model model) {
        final User user = getLoggedUser(model);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        final List<SnapshotViewModelOutput> snapshotViewModelOutputs =
                user.getSnapshots().stream().map(SnapshotViewModelOutput::of).collect(toList());

        final Map<AccountType, List<AccountViewModelOutput>> accountViewModelOutputs =
                getAccountViewModelOutputs(user);
        model.addAttribute(ASSET_ACCOUNTS_KEY, accountViewModelOutputs.get(AccountType.ASSET));
        model.addAttribute(
                LIABILITY_ACCOUNTS_KEY, accountViewModelOutputs.get(AccountType.LIABILITY));

        model.addAttribute(SNAPSHOTS_KEY, snapshotViewModelOutputs);

        return "home";
    }

    private static Map<AccountType, List<AccountViewModelOutput>> getAccountViewModelOutputs(
            final User user) {
        final Map<AccountType, SortedSet<Account>> accountsByType = user.getAccounts();

        final SortedSet<Account> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<Account> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        final List<AccountViewModelOutput> assetsList =
                (assetAccounts == null)
                        ? ImmutableList.of()
                        : assetAccounts.stream().map(AccountViewModelOutput::of).collect(toList());

        final List<AccountViewModelOutput> liabilitiesList =
                (liabilityAccounts == null)
                        ? ImmutableList.of()
                        : liabilityAccounts.stream()
                                .map(AccountViewModelOutput::of)
                                .collect(toList());

        return ImmutableMap.of(
                AccountType.ASSET, assetsList, AccountType.LIABILITY, liabilitiesList);
    }
}
