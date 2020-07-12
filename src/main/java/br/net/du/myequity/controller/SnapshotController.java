package br.net.du.myequity.controller;

import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class SnapshotController extends BaseController {
    @Autowired
    private SnapshotRepository snapshotRepository;

    @GetMapping("/snapshot/{id}")
    public String get(@PathVariable(value = "id") final Long snapshotId, final Model model) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(snapshotId);
        if (!snapshotOpt.isPresent() || !snapshotOpt.get().getWorkspace().getUser().equals(user)) {
            // TODO Error message
            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("snapshot", snapshotOpt.get());
        model.addAttribute("assetMapKey", AccountType.ASSET);
        model.addAttribute("liabilityMapKey", AccountType.LIABILITY);

        return "snapshot";
    }
}