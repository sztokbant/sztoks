package br.net.du.myequity.controller.viewmodel.account;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.FutureTithingCapable;
import br.net.du.myequity.model.account.SharedBillAccount;
import lombok.Getter;
import lombok.NonNull;
import org.joda.money.CurrencyUnit;

@Getter
public class SharedBillAccountViewModelOutput extends AccountViewModelOutput {
    private final String billAmount;
    private final Boolean isPaid;
    private final Integer numberOfPartners;
    private final Integer dueDay;
    private final String futureTithingPolicy;

    public SharedBillAccountViewModelOutput(
            @NonNull final AccountViewModelOutput other,
            @NonNull final String billAmount,
            final boolean isPaid,
            final int numberOfPartners,
            final int dueDay,
            final String futureTithingPolicy) {
        super(other);
        this.billAmount = billAmount;
        this.isPaid = isPaid;
        this.numberOfPartners = numberOfPartners;
        this.dueDay = dueDay;
        this.futureTithingPolicy = futureTithingPolicy;
    }

    public static SharedBillAccountViewModelOutput of(
            final Account account, final boolean includeTotals) {
        final SharedBillAccount paymentCapableAccount = (SharedBillAccount) account;

        final CurrencyUnit currencyUnit = account.getCurrencyUnit();

        final String billAmount =
                format(currencyUnit, toDecimal(paymentCapableAccount.getBillAmount()));
        final boolean isPaid = paymentCapableAccount.isPaid();
        final Integer numberOfPartners = paymentCapableAccount.getNumberOfPartners();
        final Integer dueDay = paymentCapableAccount.getDueDay();

        final String futureTithingPolicy =
                account instanceof FutureTithingCapable
                        ? ((FutureTithingCapable) account).getFutureTithingPolicy().name()
                        : null;

        return new SharedBillAccountViewModelOutput(
                AccountViewModelOutput.of(account, includeTotals),
                billAmount,
                isPaid,
                numberOfPartners,
                dueDay,
                futureTithingPolicy);
    }

    public static SharedBillAccountViewModelOutput of(final Account account) {
        return of(account, false);
    }

    @Override
    public int compareTo(final AccountViewModelOutput other) {
        if (other instanceof SharedBillAccountViewModelOutput) {
            if (getCurrencyUnit().equals(other.getCurrencyUnit())) {
                if (dueDay.equals(((SharedBillAccountViewModelOutput) other).getDueDay())) {
                    return getName().compareToIgnoreCase(other.getName());
                }
                return dueDay.compareTo(((SharedBillAccountViewModelOutput) other).getDueDay());
            }
            return getCurrencyIndex().compareTo(other.getCurrencyIndex());
        } else {
            return super.compareTo(other);
        }
    }
}
