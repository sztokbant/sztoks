package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.viewmodel.AccountViewModelOutput;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
        return accountOpt.isPresent() && snapshot.getAccountSnapshotFor(accountOpt.get()).isPresent();
    }

    public static Map<AccountType, List<AccountViewModelOutput>> getAccountViewModelOutputs(final User user) {
        final Map<AccountType, SortedSet<Account>> accountsByType = user.getAccounts();

        final SortedSet<Account> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<Account> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(AccountType.ASSET,
                               assetAccounts == null ?
                                       ImmutableList.of() :
                                       assetAccounts.stream().map(AccountViewModelOutput::of).collect(toList()),
                               AccountType.LIABILITY,
                               liabilityAccounts == null ?
                                       ImmutableList.of() :
                                       liabilityAccounts.stream().map(AccountViewModelOutput::of).collect(toList()));
    }

    public static Map<AccountType, List<AccountViewModelOutput>> getAccountViewModelOutputs(final Snapshot snapshot) {
        final Map<AccountType, SortedSet<AccountSnapshot>> accountsByType = snapshot.getAccountSnapshotsByType();

        final SortedSet<AccountSnapshot> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<AccountSnapshot> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(AccountType.ASSET,
                               assetAccounts == null ?
                                       ImmutableList.of() :
                                       assetAccounts.stream()
                                                    .map(AccountViewModelOutput::of)
                                                    .sorted()
                                                    .collect(toList()),
                               AccountType.LIABILITY,
                               liabilityAccounts == null ?
                                       ImmutableList.of() :
                                       liabilityAccounts.stream()
                                                        .map(AccountViewModelOutput::of)
                                                        .sorted()
                                                        .collect(toList()));
    }
}
