package br.net.du.myequity.viewmodel;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PayableViewModelOutput extends SimpleAccountViewModelOutput {
    private final LocalDate dueDate;

    public static PayableViewModelOutput of(final AccountSnapshot accountSnapshot) {
        final PayableSnapshot payableSnapshot = (PayableSnapshot) accountSnapshot;
        return new PayableViewModelOutput(SimpleAccountViewModelOutput.of(accountSnapshot),
                                          payableSnapshot.getDueDate());
    }

    public PayableViewModelOutput(final SimpleAccountViewModelOutput simpleAccountViewModelOutput,
                                  final LocalDate dueDate) {
        super(simpleAccountViewModelOutput);
        this.dueDate = dueDate;
    }

    @Override
    public int compareTo(final SimpleAccountViewModelOutput other) {
        if (!(other instanceof PayableViewModelOutput) ||
                dueDate.equals(((PayableViewModelOutput) other).getDueDate())) {
            return super.compareTo(other);
        }

        return dueDate.compareTo(((PayableViewModelOutput) other).getDueDate());
    }
}
