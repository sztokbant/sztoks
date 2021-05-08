package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.BalanceUpdateable;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateAccountBalance")
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                    ((BalanceUpdateable) account).setBalance(newValue);

                    return AccountViewModelOutput.of(account, true);
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, BalanceUpdateable.class, updateAmountFunction, true);
    }
}
