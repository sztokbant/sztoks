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
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerConstants.ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getAccountViewModels;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;
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

        final Map<AccountType, List<AccountViewModel>> accountViewModels = getAccountViewModels(snapshot);
        model.addAttribute(ASSET_ACCOUNTS_KEY, accountViewModels.get(AccountType.ASSET));
        model.addAttribute(LIABILITY_ACCOUNTS_KEY, accountViewModels.get(AccountType.LIABILITY));

        return "snapshot";
    }
}
