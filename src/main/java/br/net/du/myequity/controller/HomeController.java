package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {
    @GetMapping("/")
    public String get(final Model model) {
        final User user = getCurrentUser();
        model.addAttribute("user", user);

        return "home";
    }
}
