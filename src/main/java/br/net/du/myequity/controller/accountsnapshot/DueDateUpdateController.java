package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.DueDateUpdateable;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import br.net.du.myequity.viewmodel.AccountSnapshotViewModelOutput;
import br.net.du.myequity.viewmodel.PayableViewModelOutput;
import br.net.du.myequity.viewmodel.ReceivableViewModelOutput;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.function.BiFunction;

@RestController
public class DueDateUpdateController extends UpdateControllerBase {

    @PostMapping("/snapshot/updateAccountDueDate")
    public AccountSnapshotViewModelOutput post(final Model model,
                                               @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateDueDateFunction = (jsonRequest, accountSnapshot) -> {
            final LocalDate dueDate = LocalDate.parse(jsonRequest.getNewValue());
            ((DueDateUpdateable) accountSnapshot).setDueDate(dueDate);

            if (accountSnapshot instanceof PayableSnapshot) {
                return PayableViewModelOutput.of(accountSnapshot);
            }
            return ReceivableViewModelOutput.of(accountSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          DueDateUpdateable.class,
                                          updateDueDateFunction);
    }
}
