package br.net.du.myequity.controller;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.service.SnapshotService;
import br.net.du.myequity.viewmodel.AccountViewModelOutputBase;
import br.net.du.myequity.viewmodel.AddAccountsToSnapshotViewModelInput;
import br.net.du.myequity.viewmodel.CreditCardViewModelOutput;
import br.net.du.myequity.viewmodel.InvestmentViewModelOutput;
import br.net.du.myequity.viewmodel.PayableViewModelOutput;
import br.net.du.myequity.viewmodel.ReceivableViewModelOutput;
import br.net.du.myequity.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.viewmodel.UserViewModelOutput;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static br.net.du.myequity.controller.util.ControllerConstants.CREDIT_CARD_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.INVESTMENT_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.PAYABLE_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.RECEIVABLE_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerConstants.SIMPLE_ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SIMPLE_LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUserOpt;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;
import static java.util.stream.Collectors.toList;

@Controller
public class SnapshotController {
    private static final String ADD_ACCOUNTS_FORM = "addAccountsForm";
    private static final String REDIRECT_SNAPSHOT_TEMPLATE = "redirect:/snapshot/%d";
    private static final String SNAPSHOT = "snapshot";

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountSnapshotRepository accountSnapshotRepository;

    @GetMapping("/snapshot/{id}")
    public String get(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final Optional<User> userOpt = getLoggedUserOpt(model);

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!userOpt.isPresent() || !snapshotBelongsToUser(userOpt.get(), snapshotOpt)) {
            // TODO Error message
            return REDIRECT_TO_HOME;
        }

