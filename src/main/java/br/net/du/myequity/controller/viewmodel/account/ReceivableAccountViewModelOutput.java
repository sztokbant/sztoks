package br.net.du.myequity.controller.viewmodel.account;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.FutureTithingCapable;
import br.net.du.myequity.model.account.ReceivableAccount;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ReceivableAccountViewModelOutput extends AccountViewModelOutput {
    private final LocalDate dueDate;
    private final String futureTithingPolicy;

    public ReceivableAccountViewModelOutput(
            final AccountViewModelOutput other,
            final LocalDate dueDate,
            final String futureTithingPolicy) {
        super(other);
        this.dueDate = dueDate;
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public static ReceivableAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final ReceivableAccount receivableAccount = (ReceivableAccount) account;

        final String futureTithingPolicy =
                ((FutureTithingCapable) account).getFutureTithingPolicy().getShortValue();

        return new ReceivableAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                receivableAccount.getDueDate(),
                futureTithingPolicy);
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
