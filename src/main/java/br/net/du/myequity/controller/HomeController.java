package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOTS_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.viewmodel.HomeSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String get(final Model model) {
        final User user = getLoggedUser(model);

        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        final List<HomeSnapshotViewModelOutput> homeSnapshotViewModelOutputs =
                user.getSnapshots().stream()
                        .map(
                                snapshot ->
                                        new HomeSnapshotViewModelOutput(
                                                snapshot.getId(),
                                                snapshot.getName(),
                                                new UpdateableTotals(snapshot).getNetWorth()))
                        .collect(Collectors.toList());

        model.addAttribute(SNAPSHOTS_KEY, homeSnapshotViewModelOutputs);

        return "home";
    }
}
