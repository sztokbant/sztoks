package br.net.du.myequity.controller.viewmodel.account;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.ReceivableAccount;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ReceivableAccountViewModelOutput extends AccountViewModelOutput {
    private final LocalDate dueDate;

    public ReceivableAccountViewModelOutput(
            final AccountViewModelOutput other, final LocalDate dueDate) {
        super(other);
        this.dueDate = dueDate;
    }

    public static ReceivableAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final ReceivableAccount receivableAccount = (ReceivableAccount) account;
        return new ReceivableAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals), receivableAccount.getDueDate());
    }

    public static ReceivableAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        if (!(other instanceof ReceivableAccountViewModelOutput)
                || dueDate.equals(((ReceivableAccountViewModelOutput) other).getDueDate())) {
            return super.compareTo(other);
        }

        return dueDate.compareTo(((ReceivableAccountViewModelOutput) other).getDueDate());
    }
}
