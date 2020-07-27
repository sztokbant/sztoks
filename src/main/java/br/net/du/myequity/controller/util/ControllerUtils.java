package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.viewmodel.AccountViewModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

import static java.util.stream.Collectors.toList;

public class ControllerUtils {
    public static boolean accountBelongsToUser(final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getUser().equals(user);
    }

    public static boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }

    public static boolean accountBelongsInSnapshot(final Snapshot snapshot, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && snapshot.getAccount(accountOpt.get()) != null;
    }

    public static Map<AccountType, List<AccountViewModel>> getAccountViewModels(final User user) {
        final Map<AccountType, SortedSet<Account>> accountsByType = user.getAccounts();

        final SortedSet<Account> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<Account> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(AccountType.ASSET,
                               assetAccounts == null ?
                                       ImmutableList.of() :
                                       assetAccounts.stream().map(AccountViewModel::of).collect(toList()),
                               AccountType.LIABILITY,
                               liabilityAccounts == null ?
                                       ImmutableList.of() :
                                       liabilityAccounts.stream().map(AccountViewModel::of).collect(toList()));
    }

    public static Map<AccountType, List<AccountViewModel>> getAccountViewModels(final Snapshot snapshot) {
        final Map<AccountType, Map<Account, BigDecimal>> accountsByType = snapshot.getAccountsByType();

        final Map<Account, BigDecimal> assetAccounts = accountsByType.get(AccountType.ASSET);
        final Map<Account, BigDecimal> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(AccountType.ASSET,
                               assetAccounts == null ?
                                       ImmutableList.of() :
                                       assetAccounts.entrySet().stream().map(AccountViewModel::of).collect(toList()),
                               AccountType.LIABILITY,
                               liabilityAccounts == null ?
                                       ImmutableList.of() :
                                       liabilityAccounts.entrySet()
                                                        .stream()
                                                        .map(AccountViewModel::of)
                                                        .collect(toList()));
    }
}
