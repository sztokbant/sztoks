package br.net.du.sztoks.controller.viewmodel.account;

import static br.net.du.sztoks.controller.util.ControllerUtils.toDecimal;
import static br.net.du.sztoks.controller.util.MoneyFormatUtils.format;

import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.PayableAccount;
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
        if (other instanceof PayableAccountViewModelOutput) {
            if (getCurrencyUnit().equals(other.getCurrencyUnit())) {
                if (dueDate.equals(((PayableAccountViewModelOutput) other).getDueDate())) {
                    return getName().compareToIgnoreCase(other.getName());
                }
                return dueDate.compareTo(((PayableAccountViewModelOutput) other).getDueDate());
            }
            return getCurrencyIndex().compareTo(other.getCurrencyIndex());
        } else {
            return super.compareTo(other);
        }
    }
}
