package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

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
        if (!snapshotOpt.isPresent() || !snapshotOpt.get().getWorkspace().getUser().equals(user)) {
            // TODO Error message
            return "redirect:/";
        }

        final Snapshot snapshot = snapshotOpt.get();

        model.addAttribute("user", user);
        model.addAttribute("snapshot", snapshot);
        model.addAttribute("assetMapKey", AccountType.ASSET);
        model.addAttribute("liabilityMapKey", AccountType.LIABILITY);

        return "snapshot";
    }

    @PostMapping("/snapshot/{id}")
    public String post(@PathVariable(value = "id") final Long snapshotId,
                       @RequestParam("account_id") final Long accountId,
                       @RequestParam("balance_amount") final BigDecimal balanceAmount) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotOpt.isPresent() || !snapshotOpt.get().getWorkspace().getUser().equals(user)) {
            // TODO Error message
            return "redirect:/";
        }

        final Snapshot snapshot = snapshotOpt.get();

        final Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent() || !accountOpt.get().getWorkspace().getUser().equals(user) || snapshot.getAccount(
                accountOpt.get()) == null) {
            // TODO Error message
            return "redirect:/";
        }

        final Account account = accountOpt.get();

        snapshot.setAccount(account, balanceAmount);

        snapshotRepository.save(snapshot);

        return String.format("redirect:/snapshot/%s", snapshotId);
    }
}