package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.SharedBillReceivableAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SharedBillReceivableAccount;
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
    public AccountViewModelOutput updateBillAmount(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, account) -> {
                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            ((SharedBillReceivableAccount) account).setBillAmount(newValue);

                            return SharedBillReceivableAccountViewModelOutput.of(account, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharedBillReceivableAccount.class,
                updateAmountFunction,
                true);
    }

    @PostMapping("/snapshot/updateAccountPaymentReceived")
    public AccountViewModelOutput updateAccountPaymentReceived(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, account) -> {
                            final boolean newValue = new Boolean(jsonRequest.getNewValue());

                            ((SharedBillReceivableAccount) account).setPaymentReceived(newValue);

                            return SharedBillReceivableAccountViewModelOutput.of(account, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharedBillReceivableAccount.class,
                updateAmountFunction,
                true);
    }

    @PostMapping("/snapshot/updateAccountNumberOfPartners")
    public AccountViewModelOutput updateNumberOfPartners(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, account) -> {
                            final Integer newValue = Integer.parseInt(jsonRequest.getNewValue());
                            ((SharedBillReceivableAccount) account).setNumberOfPartners(newValue);

                            return SharedBillReceivableAccountViewModelOutput.of(account, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharedBillReceivableAccount.class,
                updateAmountFunction,
                true);
    }

    @PostMapping("/snapshot/updateAccountDueDay")
    public AccountViewModelOutput updateAccountDueDay(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, account) -> {
                            final Integer newValue = Integer.parseInt(jsonRequest.getNewValue());
                            ((SharedBillReceivableAccount) account).setDueDay(newValue);

                            return SharedBillReceivableAccountViewModelOutput.of(account, false);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharedBillReceivableAccount.class,
                updateAmountFunction,
                false);
    }
}
