package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.EntityRenameJsonResponse;
import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.model.account.Account;
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
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAmountFunction =
                (jsonRequest, account) -> {
                    if (StringUtils.isEmpty(jsonRequest.getNewValue())) {
                        throw new IllegalArgumentException("Account name can't be empty");
                    }

                    account.setName(jsonRequest.getNewValue().trim());

                    return EntityRenameJsonResponse.builder().name(account.getName()).build();
                };

        return accountUpdater.updateField(
                model, valueUpdateJsonRequest, Account.class, updateAmountFunction, false);
    }
}
