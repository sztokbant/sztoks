package br.net.du.myequity.controller.viewmodel.transaction;

import br.net.du.myequity.controller.viewmodel.UpdateableTotals;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

import java.time.LocalDate;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

@AllArgsConstructor
@Data
@Builder
public class TransactionViewModelOutput implements Comparable<TransactionViewModelOutput> {
    private final Long id;
    private final String type;
    private final Long snapshotId;
    private final LocalDate date;
    private final String currencyUnit;
    private final String currencyUnitSymbol;
    private final String amount;
    private final String tithingPercentage;
    private final String description;
    private final boolean isRecurring;

    // fields only used on updates
    private final String totalForTransactionType;
    private final String taxDeductibleDonationsTotal;
    private final String tithingBalance;
    private final String netWorth;
    private final String totalLiability;

    public TransactionViewModelOutput(final TransactionViewModelOutput other) {
        id = other.getId();
        type = other.getType();
        snapshotId = other.getSnapshotId();
        date = other.getDate();
        currencyUnit = other.getCurrencyUnit();
        currencyUnitSymbol = other.getCurrencyUnitSymbol();
        amount = other.getAmount();
        tithingPercentage = other.getTithingPercentage();
        description = other.getDescription();
        isRecurring = other.isRecurring();

        totalForTransactionType = other.getTotalForTransactionType();
        taxDeductibleDonationsTotal = other.taxDeductibleDonationsTotal;
        tithingBalance = other.getTithingBalance();
        netWorth = other.getNetWorth();
        totalLiability = other.getTotalLiability();
    }

    public static TransactionViewModelOutput of(
            final Transaction transaction, final boolean includeTotals) {
        final CurrencyUnit currencyUnit = transaction.getCurrencyUnit();
        final String amount = format(currencyUnit, toDecimal(transaction.getAmount()));

        final TransactionType transactionType = transaction.getTransactionType();

        final String tithingPercentage =
                transaction.getTransactionType().equals(TransactionType.INCOME)
                        ? formatAsPercentage(
                                ((IncomeTransaction) transaction).getTithingPercentage())
                        : null;

        final TransactionViewModelOutputBuilder builder =
                TransactionViewModelOutput.builder()
                        .id(transaction.getId())
                        .type(transactionType.name())
                        .snapshotId(transaction.getSnapshot().getId())
                        .date(transaction.getDate())
                        .currencyUnit(transaction.getCurrencyUnit().getCode())
                        .currencyUnitSymbol(transaction.getCurrencyUnit().getSymbol())
                        .amount(amount)
                        .tithingPercentage(tithingPercentage)
                        .description(transaction.getDescription())
                        .isRecurring(transaction.isRecurring());

        if (includeTotals) {
            final UpdateableTotals updateableTotals =
                    new UpdateableTotals(transaction.getSnapshot());

            builder.totalForTransactionType(updateableTotals.getTotalFor(transactionType));

            if (transactionType.equals(TransactionType.INCOME)
                    || transactionType.equals(TransactionType.DONATION)) {
                if (transactionType.equals(TransactionType.DONATION)) {
                    builder.taxDeductibleDonationsTotal(
                            updateableTotals.getTaxDeductibleDonationsTotal());
                }

                builder.tithingBalance(updateableTotals.getTithingBalance())
                        .totalLiability(updateableTotals.getTotalFor(AccountType.LIABILITY))
                        .netWorth(updateableTotals.getNetWorth());
            }
        }

        return builder.build();
    }

    @Override
    public int compareTo(final TransactionViewModelOutput other) {
        if (currencyUnit.equals(other.getCurrencyUnit())) {
            if (date.equals(other.getDate())) {
                return description.compareTo(other.getDescription());
            }
            return date.compareTo(other.getDate());
        }
        return currencyUnit.compareTo(other.getCurrencyUnit());
    }
}
