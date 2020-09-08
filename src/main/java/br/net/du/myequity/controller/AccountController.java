package br.net.du.myequity.controller;

import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.UserRepository;
import br.net.du.myequity.validator.AccountViewModelInputValidator;
import br.net.du.myequity.viewmodel.AccountViewModelInput;
import br.net.du.myequity.viewmodel.UserViewModelOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

@Controller
public class AccountController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountViewModelInputValidator accountViewModelInputValidator;

    @GetMapping("/newaccount")
    public String newAccount(final Model model) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));

        model.addAttribute("accountForm", new AccountViewModelInput());

        return "new_account";
    }

    @PostMapping("/newaccount")
    public String newAccount(final Model model,
                             @ModelAttribute("accountForm") final AccountViewModelInput accountViewModelInput,
                             final BindingResult bindingResult) {
        final User user = getLoggedUser(model);
        accountViewModelInputValidator.validate(accountViewModelInput, bindingResult, user);

        if (bindingResult.hasErrors()) {
            return "new_account";
        }

        user.addAccount(accountViewModelInput.toAccount());
        userRepository.save(user);

        return "redirect:/";
    }
}
