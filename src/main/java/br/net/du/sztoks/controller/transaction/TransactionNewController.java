package br.net.du.sztoks.controller.transaction;

import static br.net.du.sztoks.controller.util.ControllerConstants.CURRENCIES;
import static br.net.du.sztoks.controller.util.ControllerConstants.ID;
import static br.net.du.sztoks.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.sztoks.controller.util.ControllerConstants.SELECTED_CURRENCY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_TITLE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.TRANSACTION_TYPE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.sztoks.controller.util.TransactionUtils.hasTithingImpact;
import static br.net.du.sztoks.controller.viewmodel.SnapshotViewModelOutput.getDisplayTitle;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.util.SnapshotValidations;
import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelInput;
import br.net.du.sztoks.controller.viewmodel.user.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.TransactionViewModelInputValidator;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.transaction.Transaction;
import br.net.du.sztoks.model.transaction.TransactionType;
import br.net.du.sztoks.service.AccountService;
import br.net.du.sztoks.service.SnapshotService;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@WebController
public class TransactionNewController {
    public static final String TRANSACTION_FORM = "transactionForm";
    private static final String NEW_TRANSACTION_TEMPLATE = "transaction/new_%s_transaction";
    private static final String NEWTRANSACTION_MAPPING = "/snapshot/{id}/newTransaction";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotValidations snapshotValidations;

    @Autowired private TransactionViewModelInputValidator transactionViewModelInputValidator;

    @Autowired private AccountService accountService;

    @GetMapping("/snapshot/{id}/newIncomeTransaction")
    public String newIncomeTransaction(
            @PathVariable(value = ID) final Long snapshotId,
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        return prepareGetMapping(
                userAgent,
                model,
                snapshotId,
                TransactionType.INCOME,
                new TransactionViewModelInput());
    }

    @GetMapping("/snapshot/{id}/newInvestmentTransaction")
    public String newInvestmentTransaction(
            @PathVariable(value = ID) final Long snapshotId,
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        return prepareGetMapping(
                userAgent,
                model,
                snapshotId,
                TransactionType.INVESTMENT,
                new TransactionViewModelInput());
    }

    @GetMapping("/snapshot/{id}/newDonationTransaction")
    public String newDonationTransaction(
            @PathVariable(value = ID) final Long snapshotId,
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        return prepareGetMapping(
                userAgent,
                model,
                snapshotId,
                TransactionType.DONATION,
                new TransactionViewModelInput());
    }

    @PostMapping(NEWTRANSACTION_MAPPING)
    @Transactional
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @ModelAttribute(TRANSACTION_FORM)
                    final TransactionViewModelInput transactionViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot =
                snapshotValidations.validateLockAndRefreshSnapshot(model, snapshotId);

        transactionViewModelInputValidator.validate(
                transactionViewModelInput, bindingResult, snapshot);

        final TransactionType transactionType =
                TransactionType.valueOf(transactionViewModelInput.getTypeName());
        if (bindingResult.hasErrors()) {
            return prepareGetMapping(
                    userAgent, model, snapshotId, transactionType, transactionViewModelInput);
        }

        final Transaction transaction = transactionViewModelInput.toTransaction();

        final Optional<Account> tithingAccountOpt =
                hasTithingImpact(transaction)
                        ? Optional.of(snapshot.getTithingAccount(transaction.getCurrencyUnit()))
                        : Optional.empty();

        snapshot.addTransaction(transaction);

        if (tithingAccountOpt.isPresent()) {
            accountService.save(tithingAccountOpt.get());
        }

        snapshotService.save(snapshot);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshot.getId());
    }

    private String prepareGetMapping(
            final String userAgent,
            final Model model,
            final Long snapshotId,
            final TransactionType transactionType,
            final TransactionViewModelInput transactionViewModelInput) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot = snapshotValidations.validateSnapshot(model, snapshotId);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshot.getId());
        model.addAttribute(SNAPSHOT_TITLE_KEY, getDisplayTitle(snapshot));
        model.addAttribute(TRANSACTION_TYPE_KEY, transactionType);

        model.addAttribute(CURRENCIES, snapshot.getCurrenciesInUse());

        model.addAttribute(
                SELECTED_CURRENCY,
                StringUtils.isEmpty(transactionViewModelInput.getCurrencyUnit())
                        ? snapshot.getBaseCurrencyUnit().toString()
                        : transactionViewModelInput.getCurrencyUnit());

        if (StringUtils.isEmpty(transactionViewModelInput.getTithingPercentage())) {
            transactionViewModelInput.setTithingPercentage(
                    snapshot.getDefaultTithingPercentage().toPlainString());
        }

        model.addAttribute(TRANSACTION_FORM, transactionViewModelInput);

        return prepareTemplate(
                userAgent,
                model,
                String.format(NEW_TRANSACTION_TEMPLATE, transactionType.toString().toLowerCase()));
    }
}
