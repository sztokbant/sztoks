package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.PayableAccountViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.ReceivableAccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.DueDateUpdatable;
import br.net.du.sztoks.model.account.PayableAccount;
import br.net.du.sztoks.model.account.ReceivableAccount;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DueDateUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateAccountDueDate")
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateDueDateFunction =
                (jsonRequest, account) -> {
                    ((DueDateUpdatable) account)
                            .setDueDate(LocalDate.parse(jsonRequest.getNewValue()));

                    if (account instanceof PayableAccount) {
                        return PayableAccountViewModelOutput.of(account, false);
                    } else if (account instanceof ReceivableAccount) {
                        return ReceivableAccountViewModelOutput.of(account, false);
                    } else {
                        throw new IllegalStateException("Unknown account type");
                    }
                };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                DueDateUpdatable.class,
                updateDueDateFunction,
                false);
    }
}
