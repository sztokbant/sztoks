package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonResponse;
import br.net.du.myequity.controller.model.DueDateUpdateJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.DueDateUpdateable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.function.BiFunction;

@RestController
public class SnapshotAccountDueDateUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/snapshot/updateAccountDueDate")
    public AccountSnapshotUpdateJsonResponse post(final Model model,
                                                  @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateDueDateFunction = (jsonRequest, accountSnapshot) -> {
            final LocalDate dueDate = LocalDate.parse(jsonRequest.getNewValue());
            ((DueDateUpdateable) accountSnapshot).setDueDate(dueDate);

            return DueDateUpdateJsonResponse.of(accountSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          DueDateUpdateable.class,
                                          updateDueDateFunction);
    }
}
