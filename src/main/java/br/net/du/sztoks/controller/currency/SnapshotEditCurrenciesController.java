package br.net.du.sztoks.controller.currency;

import static br.net.du.sztoks.controller.currency.SnapshotNewCurrencyController.SNAPSHOT_NEW_CURRENCY_MAPPING;
import static br.net.du.sztoks.controller.util.ControllerConstants.ID;
import static br.net.du.sztoks.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_BASE_CURRENCY_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_TITLE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.sztoks.controller.viewmodel.SnapshotViewModelOutput.getDisplayTitle;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.util.SnapshotUtils;
import br.net.du.sztoks.controller.viewmodel.EditCurrenciesViewModelInput;
import br.net.du.sztoks.controller.viewmodel.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.EditCurrenciesViewModelInputValidator;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
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
public class SnapshotEditCurrenciesController {
    private static final String SNAPSHOT_EDIT_CURRENCIES_MAPPING = "/snapshot/{id}/currencies";

    private static final String SNAPSHOT_EDIT_CURRENCIES_TEMPLATE =
            "currency/snapshot_edit_currencies";
    private static final String EDIT_CURRENCIES_FORM = "editCurrenciesForm";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Autowired private EditCurrenciesViewModelInputValidator validator;

    @GetMapping(SNAPSHOT_EDIT_CURRENCIES_MAPPING)
    public String get(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            final Device device) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        if (snapshot.getCurrencyConversionRates().isEmpty()) {
            return "redirect:" + SNAPSHOT_NEW_CURRENCY_MAPPING;
        }

        final EditCurrenciesViewModelInput currenciesViewModelInput =
                new EditCurrenciesViewModelInput(snapshot.getCurrencyConversionRates());
        return prepareGetMapping(snapshot, model, device, currenciesViewModelInput);
    }

    @PostMapping(SNAPSHOT_EDIT_CURRENCIES_MAPPING)
    @Transactional
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            final Device device,
            @ModelAttribute(EDIT_CURRENCIES_FORM)
                    final EditCurrenciesViewModelInput currenciesViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        validator.validate(currenciesViewModelInput, bindingResult, snapshot);

        if (bindingResult.hasErrors()) {
            return prepareGetMapping(snapshot, model, device, currenciesViewModelInput);
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
            final Device device,
            final EditCurrenciesViewModelInput currenciesViewModelInput) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshot.getId());
        model.addAttribute(SNAPSHOT_TITLE_KEY, getDisplayTitle(snapshot));
        model.addAttribute(SNAPSHOT_BASE_CURRENCY_KEY, snapshot.getBaseCurrencyUnit().getCode());

        model.addAttribute(EDIT_CURRENCIES_FORM, currenciesViewModelInput);

        return prepareTemplate(model, device, SNAPSHOT_EDIT_CURRENCIES_TEMPLATE);
    }
}
