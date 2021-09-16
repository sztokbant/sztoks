package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.SharedBillAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SharedBillAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SharedBillUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateAccountBillAmount")
    public Object updateBillAmount(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                    ((SharedBillAccount) account).setBillAmount(newValue);

                    return SharedBillAccountViewModelOutput.of(account, true);
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, SharedBillAccount.class, updateAmountFunction, true);
    }

    @PostMapping("/snapshot/updateBillIsPaid")
    public Object updateBillIsPaid(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final boolean newValue = new Boolean(jsonRequest.getNewValue());

                    ((SharedBillAccount) account).setIsPaid(newValue);

                    return SharedBillAccountViewModelOutput.of(account, true);
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, SharedBillAccount.class, updateAmountFunction, true);
    }

    @PostMapping("/snapshot/updateAccountNumberOfPartners")
    public Object updateNumberOfPartners(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final Integer newValue = Integer.parseInt(jsonRequest.getNewValue());
                    ((SharedBillAccount) account).setNumberOfPartners(newValue);

                    return SharedBillAccountViewModelOutput.of(account, true);
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, SharedBillAccount.class, updateAmountFunction, true);
    }

    @PostMapping("/snapshot/updateAccountDueDay")
    public Object updateAccountDueDay(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final Integer newValue = Integer.parseInt(jsonRequest.getNewValue());
                    ((SharedBillAccount) account).setDueDay(newValue);

                    return SharedBillAccountViewModelOutput.of(account, false);
                };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharedBillAccount.class,
                updateAmountFunction,
                false);
    }
}
