package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.AddAccountsToSnapshotViewModelInput;
import br.net.du.myequity.controller.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.service.AccountService;
import br.net.du.myequity.service.AccountSnapshotService;
import br.net.du.myequity.service.SnapshotService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@WebController
public class SnapshotAddAccountController {
    private static final String ADD_ACCOUNTS_FORM = "addAccountsForm";

    @Autowired private SnapshotService snapshotService;

    @Autowired private AccountService accountService;

    @Autowired private AccountSnapshotService accountSnapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @GetMapping("/snapshot/addAccounts/{id}")
    public String addAccounts(
            @PathVariable(value = "id") final Long snapshotId, final Model model) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotUtils.getSnapshot(model, snapshotId);

        final List<Account> allUserAccounts = accountService.findByUser(user);

        final List<AccountViewModelOutput> availableAssets =
                allUserAccounts.stream()
                        .filter(
                                account ->
                                        account.getAccountType().equals(AccountType.ASSET)
                                                && !snapshot.getAccountSnapshotFor(account)
                                                        .isPresent())
                        .map(AccountViewModelOutput::of)
                        .collect(Collectors.toList());

        final List<AccountViewModelOutput> availableLiabilities =
                allUserAccounts.stream()
                        .filter(
                                account ->
                                        account.getAccountType().equals(AccountType.LIABILITY)
                                                && !snapshot.getAccountSnapshotFor(account)
                                                        .isPresent())
                        .map(AccountViewModelOutput::of)
                        .collect(Collectors.toList());

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModelOutput.of(snapshot));
        model.addAttribute("assets", availableAssets);
        model.addAttribute("liabilities", availableLiabilities);
        model.addAttribute(ADD_ACCOUNTS_FORM, new AddAccountsToSnapshotViewModelInput());

        return "add_accounts";
    }

    @PostMapping("/snapshot/addAccounts/{id}")
    public String addAccounts(
            @PathVariable(value = "id") final Long snapshotId,
            final Model model,
            @ModelAttribute(ADD_ACCOUNTS_FORM)
                    final AddAccountsToSnapshotViewModelInput addAccountsViewModelInput,
            final BindingResult bindingResult) {
        final User user = getLoggedUser(model);
        final Snapshot snapshot = snapshotUtils.getSnapshot(model, snapshotId);

        if (addAccountsViewModelInput.getAccounts() != null
                && !addAccountsViewModelInput.getAccounts().isEmpty()) {
            final List<Account> allUserAccounts = accountService.findByUser(user);

            final List<Account> accountsToBeAdded =
                    allUserAccounts.stream()
                            .filter(
                                    account -> {
                                        return
                                        // Input account actually belongs to user
                                        addAccountsViewModelInput
                                                        .getAccounts()
                                                        .contains(account.getId())
                                                &&
                                                // Account not yet in snapshot
                                                !snapshot.getAccountSnapshotFor(account)
                                                        .isPresent();
                                    })
                            .collect(Collectors.toList());

            if (!accountsToBeAdded.isEmpty()) {
                accountsToBeAdded.forEach(
                        account -> snapshot.addAccountSnapshot(account.newEmptySnapshot()));
                snapshotService.save(snapshot);
            }
        }

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshotId);
    }
}
