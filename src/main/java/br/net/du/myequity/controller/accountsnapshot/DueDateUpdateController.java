package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.viewmodel.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.PayableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.ReceivableViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.DueDateUpdateable;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DueDateUpdateController extends UpdateControllerBase {

    @PostMapping("/snapshot/updateAccountDueDate")
    public AccountSnapshotViewModelOutput post(
            final Model model,
            @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<
                        AccountSnapshotUpdateJsonRequest,
                        AccountSnapshot,
                        AccountSnapshotViewModelOutput>
                updateDueDateFunction =
                        (jsonRequest, accountSnapshot) -> {
                            final LocalDate dueDate = LocalDate.parse(jsonRequest.getNewValue());
                            ((DueDateUpdateable) accountSnapshot).setDueDate(dueDate);

                            if (accountSnapshot instanceof PayableSnapshot) {
                                return PayableViewModelOutput.of(accountSnapshot);
                            }
                            return ReceivableViewModelOutput.of(accountSnapshot);
                        };

        return updateAccountSnapshotField(
                model,
                accountSnapshotUpdateJsonRequest,
                DueDateUpdateable.class,
                updateDueDateFunction);
    }
}
