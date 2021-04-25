package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SharedBillReceivableAccount;
import lombok.Getter;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Getter
public class SharedBillReceivableAccountViewModelOutput extends AccountViewModelOutput {
    private final String billAmount;
    private final Integer numberOfPartners;
    private final Integer dueDay;

    public SharedBillReceivableAccountViewModelOutput(
            @NonNull final AccountViewModelOutput other,
            @NonNull final String billAmount,
            final int numberOfPartners,
            final int dueDay) {
        super(other);
        this.billAmount = billAmount;
        this.numberOfPartners = numberOfPartners;
        this.dueDay = dueDay;
    }

    public static SharedBillReceivableAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final SharedBillReceivableAccount sharedBillReceivableAccount =
                (SharedBillReceivableAccount) account;

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String billAmount =
                format(currencyUnit, toDecimal(sharedBillReceivableAccount.getBillAmount()));
        final Integer numberOfPartners = sharedBillReceivableAccount.getNumberOfPartners();
        final Integer dueDay = sharedBillReceivableAccount.getDueDay();

        return new SharedBillReceivableAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                billAmount,
                numberOfPartners,
                dueDay);
    }

    public static SharedBillReceivableAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        if (!(other instanceof SharedBillReceivableAccountViewModelOutput)
                || dueDay.equals(
                        ((SharedBillReceivableAccountViewModelOutput) other).getDueDay())) {
            return super.compareTo(other);
        }

        return dueDay.compareTo(((SharedBillReceivableAccountViewModelOutput) other).getDueDay());
    }
}