        final User user = userOpt.get();
        final Snapshot snapshot = snapshotOpt.get();

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));

        addAccountsToModel(model, snapshot);

        return SNAPSHOT;
    }

    @PostMapping("/snapshot/new")
    public String copy(final Model model) {
        final Optional<User> userOpt = getLoggedUserOpt(model);

        if (!userOpt.isPresent()) {
            // TODO Error message
            return REDIRECT_TO_HOME;
        }

        final User user = userOpt.get();

        final Snapshot newSnapshot = snapshotService.newSnapshot(user);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, newSnapshot.getId());
    }

    @PostMapping("/snapshot/delete/{id}")
    public String delete(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final Optional<User> userOpt = getLoggedUserOpt(model);

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!userOpt.isPresent() || !snapshotBelongsToUser(userOpt.get(), snapshotOpt)) {
            // TODO Error message
            return REDIRECT_TO_HOME;
        }

        final User user = userOpt.get();
        final Snapshot snapshot = snapshotOpt.get();

        snapshotService.deleteSnapshot(user, snapshot);

        return REDIRECT_TO_HOME;
    }

    private void addAccountsToModel(final Model model, final Snapshot snapshot) {
        final Map<AccountType, List<AccountViewModelOutputBase>> accountViewModels =
                getAccountViewModelOutputs(snapshot);

        final Map<String, List<AccountViewModelOutputBase>> assetsByType =
                breakDownAccountsByType(accountViewModels.get(AccountType.ASSET));
        model.addAttribute(SIMPLE_ASSET_ACCOUNTS_KEY,
                           assetsByType.get(AccountViewModelOutputBase.class.getSimpleName()));
        model.addAttribute(RECEIVABLE_ACCOUNTS_KEY, assetsByType.get(ReceivableViewModelOutput.class.getSimpleName()));
        model.addAttribute(INVESTMENT_ACCOUNTS_KEY, assetsByType.get(InvestmentViewModelOutput.class.getSimpleName()));

        final Map<String, List<AccountViewModelOutputBase>> liabilitiesByType =
                breakDownAccountsByType(accountViewModels.get(AccountType.LIABILITY));
        model.addAttribute(SIMPLE_LIABILITY_ACCOUNTS_KEY,
                           liabilitiesByType.get(AccountViewModelOutputBase.class.getSimpleName()));
        model.addAttribute(PAYABLE_ACCOUNTS_KEY, liabilitiesByType.get(PayableViewModelOutput.class.getSimpleName()));
        model.addAttribute(CREDIT_CARD_ACCOUNTS_KEY,
                           liabilitiesByType.get(CreditCardViewModelOutput.class.getSimpleName()));
    }

    private static Map<AccountType, List<AccountViewModelOutputBase>> getAccountViewModelOutputs(final Snapshot snapshot) {
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

    private static List<AccountViewModelOutputBase> getViewModelOutputs(final SortedSet<AccountSnapshot> accountSnapshots) {
        return accountSnapshots.stream().map(accountSnapshot -> {
            try {
                final Method factoryMethod = getViewModelOutputFactoryMethod(accountSnapshot.getClass());
                final AccountViewModelOutputBase accountViewModelOutputBase =
                        (AccountViewModelOutputBase) factoryMethod.invoke(null, accountSnapshot);
                return accountViewModelOutputBase;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }).sorted().collect(toList());
    }

    private static Method getViewModelOutputFactoryMethod(@NonNull final Class<? extends AccountSnapshot> clazz)
            throws ClassNotFoundException, NoSuchMethodException {
        return getViewModelOutputClass(clazz).getMethod("of", AccountSnapshot.class);
    }

    @VisibleForTesting
    static Class getViewModelOutputClass(@NonNull final Class<? extends AccountSnapshot> clazz)
            throws ClassNotFoundException {
        final String className = clazz.getSimpleName();
        final String prefix = className.split("Snapshot")[0];

        return Class.forName(String.format("%s.%s%s",
                                           AccountViewModelOutputBase.class.getPackage().getName(),
                                           prefix,
                                           "ViewModelOutput"));
    }

    private Map<String, List<AccountViewModelOutputBase>> breakDownAccountsByType(final List<AccountViewModelOutputBase> accounts) {
        final Map<String, List<AccountViewModelOutputBase>> accountsByType = new HashMap<>();

        for (final AccountViewModelOutputBase account : accounts) {
            final String key = account.getClass().getSimpleName();
            if (!accountsByType.containsKey(key)) {
                accountsByType.put(key, new ArrayList<>());
            }
            accountsByType.get(key).add(account);
        }

        return accountsByType;
    }

    @GetMapping("/snapshot/addAccounts/{id}")
    public String addAccounts(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final Optional<User> userOpt = getLoggedUserOpt(model);

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!userOpt.isPresent() || !snapshotBelongsToUser(userOpt.get(), snapshotOpt)) {
            // TODO Error message
            return REDIRECT_TO_HOME;
        }

        final User user = userOpt.get();
        final Snapshot snapshot = snapshotOpt.get();

        final List<Account> allUserAccounts = accountRepository.findByUser(user);

        final List<AccountViewModelOutputBase> availableAssets = allUserAccounts.stream()
                                                                                .filter(account ->
                                                                                                account.getAccountType()
                                                                                                       .equals(AccountType.ASSET) &&
                                                                                                        !snapshot.getAccountSnapshotFor(
                                                                                                                account)
                                                                                                                 .isPresent())
                                                                                .map(AccountViewModelOutputBase::of)
                                                                                .collect(Collectors.toList());

        final List<AccountViewModelOutputBase> availableLiabilities = allUserAccounts.stream()
                                                                                     .filter(account ->
                                                                                                     account.getAccountType()
                                                                                                            .equals(AccountType.LIABILITY) &&
                                                                                                             !snapshot.getAccountSnapshotFor(
                                                                                                                     account)
                                                                                                                      .isPresent())
                                                                                     .map(AccountViewModelOutputBase::of)
                                                                                     .collect(Collectors.toList());

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));
        model.addAttribute("assets", availableAssets);
        model.addAttribute("liabilities", availableLiabilities);
        model.addAttribute(ADD_ACCOUNTS_FORM, new AddAccountsToSnapshotViewModelInput());

        return "add_accounts";
    }

    @PostMapping("/snapshot/addAccounts/{id}")
    public String addAccounts(@PathVariable(value = "id") final Long snapshotId,
                              final Model model,
                              @ModelAttribute(
                                      ADD_ACCOUNTS_FORM) final AddAccountsToSnapshotViewModelInput addAccountsViewModelInput,
                              final BindingResult bindingResult) {
        final Optional<User> userOpt = getLoggedUserOpt(model);

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!userOpt.isPresent() || !snapshotBelongsToUser(userOpt.get(), snapshotOpt)) {
            // TODO Error message
            return REDIRECT_TO_HOME;
        }

        if (!addAccountsViewModelInput.getAccounts().isEmpty()) {
            final User user = userOpt.get();
            final Snapshot snapshot = snapshotOpt.get();

            final List<Account> allUserAccounts = accountRepository.findByUser(user);
            allUserAccounts.stream()
                           .filter(account -> addAccountsViewModelInput.getAccounts().contains(account.getId()))
                           .forEach(account -> snapshot.addAccountSnapshot(account.newEmptySnapshot()));

            snapshotRepository.save(snapshot);
        }

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshotId);
    }
}
