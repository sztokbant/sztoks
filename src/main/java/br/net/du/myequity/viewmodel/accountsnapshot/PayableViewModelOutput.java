package br.net.du.myequity.viewmodel.accountsnapshot;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PayableViewModelOutput extends AccountSnapshotViewModelOutput {
    private final LocalDate dueDate;

    public PayableViewModelOutput(final AccountSnapshotViewModelOutput accountSnapshotViewModelOutput,
                                  final LocalDate dueDate) {
        super(accountSnapshotViewModelOutput);
        this.dueDate = dueDate;
    }

    public static PayableViewModelOutput of(final AccountSnapshot accountSnapshot, final boolean includeTotals) {
        final PayableSnapshot payableSnapshot = (PayableSnapshot) accountSnapshot;
        return new PayableViewModelOutput(AccountSnapshotViewModelOutput.of(accountSnapshot, includeTotals),
                                          payableSnapshot.getDueDate());
    }

    public static PayableViewModelOutput of(final AccountSnapshot accountSnapshot) {
        return of(accountSnapshot, false);
    }

    @Override
    public int compareTo(final AccountSnapshotViewModelOutput other) {
        if (!(other instanceof PayableViewModelOutput) ||
                dueDate.equals(((PayableViewModelOutput) other).getDueDate())) {
            return super.compareTo(other);
        }

        return dueDate.compareTo(((PayableViewModelOutput) other).getDueDate());
    }
}
