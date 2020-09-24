package br.net.du.myequity.controller.model;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.DueDateUpdateable;
import lombok.Getter;

@Getter
public class DueDateUpdateJsonResponse extends AccountSnapshotUpdateJsonResponse {
    private String dueDate;

    public DueDateUpdateJsonResponse(final AccountSnapshotUpdateJsonResponse snapshotAccountUpdateJsonResponse,
                                     final String dueDate) {
        super(snapshotAccountUpdateJsonResponse);
        this.dueDate = dueDate;
    }

    public static DueDateUpdateJsonResponse of(final AccountSnapshot accountSnapshot) {
        final String dueDate = ((DueDateUpdateable) accountSnapshot).getDueDate().toString();

        return new DueDateUpdateJsonResponse(AccountSnapshotUpdateJsonResponse.of(accountSnapshot), dueDate);
    }
}
