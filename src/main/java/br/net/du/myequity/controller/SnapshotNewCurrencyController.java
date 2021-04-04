package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerConstants.ID;
import static br.net.du.myequity.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.myequity.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.myequity.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.myequity.controller.util.ControllerUtils.getLoggedUser;

import br.net.du.myequity.controller.util.SnapshotUtils;
import br.net.du.myequity.controller.viewmodel.NewCurrencyViewModelInput;
import br.net.du.myequity.controller.viewmodel.UserViewModelOutput;
import br.net.du.myequity.controller.viewmodel.validator.NewCurrencyViewModelInputValidator;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.service.SnapshotService;
import java.math.BigDecimal;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SnapshotNewCurrencyController {
    private static final String SNAPSHOT_NEW_CURRENCY_MAPPING = "/snapshot/{id}/newCurrency";

    private static final String SNAPSHOT_NEW_CURRENCY_TEMPLATE = "snapshot_new_currency";
    private static final String NEW_CURRENCY_FORM = "newCurrencyForm";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Autowired private NewCurrencyViewModelInputValidator validator;

    @GetMapping(SNAPSHOT_NEW_CURRENCY_MAPPING)
    public String get(@PathVariable(value = ID) final Long snapshotId, final Model model) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);
        return prepareGetMapping(snapshot, model, new NewCurrencyViewModelInput());
    }

    @PostMapping(SNAPSHOT_NEW_CURRENCY_MAPPING)
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            final Model model,
            @ModelAttribute(NEW_CURRENCY_FORM)
                    final NewCurrencyViewModelInput newCurrencyViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        validator.validate(newCurrencyViewModelInput, bindingResult, snapshot);

        if (bindingResult.hasErrors()) {
            return prepareGetMapping(snapshot, model, newCurrencyViewModelInput);
        }

        snapshot.putCurrencyConversionRate(
                CurrencyUnit.of(newCurrencyViewModelInput.getCurrencyUnit().trim()),
                new BigDecimal(newCurrencyViewModelInput.getConversionRate().trim()));
        snapshotService.save(snapshot);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshot.getId());
    }

    private String prepareGetMapping(
            final Snapshot snapshot,
            final Model model,
            final NewCurrencyViewModelInput newCurrencyViewModelInput) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshot.getId());

        model.addAttribute(NEW_CURRENCY_FORM, newCurrencyViewModelInput);

        return SNAPSHOT_NEW_CURRENCY_TEMPLATE;
    }
}
