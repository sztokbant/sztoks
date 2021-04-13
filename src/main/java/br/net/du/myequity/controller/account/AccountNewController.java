package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelInput;
import br.net.du.myequity.controller.viewmodel.validator.AccountViewModelInputValidator;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.account.DueDateUpdateable;
import br.net.du.myequity.service.SnapshotService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.YearMonth;

import static br.net.du.myequity.controller.util.ControllerConstants.ACCOUNT_TYPE_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.CURRENCIES;
import static br.net.du.myequity.controller.util.ControllerConstants.ID;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerConstants.SELECTED_CURRENCY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

@Controller
@WebController
public class AccountNewController {
    public static final String ACCOUNT_FORM = "accountForm";
    private static final String NEW_ACCOUNT_TEMPLATE = "account/new_%s_account";
    private static final String NEWACCOUNT_MAPPING = "/snapshot/{id}/newAccount";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Autowired private AccountViewModelInputValidator accountViewModelInputValidator;

    @GetMapping("/snapshot/{id}/newAssetAccount")
    public String newAssetAccount(
            @PathVariable(value = ID) final Long snapshotId, final Model model) {
        return prepareGetMapping(snapshotId, model, AccountType.ASSET, new AccountViewModelInput());
    }

    @GetMapping("/snapshot/{id}/newLiabilityAccount")
    public String newLiabilityAccount(
            @PathVariable(value = ID) final Long snapshotId, final Model model) {
        return prepareGetMapping(
                snapshotId, model, AccountType.LIABILITY, new AccountViewModelInput());
    }

    @PostMapping(NEWACCOUNT_MAPPING)
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            @ModelAttribute(ACCOUNT_FORM) final AccountViewModelInput accountViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        accountViewModelInputValidator.validate(accountViewModelInput, bindingResult, snapshot);

        final AccountType accountType = AccountType.valueOf(accountViewModelInput.getAccountType());
        if (bindingResult.hasErrors()) {
            return prepareGetMapping(snapshotId, model, accountType, accountViewModelInput);
        }

        final Account account = accountViewModelInput.toAccount();

        if (account instanceof DueDateUpdateable) {
            ((DueDateUpdateable) account)
                    .setDueDate(
                            YearMonth.of(snapshot.getYear(), snapshot.getMonth()).atEndOfMonth());
        }

        snapshot.addAccount(account);
        snapshotService.save(snapshot);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshot.getId());
    }

    private String prepareGetMapping(
            final Long snapshotId,
            final Model model,
            final AccountType accountType,
            final AccountViewModelInput accountViewModelInput) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshotId);
        model.addAttribute(ACCOUNT_TYPE_KEY, accountType);

        model.addAttribute(CURRENCIES, snapshot.getCurrenciesInUse());

        if (StringUtils.isEmpty(accountViewModelInput.getCurrencyUnit())) {
            model.addAttribute(SELECTED_CURRENCY, snapshot.getBaseCurrencyUnit().toString());
        }

        model.addAttribute(ACCOUNT_FORM, accountViewModelInput);

        return getTemplateFor(accountType);
    }

    private String getTemplateFor(final AccountType accountType) {
        return String.format(NEW_ACCOUNT_TEMPLATE, accountType.toString().toLowerCase());
    }
}
