package br.net.du.myequity.controller.util;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.viewmodel.CreditCardViewModelOutput;
import br.net.du.myequity.viewmodel.SimpleAccountViewModelOutput;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

import static br.net.du.myequity.controller.util.ControllerConstants.LOGGED_USER;
import static java.util.stream.Collectors.toList;

public class ControllerUtils {

    public static User getLoggedUser(final Model model) {
        final Optional<User> loggedUserOpt = getLoggedUserOpt(model);
        if (!loggedUserOpt.isPresent()) {
            throw new RuntimeException();
        }
        return loggedUserOpt.get();
    }

    public static Optional<User> getLoggedUserOpt(final Model model) {
        return model.containsAttribute(LOGGED_USER) ?
                (Optional<User>) model.getAttribute(LOGGED_USER) :
                Optional.empty();
    }

    public static boolean accountBelongsToUser(final User user, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && accountOpt.get().getUser().equals(user);
    }

    public static boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getUser().equals(user);
    }

    public static boolean accountBelongsInSnapshot(final Snapshot snapshot, final Optional<Account> accountOpt) {
        return accountOpt.isPresent() && snapshot.getAccountSnapshotFor(accountOpt.get()).isPresent();
    }

    public static Map<AccountType, List<SimpleAccountViewModelOutput>> getAccountViewModelOutputs(final User user) {
        final Map<AccountType, SortedSet<Account>> accountsByType = user.getAccounts();

        final SortedSet<Account> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<Account> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(AccountType.ASSET,
                               assetAccounts == null ?
                                       ImmutableList.of() :
                                       assetAccounts.stream().map(SimpleAccountViewModelOutput::of).collect(toList()),
                               AccountType.LIABILITY,
                               liabilityAccounts == null ?
                                       ImmutableList.of() :
                                       liabilityAccounts.stream()
                                                        .map(SimpleAccountViewModelOutput::of)
                                                        .collect(toList()));
    }

    public static Map<AccountType, List<SimpleAccountViewModelOutput>> getAccountViewModelOutputs(final Snapshot snapshot) {
        final Map<AccountType, SortedSet<AccountSnapshot>> accountsByType = snapshot.getAccountSnapshotsByType();

        final SortedSet<AccountSnapshot> assetAccounts = accountsByType.get(AccountType.ASSET);
        final SortedSet<AccountSnapshot> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);

        return ImmutableMap.of(AccountType.ASSET,
                               assetAccounts == null ?
                                       ImmutableList.of() :
                                       getViewModelOutputs(assetAccounts),
                               AccountType.LIABILITY,
                               liabilityAccounts == null ?
                                       ImmutableList.of() :
                                       getViewModelOutputs(liabilityAccounts));
    }

    private static List<SimpleAccountViewModelOutput> getViewModelOutputs(final SortedSet<AccountSnapshot> accountSnapshots) {
        return accountSnapshots.stream().map(accountSnapshot -> {
            if (accountSnapshot instanceof CreditCardSnapshot) {
                return CreditCardViewModelOutput.of(accountSnapshot);
            }
            return SimpleAccountViewModelOutput.of(accountSnapshot);
        }).sorted().collect(toList());
    }
}
