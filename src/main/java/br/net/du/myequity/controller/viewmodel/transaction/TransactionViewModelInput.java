package br.net.du.myequity.controller.viewmodel.transaction;

import br.net.du.myequity.model.transaction.DonationCategory;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeCategory;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentCategory;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.model.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TransactionViewModelInput {
    private String typeName;

    private String date;
    private String currencyUnit;
    private String amount;
    private String description;
    private Boolean isRecurring;

    // Categorizable only
    private String category;

    // INCOME only
    private String tithingPercentage;

    // DONATION only
    private Boolean isTaxDeductible;

    public Transaction toTransaction() {
        final TransactionType transactionType = TransactionType.valueOf(typeName);
        final LocalDate localDate = LocalDate.parse(date);

        if (transactionType.equals(TransactionType.INCOME)) {
            return new IncomeTransaction(
                    localDate,
                    currencyUnit,
                    new BigDecimal(amount),
                    description,
                    isRecurring,
                    new BigDecimal(tithingPercentage),
                    IncomeCategory.valueOf(category));
        } else if (transactionType.equals(TransactionType.INVESTMENT)) {
            return new InvestmentTransaction(
                    localDate,
                    currencyUnit,
                    new BigDecimal(amount),
                    description,
                    isRecurring,
                    InvestmentCategory.valueOf(category));
        } else if (transactionType.equals(TransactionType.DONATION)) {
            return new DonationTransaction(
                    localDate,
                    currencyUnit,
                    new BigDecimal(amount),
                    description,
                    isRecurring,
                    isTaxDeductible,
                    DonationCategory.valueOf(category));
        }

        throw new IllegalArgumentException("typeName: " + typeName);
    }
}
