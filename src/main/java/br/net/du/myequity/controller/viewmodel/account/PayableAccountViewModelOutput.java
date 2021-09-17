package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.PayableAccount;
import java.time.LocalDate;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class PayableAccountViewModelOutput extends AccountViewModelOutput {
    private final String billAmount;
    private final LocalDate dueDate;
    private final Boolean isPaid;

    public PayableAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String billAmount,
            final LocalDate dueDate,
            final Boolean isPaid) {
        super(other);
        this.billAmount = billAmount;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
    }

    public static PayableAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final PayableAccount payableAccount = (PayableAccount) account;

        final CurrencyUnit currencyUnit = payableAccount.getCurrencyUnit();

        return new PayableAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                format(currencyUnit, toDecimal(payableAccount.getBillAmount())),
                payableAccount.getDueDate(),
                payableAccount.isPaid());
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
