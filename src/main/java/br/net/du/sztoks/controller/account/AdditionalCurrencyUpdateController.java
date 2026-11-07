package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.AdditionalCurrencyAccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.AdditionalCurrencyAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdditionalCurrencyUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateAdditionalCurrencyShares")
    public Object updateShares(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, Object> fn =
                (jsonRequest, account) -> {
                    ((AdditionalCurrencyAccount) account)
                            .setShares(new BigDecimal(jsonRequest.getNewValue()));
                    return AdditionalCurrencyAccountViewModelOutput.of(account, true);
                };
        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, AdditionalCurrencyAccount.class, fn, true);
    }

    @PostMapping("/snapshot/updateAdditionalCurrencyAmountInvested")
    public Object updateAmountInvested(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, Object> fn =
                (jsonRequest, account) -> {
                    ((AdditionalCurrencyAccount) account)
                            .setAmountInvested(new BigDecimal(jsonRequest.getNewValue()));
                    return AdditionalCurrencyAccountViewModelOutput.of(account, true);
                };
        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, AdditionalCurrencyAccount.class, fn, true);
    }
}
