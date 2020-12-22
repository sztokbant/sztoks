package br.net.du.myequity.controller.viewmodel.accountsnapshot;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.ReceivableSnapshot;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ReceivableViewModelOutput extends AccountSnapshotViewModelOutput {
    private final LocalDate dueDate;

    public ReceivableViewModelOutput(
            final AccountSnapshotViewModelOutput accountSnapshotViewModelOutput,
            final LocalDate dueDate) {
        super(accountSnapshotViewModelOutput);
        this.dueDate = dueDate;
    }

    public static ReceivableViewModelOutput of(
            final AccountSnapshot accountSnapshot, final boolean includeTotals) {
        final ReceivableSnapshot receivableSnapshot = (ReceivableSnapshot) accountSnapshot;
        return new ReceivableViewModelOutput(
                AccountSnapshotViewModelOutput.of(accountSnapshot, includeTotals),
                receivableSnapshot.getDueDate());
    }

    public static ReceivableViewModelOutput of(final AccountSnapshot accountSnapshot) {
        return of(accountSnapshot, false);
    }

    @Override
    public int compareTo(final AccountSnapshotViewModelOutput other) {
        if (!(other instanceof ReceivableViewModelOutput)
                || dueDate.equals(((ReceivableViewModelOutput) other).getDueDate())) {
            return super.compareTo(other);
        }

        return dueDate.compareTo(((ReceivableViewModelOutput) other).getDueDate());
    }
}
