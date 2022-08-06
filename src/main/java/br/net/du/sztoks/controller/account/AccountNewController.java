package br.net.du.sztoks.controller.account;

import static br.net.du.sztoks.controller.util.ControllerConstants.ACCOUNT_TYPE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.CURRENCIES;
import static br.net.du.sztoks.controller.util.ControllerConstants.ID;
import static br.net.du.sztoks.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.sztoks.controller.util.ControllerConstants.SELECTED_CURRENCY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_TITLE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.sztoks.controller.viewmodel.SnapshotViewModelOutput.getDisplayTitle;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.util.SnapshotUtils;
import br.net.du.sztoks.controller.viewmodel.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.AccountViewModelInput;
import br.net.du.sztoks.controller.viewmodel.validator.AccountViewModelInputValidator;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AccountType;
import br.net.du.sztoks.model.account.DueDateUpdatable;
import br.net.du.sztoks.service.SnapshotService;
import java.time.YearMonth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            final Device device) {
        return prepareGetMapping(
                snapshotId, model, device, AccountType.ASSET, new AccountViewModelInput());
    }

    @GetMapping("/snapshot/{id}/newLiabilityAccount")
    public String newLiabilityAccount(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            final Device device) {
        return prepareGetMapping(
                snapshotId, model, device, AccountType.LIABILITY, new AccountViewModelInput());
    }

    @PostMapping(NEWACCOUNT_MAPPING)
    @Transactional
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            final Device device,
            @ModelAttribute(ACCOUNT_FORM) final AccountViewModelInput accountViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        accountViewModelInputValidator.validate(accountViewModelInput, bindingResult, snapshot);

        final AccountType accountType = AccountType.valueOf(accountViewModelInput.getAccountType());
        if (bindingResult.hasErrors()) {
            return prepareGetMapping(snapshotId, model, device, accountType, accountViewModelInput);
        }

        final Account account = accountViewModelInput.toAccount();

        if (account instanceof DueDateUpdatable) {
            ((DueDateUpdatable) account)
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
            final Device device,
            final AccountType accountType,
            final AccountViewModelInput accountViewModelInput) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshotId);
        model.addAttribute(SNAPSHOT_TITLE_KEY, getDisplayTitle(snapshot));
        model.addAttribute(ACCOUNT_TYPE_KEY, accountType);

        model.addAttribute(CURRENCIES, snapshot.getCurrenciesInUse());

        model.addAttribute(
                SELECTED_CURRENCY,
                StringUtils.isEmpty(accountViewModelInput.getCurrencyUnit())
                        ? snapshot.getBaseCurrencyUnit().toString()
                        : accountViewModelInput.getCurrencyUnit());

        model.addAttribute(ACCOUNT_FORM, accountViewModelInput);

        return prepareTemplate(
                model,
                device,
                String.format(NEW_ACCOUNT_TEMPLATE, accountType.toString().toLowerCase()));
    }
}
