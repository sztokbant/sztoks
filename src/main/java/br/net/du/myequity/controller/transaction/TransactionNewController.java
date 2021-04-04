package br.net.du.myequity.controller.transaction;

import static br.net.du.myequity.controller.util.ControllerConstants.ID;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.TRANSACTION_TYPE_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelInput;
import br.net.du.myequity.controller.viewmodel.validator.TransactionViewModelInputValidator;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.transaction.TransactionType;
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

@Controller
@WebController
public class TransactionNewController {
    public static final String TRANSACTION_FORM = "transactionForm";
    private static final String NEW_TRANSACTION_TEMPLATE = "transaction/new_%s_transaction";
    private static final String NEWTRANSACTION_MAPPING = "/snapshot/{id}/newTransaction";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Autowired private TransactionViewModelInputValidator transactionViewModelInputValidator;

    @GetMapping("/snapshot/{id}/newIncomeTransaction")
    public String newIncomeTransaction(
            @PathVariable(value = ID) final Long snapshotId, final Model model) {
        return prepareGetMapping(
                snapshotId, model, TransactionType.INCOME, new TransactionViewModelInput());
    }

    @GetMapping("/snapshot/{id}/newInvestmentTransaction")
    public String newInvestmentTransaction(
            @PathVariable(value = ID) final Long snapshotId, final Model model) {
        return prepareGetMapping(
                snapshotId, model, TransactionType.INVESTMENT, new TransactionViewModelInput());
    }

    @GetMapping("/snapshot/{id}/newDonationTransaction")
    public String newDonationTransaction(
            @PathVariable(value = ID) final Long snapshotId, final Model model) {
        return prepareGetMapping(
                snapshotId, model, TransactionType.DONATION, new TransactionViewModelInput());
    }

    @PostMapping(NEWTRANSACTION_MAPPING)
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            @ModelAttribute(TRANSACTION_FORM)
                    final TransactionViewModelInput transactionViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        transactionViewModelInputValidator.validate(
                transactionViewModelInput, bindingResult, snapshot);

        final TransactionType transactionType =
                TransactionType.valueOf(transactionViewModelInput.getTypeName());
        if (bindingResult.hasErrors()) {
            return prepareGetMapping(snapshotId, model, transactionType, transactionViewModelInput);
        }

        snapshot.addTransaction(transactionViewModelInput.toTransaction());
        snapshotService.save(snapshot);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshot.getId());
    }

    private String prepareGetMapping(
            final Long snapshotId,
            final Model model,
            final TransactionType transactionType,
            final TransactionViewModelInput transactionViewModelInput) {
        // Ensure snapshot belongs to logged user
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshotId);
        model.addAttribute(TRANSACTION_TYPE_KEY, transactionType);

        if (StringUtils.isEmpty(transactionViewModelInput.getCurrencyUnit())) {
            transactionViewModelInput.setCurrencyUnit(snapshot.getBaseCurrencyUnit().toString());
        }

        if (StringUtils.isEmpty(transactionViewModelInput.getTithingPercentage())) {
            transactionViewModelInput.setTithingPercentage(
                    snapshot.getDefaultTithingPercentage().toPlainString());
        }

        model.addAttribute(TRANSACTION_FORM, transactionViewModelInput);

        return getTemplateFor(transactionType);
    }

    private String getTemplateFor(final TransactionType transactionType) {
        return String.format(NEW_TRANSACTION_TEMPLATE, transactionType.toString().toLowerCase());
    }
}
