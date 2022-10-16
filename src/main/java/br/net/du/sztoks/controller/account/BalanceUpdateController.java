package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.BalanceUpdatable;
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
                    ((BalanceUpdatable) account)
                            .setBalance(new BigDecimal(jsonRequest.getNewValue()));

                    return AccountViewModelOutput.of(account, true);
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, BalanceUpdatable.class, updateAmountFunction, true);
    }
}
