package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.PayableAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.ReceivableAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.DueDateUpdateable;
import br.net.du.myequity.model.account.PayableAccount;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DueDateUpdateController extends AccountUpdateControllerBase {

    @PostMapping("/snapshot/updateAccountDueDate")
    public AccountViewModelOutput post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateDueDateFunction =
                        (jsonRequest, account) -> {
                            final LocalDate dueDate = LocalDate.parse(jsonRequest.getNewValue());
                            ((DueDateUpdateable) account).setDueDate(dueDate);

                            if (account instanceof PayableAccount) {
                                return PayableAccountViewModelOutput.of(account);
                            }
                            return ReceivableAccountViewModelOutput.of(account);
                        };

        return updateAccountField(
                model, valueUpdateJsonRequest, DueDateUpdateable.class, updateDueDateFunction);
    }
}
