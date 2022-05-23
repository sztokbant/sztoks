package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.FutureTithingCapable;
import br.net.du.myequity.model.account.ReceivableAccount;
import java.time.LocalDate;
import lombok.Getter;
import org.joda.money.CurrencyUnit;

@Getter
public class ReceivableAccountViewModelOutput extends AccountViewModelOutput {
    private final String billAmount;
    private final LocalDate dueDate;
    private final Boolean isPaid;
    private final String futureTithingPolicy;

    public ReceivableAccountViewModelOutput(
            final AccountViewModelOutput other,
            final String billAmount,
            final LocalDate dueDate,
            final Boolean isPaid,
            final String futureTithingPolicy) {
        super(other);
        this.billAmount = billAmount;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public static ReceivableAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final ReceivableAccount receivableAccount = (ReceivableAccount) account;

        final String futureTithingPolicy =
                ((FutureTithingCapable) account).getFutureTithingPolicy().name();

        final CurrencyUnit currencyUnit = receivableAccount.getCurrencyUnit();

        return new ReceivableAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                format(currencyUnit, toDecimal(receivableAccount.getBillAmount())),
                receivableAccount.getDueDate(),
                receivableAccount.isPaid(),
                futureTithingPolicy);
    }

    public static ReceivableAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        if (other instanceof ReceivableAccountViewModelOutput) {
            if (getCurrencyUnit().equals(other.getCurrencyUnit())) {
                if (dueDate.equals(((ReceivableAccountViewModelOutput) other).getDueDate())) {
                    return getName().compareTo(other.getName());
                }
                return dueDate.compareTo(((ReceivableAccountViewModelOutput) other).getDueDate());
            }
            return getCurrencyIndex().compareTo(other.getCurrencyIndex());
        } else {
            return super.compareTo(other);
        }
    }
}
