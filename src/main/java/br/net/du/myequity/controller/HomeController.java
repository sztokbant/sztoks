package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import br.net.du.myequity.viewmodel.UserViewModel;
import br.net.du.myequity.viewmodel.WorkspaceViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.WORKSPACES_KEY;
import static java.util.stream.Collectors.toList;

@Controller
public class HomeController extends BaseController {

    @GetMapping("/")
    public String get(final Model model) {
        final User user = getCurrentUser();

        model.addAttribute(USER_KEY, UserViewModel.of(user));

        final List<WorkspaceViewModel> workspaceViewModels =
                user.getWorkspaces().stream().map(WorkspaceViewModel::of).collect(toList());

        model.addAttribute(WORKSPACES_KEY, workspaceViewModels);

        return "home";
    }
}
