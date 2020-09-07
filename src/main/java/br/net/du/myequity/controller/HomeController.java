package br.net.du.myequity.controller;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.viewmodel.SimpleAccountViewModelOutput;
import br.net.du.myequity.viewmodel.SnapshotViewModelOutput;
import br.net.du.myequity.viewmodel.UserViewModelOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

import static br.net.du.myequity.controller.util.ControllerConstants.ASSET_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.LIABILITY_ACCOUNTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getAccountViewModelOutputs;
import static java.util.stream.Collectors.toList;

@Controller
public class HomeController extends BaseController {

    @GetMapping("/")
    public String get(final Model model) {
        final User user = getCurrentUser();

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        final List<SnapshotViewModelOutput> snapshotViewModelOutputs =
                user.getSnapshots().stream().map(SnapshotViewModelOutput::of).collect(toList());

        final Map<AccountType, List<SimpleAccountViewModelOutput>> accountViewModelOutputs =
                getAccountViewModelOutputs(user);
        model.addAttribute(ASSET_ACCOUNTS_KEY, accountViewModelOutputs.get(AccountType.ASSET));
        model.addAttribute(LIABILITY_ACCOUNTS_KEY, accountViewModelOutputs.get(AccountType.LIABILITY));

        model.addAttribute(SNAPSHOTS_KEY, snapshotViewModelOutputs);

        return "home";
    }
}
