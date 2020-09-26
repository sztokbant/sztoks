package br.net.du.myequity.controller;

import br.net.du.myequity.controller.viewmodel.AccountViewModelInput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.UserRepository;
import br.net.du.myequity.validator.AccountViewModelInputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_TO_HOME;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

@Controller
public class AccountController {
    public static final String ACCOUNT_FORM = "accountForm";
    private static final String NEW_ACCOUNT_TEMPLATE = "new_account";
    private static final String NEWACCOUNT_MAPPING = "/newaccount";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountViewModelInputValidator accountViewModelInputValidator;

    @GetMapping(NEWACCOUNT_MAPPING)
    public String newAccount(final Model model) {
        model.addAttribute(USER_KEY, UserViewModelOutput.of(getLoggedUser(model)));
        model.addAttribute(ACCOUNT_FORM, new AccountViewModelInput());

        return NEW_ACCOUNT_TEMPLATE;
    }

    @PostMapping(NEWACCOUNT_MAPPING)
    public String newAccount(final Model model,
                             @ModelAttribute(ACCOUNT_FORM) final AccountViewModelInput accountViewModelInput,
                             final BindingResult bindingResult) {
        final User user = getLoggedUser(model);
        accountViewModelInputValidator.validate(accountViewModelInput, bindingResult, user);

        if (bindingResult.hasErrors()) {
            return NEW_ACCOUNT_TEMPLATE;
        }

        user.addAccount(accountViewModelInput.toAccount());
        userRepository.save(user);

        return REDIRECT_TO_HOME;
    }
}
