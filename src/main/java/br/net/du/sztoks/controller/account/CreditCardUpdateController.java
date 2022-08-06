package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.CreditCardAccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.CreditCardAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditCardUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateCreditCardTotalCredit")
    public Object updateCreditCardTotalCredit(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object>
                updateCreditCardTotalCreditFunction =
                        (jsonRequest, account) -> {
                            final CreditCardAccount creditCardAccount = (CreditCardAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            creditCardAccount.setTotalCredit(newValue);

                            return CreditCardAccountViewModelOutput.of(creditCardAccount, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                CreditCardAccount.class,
                updateCreditCardTotalCreditFunction,
                true);
    }

    @PostMapping("/snapshot/updateCreditCardAvailableCredit")
    public Object updateCreditCardAvailableCredit(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, Object>
                updateCreditCardAvailableCreditFunction =
                        (jsonRequest, account) -> {
                            final CreditCardAccount creditCardAccount = (CreditCardAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            creditCardAccount.setAvailableCredit(newValue);

                            return CreditCardAccountViewModelOutput.of(creditCardAccount, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                CreditCardAccount.class,
                updateCreditCardAvailableCreditFunction,
                true);
    }

    @PostMapping("/snapshot/updateCreditCardStatement")
    public Object updateCreditCardStatement(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, Object>
                updateCreditCardStatementFunction =
                        (jsonRequest, account) -> {
                            final CreditCardAccount creditCardAccount = (CreditCardAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            creditCardAccount.setStatement(newValue);

                            return CreditCardAccountViewModelOutput.of(creditCardAccount, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                CreditCardAccount.class,
                updateCreditCardStatementFunction,
                false);
    }
}
