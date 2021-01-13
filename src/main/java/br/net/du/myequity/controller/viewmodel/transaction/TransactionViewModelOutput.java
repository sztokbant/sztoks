package br.net.du.myequity.controller.viewmodel.transaction;

import static br.net.du.myequity.controller.util.ControllerUtils.toDecimal;

import br.net.du.myequity.controller.util.MoneyFormatUtils;
import br.net.du.myequity.model.transaction.Transaction;
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
    }

    public static TransactionViewModelOutput of(final Transaction transaction) {
        final CurrencyUnit currencyUnit = transaction.getCurrencyUnit();
        final String amount =
                MoneyFormatUtils.format(currencyUnit, toDecimal(transaction.getAmount()));

        return TransactionViewModelOutput.builder()
                .id(transaction.getId())
                .type(transaction.getTransactionType().getDisplayName())
                .snapshotId(transaction.getSnapshot().getId())
                .date(transaction.getDate())
                .currencyUnit(transaction.getCurrencyUnit().getCode())
                .currencyUnitSymbol(transaction.getCurrencyUnit().getSymbol())
                .amount(amount)
                .description(transaction.getDescription())
                .isRecurring(transaction.isRecurring())
                .build();
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
