package br.net.du.myequity.controller;

import br.net.du.myequity.controller.util.ControllerUtils;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.viewmodel.AccountViewModelOutput;
import br.net.du.myequity.viewmodel.AddAccountsViewModelInput;
import br.net.du.myequity.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.viewmodel.UserViewModelOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.net.du.myequity.controller.util.ControllerConstants.ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

@Controller
public class SnapshotController extends BaseController {
    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/snapshot/{id}")
    public String get(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            // TODO Error message
            return "redirect:/";
        }

        final Snapshot snapshot = snapshotOpt.get();

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));

        final Map<AccountType, List<AccountViewModelOutput>> accountViewModels =
                ControllerUtils.getAccountViewModelOutputs(snapshot);
        model.addAttribute(ASSET_ACCOUNTS_KEY, accountViewModels.get(AccountType.ASSET));
        model.addAttribute(LIABILITY_ACCOUNTS_KEY, accountViewModels.get(AccountType.LIABILITY));

        return "snapshot";
    }

    @GetMapping("/addaccounts/{id}")
    public String addAccounts(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            // TODO Error message
            return "redirect:/";
        }

        final Snapshot snapshot = snapshotOpt.get();
        final List<Account> allUserAccounts = accountRepository.findByUser(user);

        final List<AccountViewModelOutput> availableAssets = allUserAccounts.stream()
                                                                            .filter(account -> account.getAccountType()
                                                                                                      .equals(AccountType.ASSET) && !snapshot
                                                                                    .getAccountSnapshotFor(account)
                                                                                    .isPresent())
                                                                            .map(AccountViewModelOutput::of)
                                                                            .collect(Collectors.toList());

        final List<AccountViewModelOutput> availableLiabilities = allUserAccounts.stream()
                                                                                 .filter(account -> account.getAccountType()
                                                                                                           .equals(AccountType.LIABILITY) && !snapshot
                                                                                         .getAccountSnapshotFor(account)
                                                                                         .isPresent())
                                                                                 .map(AccountViewModelOutput::of)
                                                                                 .collect(Collectors.toList());

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));
        model.addAttribute("assets", availableAssets);
        model.addAttribute("liabilities", availableLiabilities);
        model.addAttribute("addAccountsForm", new AddAccountsViewModelInput());

        return "add_accounts";
    }

    @PostMapping("/addaccounts/{id}")
    public String addAccounts(@PathVariable(value = "id") final Long snapshotId,
                              @ModelAttribute(
                                      "addAccountsForm") final AddAccountsViewModelInput addAccountsViewModelInput,
                              final BindingResult bindingResult) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            // TODO Error message
            return "redirect:/";
        }

        if (!addAccountsViewModelInput.getAccounts().isEmpty()) {
            final Snapshot snapshot = snapshotOpt.get();

            final List<Account> allUserAccounts = accountRepository.findByUser(user);
            allUserAccounts.stream()
                           .filter(account -> addAccountsViewModelInput.getAccounts().contains(account.getId()))
                           .forEach(account -> snapshot.addAccountSnapshot(account.newEmptySnapshot()));

            snapshotRepository.save(snapshot);
        }

        return "redirect:/snapshot/" + snapshotId;
    }
}
