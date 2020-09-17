package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.ReceivableSnapshot;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReceivableViewModelOutput extends AccountViewModelOutputBase {
    private final LocalDate dueDate;

    public static ReceivableViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final ReceivableSnapshot receivableSnapshot = (ReceivableSnapshot) accountSnapshot;
        return new ReceivableViewModelOutput(AccountViewModelOutputBase.of(accountSnapshot),
                                             receivableSnapshot.getDueDate());
    }

    public ReceivableViewModelOutput(final AccountViewModelOutputBase simpleAccountViewModelOutput,
                                     final LocalDate dueDate) {
        super(simpleAccountViewModelOutput);
        this.dueDate = dueDate;
    }

    @Override
    public int compareTo(final AccountViewModelOutputBase other) {
        if (!(other instanceof ReceivableViewModelOutput) ||
                dueDate.equals(((ReceivableViewModelOutput) other).getDueDate())) {
            return super.compareTo(other);
        }

        return dueDate.compareTo(((ReceivableViewModelOutput) other).getDueDate());
    }
}
