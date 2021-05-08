package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.BalanceUpdateable;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRenameController {

    @Autowired private AccountUpdater accountUpdater;

    @PostMapping("/snapshot/renameAccount")
    @Transactional
    public AccountViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAmountFunction =
                        (jsonRequest, account) -> {
                            if (StringUtils.isEmpty(jsonRequest.getNewValue())) {
                                throw new IllegalArgumentException("Account name can't be empty");
                            }

                            account.setName(jsonRequest.getNewValue().trim());

                            return AccountViewModelOutput.of(account, false);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                BalanceUpdateable.class,
                updateAmountFunction,
                false);
    }
}
