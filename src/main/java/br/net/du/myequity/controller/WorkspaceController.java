package br.net.du.myequity.controller;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.Workspace;
import br.net.du.myequity.persistence.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

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

        model.addAttribute("user", user);
        model.addAttribute("workspace", workspaceOpt.get());
        model.addAttribute("assetMapKey", AccountType.ASSET);
        model.addAttribute("liabilityMapKey", AccountType.LIABILITY);

        return "workspace";
    }
}