package br.net.du.myequity.controller.viewmodel.transaction;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;
import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;
import static br.net.du.myequity.controller.util.MoneyFormatUtils.format;

import br.net.du.myequity.controller.viewmodel.UpdatableTotals;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
import br.net.du.myequity.model.transaction.Categorizable;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;

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
    private final String recurrencePolicy;

    // Categorizable only
    private final String category;

    // fields only used on updates
    private final String totalForTransactionType;
    private final String taxDeductibleDonationsTotal;
    private final String tithingBalance;
    private final String totalTithingBalance;
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
        recurrencePolicy = other.getRecurrencePolicy();
        category = other.getCategory();

        totalForTransactionType = other.getTotalForTransactionType();
        taxDeductibleDonationsTotal = other.taxDeductibleDonationsTotal;
        tithingBalance = other.getTithingBalance();
        totalTithingBalance = other.getTotalTithingBalance();
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
                        .recurrencePolicy(transaction.getRecurrencePolicy().getShortValue());

        final String category =
                transaction instanceof Categorizable
                        ? ((Categorizable) transaction).getCategory().name()
                        : null;
        builder.category(category);

        if (includeTotals) {
            final UpdatableTotals updatableTotals = new UpdatableTotals(transaction.getSnapshot());

            builder.totalForTransactionType(updatableTotals.getTotalFor(transactionType));

            if (transactionType.equals(TransactionType.INCOME)
                    || transactionType.equals(TransactionType.DONATION)) {
                if (transactionType.equals(TransactionType.DONATION)) {
                    builder.taxDeductibleDonationsTotal(
                            updatableTotals.getTaxDeductibleDonationsTotal());
                }

                builder.tithingBalance(updatableTotals.getTithingBalance())
                        .totalTithingBalance(
                                updatableTotals.getTotalForAccountSubtype(
                                        AccountSubtypeDisplayGroup.TITHING))
                        .totalLiability(updatableTotals.getTotalFor(AccountType.LIABILITY))
                        .netWorth(updatableTotals.getNetWorth());
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
