package br.net.du.myequity.controller.viewmodel.account;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.PayableAccount;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PayableAccountViewModelOutput extends AccountViewModelOutput {
    private final LocalDate dueDate;

    public PayableAccountViewModelOutput(
            final AccountViewModelOutput other, final LocalDate dueDate) {
        super(other);
        this.dueDate = dueDate;
    }

    public static PayableAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final PayableAccount payableAccount = (PayableAccount) account;
        return new PayableAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals), payableAccount.getDueDate());
    }

    public static PayableAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        if (!(other instanceof PayableAccountViewModelOutput)
                || dueDate.equals(((PayableAccountViewModelOutput) other).getDueDate())) {
            return super.compareTo(other);
        }

        return dueDate.compareTo(((PayableAccountViewModelOutput) other).getDueDate());
    }
}
