package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import br.net.du.myequity.viewmodel.AccountViewModel;
import br.net.du.myequity.viewmodel.SnapshotViewModel;
import br.net.du.myequity.viewmodel.UserViewModel;
import br.net.du.myequity.viewmodel.WorkspaceViewModel;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerConstants.ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.WORKSPACE_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static java.util.stream.Collectors.toList;

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

        model.addAttribute(USER_KEY, UserViewModel.of(user));

        model.addAttribute(SNAPSHOT_KEY, SnapshotViewModel.of(snapshot));

        final Map<AccountType, Map<Account, BigDecimal>> accountsByType = snapshot.getAccountsByType();

        final Map<Account, BigDecimal> assetAccounts = accountsByType.get(AccountType.ASSET);
        model.addAttribute(ASSET_ACCOUNTS_KEY,
                           assetAccounts == null ?
                                   ImmutableList.of() :
                                   assetAccounts.entrySet().stream().map(AccountViewModel::of).collect(toList()));

        final Map<Account, BigDecimal> liabilityAccounts = accountsByType.get(AccountType.LIABILITY);
        model.addAttribute(LIABILITY_ACCOUNTS_KEY,
                           liabilityAccounts == null ?
                                   ImmutableList.of() :
                                   liabilityAccounts.entrySet().stream().map(AccountViewModel::of).collect(toList()));

        model.addAttribute(WORKSPACE_KEY, WorkspaceViewModel.of(snapshot.getWorkspace()));

        return "snapshot";
    }

    @PostMapping("/snapshot/{id}")
    public String post(@PathVariable(value = "id") final Long snapshotId,
                       @RequestParam("account_id") final Long accountId,
                       @RequestParam("balance_amount") final BigDecimal balanceAmount) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            // TODO Error message
            return "redirect:/";
        }

        final Snapshot snapshot = snapshotOpt.get();

        final Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountBelongsToUser(user, accountOpt) || snapshot.getAccount(accountOpt.get()) == null) {
            // TODO Error message
            return "redirect:/";
        }

        final Account account = accountOpt.get();

        snapshot.setAccount(account, balanceAmount);

        snapshotRepository.save(snapshot);

        return String.format("redirect:/snapshot/%s", snapshotId);
    }

    private boolean snapshotBelongsToUser(final User user, final Optional<Snapshot> snapshotOpt) {
        return snapshotOpt.isPresent() && snapshotOpt.get().getWorkspace().getUser().equals(user);
    }
}
