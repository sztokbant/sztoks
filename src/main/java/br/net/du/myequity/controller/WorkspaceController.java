package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import br.net.du.myequity.persistence.WorkspaceRepository;
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

import java.util.List;
import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerConstants.ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.WORKSPACE_KEY;
import static java.util.stream.Collectors.toList;

@Controller
public class WorkspaceController extends BaseController {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @GetMapping("/workspace/{id}")
    public String get(@PathVariable(value = "id") final Long workspaceId, final Model model) {
        final User user = getCurrentUser();

        final Optional<Workspace> workspaceOpt = workspaceRepository.findById(workspaceId);
        if (!workspaceOpt.isPresent() || !workspaceOpt.get().getUser().equals(user)) {
            // TODO Error message
            return "redirect:/";
        }

        model.addAttribute(USER_KEY, UserViewModel.of(user));

        final Workspace workspace = workspaceOpt.get();

        model.addAttribute(SNAPSHOTS_KEY,
                           workspace.getSnapshots().stream().map(SnapshotViewModel::of).collect(toList()));

        model.addAttribute(WORKSPACE_KEY, WorkspaceViewModel.of(workspace));

        final List<Account> assetAccounts = workspace.getAccounts().get(AccountType.ASSET);
        model.addAttribute(ASSET_ACCOUNTS_KEY,
                           assetAccounts == null ?
                                   ImmutableList.of() :
                                   assetAccounts.stream().map(AccountViewModel::of).collect(toList()));

        final List<Account> liabilityAccounts = workspace.getAccounts().get(AccountType.LIABILITY);
        model.addAttribute(LIABILITY_ACCOUNTS_KEY,
                           liabilityAccounts == null ?
                                   ImmutableList.of() :
                                   liabilityAccounts.stream().map(AccountViewModel::of).collect(toList()));

        return "workspace";
    }
}