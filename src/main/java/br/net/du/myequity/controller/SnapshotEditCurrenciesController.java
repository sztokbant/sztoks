package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.SnapshotNewCurrencyController.SNAPSHOT_NEW_CURRENCY_MAPPING;
import static br.net.du.myequity.controller.util.ControllerConstants.ID;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_BASE_CURRENCY_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.interceptor.WebController;
import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.EditCurrenciesViewModelInput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.controller.viewmodel.validator.EditCurrenciesViewModelInputValidator;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@WebController
public class SnapshotEditCurrenciesController {
    private static final String SNAPSHOT_EDIT_CURRENCIES_MAPPING = "/snapshot/{id}/currencies";

    private static final String SNAPSHOT_EDIT_CURRENCIES_TEMPLATE = "snapshot_edit_currencies";
    private static final String EDIT_CURRENCIES_FORM = "editCurrenciesForm";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Autowired private EditCurrenciesViewModelInputValidator validator;

    @GetMapping(SNAPSHOT_EDIT_CURRENCIES_MAPPING)
    public String get(@PathVariable(value = ID) final Long snapshotId, final Model model) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        if (snapshot.getCurrencyConversionRates().isEmpty()) {
            return "redirect:" + SNAPSHOT_NEW_CURRENCY_MAPPING;
        }

        final EditCurrenciesViewModelInput currenciesViewModelInput =
                new EditCurrenciesViewModelInput(snapshot.getCurrencyConversionRates());
        return prepareGetMapping(snapshot, model, currenciesViewModelInput);
    }

    @PostMapping(SNAPSHOT_EDIT_CURRENCIES_MAPPING)
    @Transactional
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            @ModelAttribute(EDIT_CURRENCIES_FORM)
                    final EditCurrenciesViewModelInput currenciesViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        validator.validate(currenciesViewModelInput, bindingResult, snapshot);

        if (bindingResult.hasErrors()) {
            return prepareGetMapping(snapshot, model, currenciesViewModelInput);
        }

        currenciesViewModelInput.getCurrencyConversionRates().entrySet().stream()
                .forEach(
                        entry ->
                                snapshot.putCurrencyConversionRate(
                                        CurrencyUnit.of(entry.getKey().trim()),
                                        new BigDecimal(entry.getValue().trim())));
        snapshotService.save(snapshot);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshot.getId());
    }

    private String prepareGetMapping(
            final Snapshot snapshot,
            final Model model,
            final EditCurrenciesViewModelInput currenciesViewModelInput) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshot.getId());
        model.addAttribute(SNAPSHOT_BASE_CURRENCY_KEY, snapshot.getBaseCurrencyUnit().getCode());

        model.addAttribute(EDIT_CURRENCIES_FORM, currenciesViewModelInput);

        return SNAPSHOT_EDIT_CURRENCIES_TEMPLATE;
    }
}
