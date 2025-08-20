package br.net.du.sztoks.controller.currency;

import static br.net.du.sztoks.controller.currency.SnapshotNewCurrencyController.SNAPSHOT_NEW_CURRENCY_MAPPING;
import static br.net.du.sztoks.controller.util.ControllerConstants.ID;
import static br.net.du.sztoks.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_BASE_CURRENCY_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_TITLE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.sztoks.controller.viewmodel.SnapshotViewModelOutput.getDisplayTitle;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.util.SnapshotValidations;
import br.net.du.sztoks.controller.viewmodel.EditCurrenciesViewModelInput;
import br.net.du.sztoks.controller.viewmodel.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.EditCurrenciesViewModelInputValidator;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
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
import org.springframework.web.bind.annotation.RequestHeader;

@WebController
public class SnapshotEditCurrenciesController {
    private static final String SNAPSHOT_EDIT_CURRENCIES_MAPPING = "/snapshot/{id}/currencies";

    private static final String SNAPSHOT_EDIT_CURRENCIES_TEMPLATE =
            "currency/snapshot_edit_currencies";
    private static final String EDIT_CURRENCIES_FORM = "editCurrenciesForm";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotValidations snapshotValidations;

    @Autowired private EditCurrenciesViewModelInputValidator validator;

    @GetMapping(SNAPSHOT_EDIT_CURRENCIES_MAPPING)
    public String get(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @PathVariable(value = ID) final Long snapshotId) {
        final Snapshot snapshot = snapshotValidations.validateSnapshot(model, snapshotId);

        if (snapshot.getCurrencyConversionRates().isEmpty()) {
            return "redirect:" + SNAPSHOT_NEW_CURRENCY_MAPPING;
        }

        final EditCurrenciesViewModelInput currenciesViewModelInput =
                new EditCurrenciesViewModelInput(snapshot.getCurrencyConversionRates());
        return prepareGetMapping(userAgent, model, snapshot, currenciesViewModelInput);
    }

    @PostMapping(SNAPSHOT_EDIT_CURRENCIES_MAPPING)
    @Transactional
    public String post(
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @PathVariable(value = ID) final Long snapshotId,
            @ModelAttribute(EDIT_CURRENCIES_FORM)
                    final EditCurrenciesViewModelInput currenciesViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotValidations.validateSnapshot(model, snapshotId);

        validator.validate(currenciesViewModelInput, bindingResult, snapshot);

        if (bindingResult.hasErrors()) {
            return prepareGetMapping(userAgent, model, snapshot, currenciesViewModelInput);
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
            final String userAgent,
            final Model model,
            final Snapshot snapshot,
            final EditCurrenciesViewModelInput currenciesViewModelInput) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshot.getId());
        model.addAttribute(SNAPSHOT_TITLE_KEY, getDisplayTitle(snapshot));
        model.addAttribute(SNAPSHOT_BASE_CURRENCY_KEY, snapshot.getBaseCurrencyUnit().getCode());

        model.addAttribute(EDIT_CURRENCIES_FORM, currenciesViewModelInput);

        return prepareTemplate(userAgent, model, SNAPSHOT_EDIT_CURRENCIES_TEMPLATE);
    }
}
