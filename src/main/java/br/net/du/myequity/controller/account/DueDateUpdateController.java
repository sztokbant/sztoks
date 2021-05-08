package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.PayableAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.ReceivableAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.DueDateUpdateable;
import br.net.du.myequity.model.account.PayableAccount;
import br.net.du.myequity.model.account.ReceivableAccount;
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
                    final LocalDate dueDate = LocalDate.parse(jsonRequest.getNewValue());
                    ((DueDateUpdateable) account).setDueDate(dueDate);

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
                DueDateUpdateable.class,
                updateDueDateFunction,
                false);
    }
}
