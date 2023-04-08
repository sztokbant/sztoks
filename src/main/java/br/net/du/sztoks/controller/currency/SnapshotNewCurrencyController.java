package br.net.du.sztoks.controller.currency;

import static br.net.du.sztoks.controller.util.ControllerConstants.CURRENCIES;
import static br.net.du.sztoks.controller.util.ControllerConstants.ID;
import static br.net.du.sztoks.controller.util.ControllerConstants.REDIRECT_SNAPSHOT_TEMPLATE;
import static br.net.du.sztoks.controller.util.ControllerConstants.SELECTED_CURRENCY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_BASE_CURRENCY_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_ID_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.SNAPSHOT_TITLE_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_AGENT_REQUEST_HEADER_KEY;
import static br.net.du.sztoks.controller.util.ControllerConstants.USER_KEY;
import static br.net.du.sztoks.controller.util.ControllerUtils.getLoggedUser;
import static br.net.du.sztoks.controller.util.ControllerUtils.prepareTemplate;
import static br.net.du.sztoks.controller.viewmodel.SnapshotViewModelOutput.getDisplayTitle;
import static java.util.stream.Collectors.toList;

import br.net.du.sztoks.controller.interceptor.WebController;
import br.net.du.sztoks.controller.util.SnapshotUtils;
import br.net.du.sztoks.controller.viewmodel.NewCurrencyViewModelInput;
import br.net.du.sztoks.controller.viewmodel.UserViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.validator.NewCurrencyViewModelInputValidator;
import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.User;
import br.net.du.sztoks.service.SnapshotService;
import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
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
public class SnapshotNewCurrencyController {
    public static final String SNAPSHOT_NEW_CURRENCY_MAPPING = "/snapshot/{id}/newCurrency";

    private static final String SNAPSHOT_NEW_CURRENCY_TEMPLATE = "currency/snapshot_new_currency";
    private static final String NEW_CURRENCY_FORM = "newCurrencyForm";

    @Autowired private SnapshotService snapshotService;

    @Autowired private SnapshotUtils snapshotUtils;

    @Autowired private NewCurrencyViewModelInputValidator validator;

    @GetMapping(SNAPSHOT_NEW_CURRENCY_MAPPING)
    public String get(
            @PathVariable(value = ID) final Long snapshotId,
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);
        return prepareGetMapping(userAgent, model, snapshot, new NewCurrencyViewModelInput());
    }

    @PostMapping(SNAPSHOT_NEW_CURRENCY_MAPPING)
    @Transactional
    public String post(
            @PathVariable(value = ID) final Long snapshotId,
            @RequestHeader(value = USER_AGENT_REQUEST_HEADER_KEY, required = false)
                    final String userAgent,
            final Model model,
            @ModelAttribute(NEW_CURRENCY_FORM)
                    final NewCurrencyViewModelInput newCurrencyViewModelInput,
            final BindingResult bindingResult) {
        final Snapshot snapshot = snapshotUtils.validateSnapshot(model, snapshotId);

        validator.validate(newCurrencyViewModelInput, bindingResult, snapshot);

        if (bindingResult.hasErrors()) {
            return prepareGetMapping(userAgent, model, snapshot, newCurrencyViewModelInput);
        }

        snapshot.putCurrencyConversionRate(
                CurrencyUnit.of(newCurrencyViewModelInput.getCurrencyUnit().trim()),
                new BigDecimal(newCurrencyViewModelInput.getConversionRate().trim()));
        snapshotService.save(snapshot);

        return String.format(REDIRECT_SNAPSHOT_TEMPLATE, snapshot.getId());
    }

    private String prepareGetMapping(
            final String userAgent,
            final Model model,
            final Snapshot snapshot,
            final NewCurrencyViewModelInput newCurrencyViewModelInput) {
        final User user = getLoggedUser(model);
        model.addAttribute(USER_KEY, UserViewModelOutput.of(user));
        model.addAttribute(SNAPSHOT_ID_KEY, snapshot.getId());
        model.addAttribute(SNAPSHOT_TITLE_KEY, getDisplayTitle(snapshot));
        model.addAttribute(SNAPSHOT_BASE_CURRENCY_KEY, snapshot.getBaseCurrencyUnit().getCode());

        final SortedSet<String> currenciesInUse = snapshot.getCurrenciesInUse();
        final List<String> availableCurrencies =
                CurrencyUnit.registeredCurrencies().stream()
                        .map(currencyUnit -> currencyUnit.getCode())
                        .filter(currency -> !currenciesInUse.contains(currency))
                        .sorted()
                        .collect(toList());

        model.addAttribute(CURRENCIES, availableCurrencies);

        // There may be a selected currency after a post error
        if (newCurrencyViewModelInput.getCurrencyUnit() != null) {
            final CurrencyUnit selectedCurrency =
                    CurrencyUnit.of(newCurrencyViewModelInput.getCurrencyUnit());
            model.addAttribute(SELECTED_CURRENCY, selectedCurrency.getCode());
        }

        model.addAttribute(NEW_CURRENCY_FORM, newCurrencyViewModelInput);

        return prepareTemplate(userAgent, model, SNAPSHOT_NEW_CURRENCY_TEMPLATE);
    }
}
