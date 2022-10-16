package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.PayableAccountViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.ReceivableAccountViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.SharedBillAccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.BillAccount;
import br.net.du.sztoks.model.account.PayableAccount;
import br.net.du.sztoks.model.account.ReceivableAccount;
import br.net.du.sztoks.model.account.SharedBillAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateAccountBillAmount")
    public Object updateBillAmount(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                    ((BillAccount) account).setBillAmount(newValue);

                    if (account instanceof SharedBillAccount) {
                        return SharedBillAccountViewModelOutput.of(account, true);
                    } else if (account instanceof ReceivableAccount) {
                        return ReceivableAccountViewModelOutput.of(account, true);
                    } else if (account instanceof PayableAccount) {
                        return PayableAccountViewModelOutput.of(account, true);
                    } else {
                        throw new IllegalArgumentException("Unrecognized account sub_type");
                    }
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, BillAccount.class, updateAmountFunction, true);
    }

    @PostMapping("/snapshot/updateBillIsPaid")
    public Object updateBillIsPaid(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final boolean newValue = new Boolean(jsonRequest.getNewValue());

                    ((BillAccount) account).setIsPaid(newValue);

                    if (account instanceof SharedBillAccount) {
                        return SharedBillAccountViewModelOutput.of(account, true);
                    } else if (account instanceof ReceivableAccount) {
                        return ReceivableAccountViewModelOutput.of(account, true);
                    } else if (account instanceof PayableAccount) {
                        return PayableAccountViewModelOutput.of(account, true);
                    } else {
                        throw new IllegalArgumentException("Unrecognized account sub_type");
                    }
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, BillAccount.class, updateAmountFunction, true);
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
                    ((SharedBillAccount) account)
                            .setDueDay(Integer.parseInt(jsonRequest.getNewValue()));

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
