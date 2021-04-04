package br.net.du.myequity.controller.viewmodel.transaction;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
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
    private final String description;
    private final boolean isRecurring;

    // fields only used on updates
    private final String totalForTransactionType;
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
        description = other.getDescription();
        isRecurring = other.isRecurring();

        totalForTransactionType = other.getTotalForTransactionType();
        tithingBalance = other.getTithingBalance();
        netWorth = other.getNetWorth();
        totalLiability = other.getTotalLiability();
    }

    public static TransactionViewModelOutput of(
            final Transaction transaction, final boolean includeTotals) {
        final CurrencyUnit currencyUnit = transaction.getCurrencyUnit();
        final String amount =
                MoneyFormatUtils.format(currencyUnit, toDecimal(transaction.getAmount()));

        final TransactionType transactionType = transaction.getTransactionType();

        final TransactionViewModelOutputBuilder builder =
                TransactionViewModelOutput.builder()
                        .id(transaction.getId())
                        .type(transactionType.name())
                        .snapshotId(transaction.getSnapshot().getId())
                        .date(transaction.getDate())
                        .currencyUnit(transaction.getCurrencyUnit().getCode())
                        .currencyUnitSymbol(transaction.getCurrencyUnit().getSymbol())
                        .amount(amount)
                        .description(transaction.getDescription())
                        .isRecurring(transaction.isRecurring());

        if (includeTotals) {
            final Snapshot snapshot = transaction.getSnapshot();

            final String totalForTransactionType =
                    MoneyFormatUtils.format(
                            snapshot.getBaseCurrencyUnit(),
                            toDecimal(snapshot.getTotalFor(transactionType)));

            builder.totalForTransactionType(totalForTransactionType);

            if (transactionType.equals(TransactionType.INCOME)
                    || transactionType.equals(TransactionType.DONATION)) {
                final String tithingBalance =
                        MoneyFormatUtils.format(
                                snapshot.getBaseCurrencyUnit(),
                                toDecimal(snapshot.getTithingBalance()));

                final String netWorth =
                        MoneyFormatUtils.format(
                                snapshot.getBaseCurrencyUnit(), toDecimal(snapshot.getNetWorth()));

                final String totalLiability =
                        MoneyFormatUtils.format(
                                snapshot.getBaseCurrencyUnit(),
                                toDecimal(snapshot.getTotalFor(AccountType.LIABILITY)));

                builder.tithingBalance(tithingBalance)
                        .netWorth(netWorth)
                        .totalLiability(totalLiability);
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
