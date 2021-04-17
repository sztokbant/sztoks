package br.net.du.myequity.controller.viewmodel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelInput;
import br.net.du.myequity.model.transaction.DonationTransaction;
import br.net.du.myequity.model.transaction.IncomeTransaction;
import br.net.du.myequity.model.transaction.InvestmentTransaction;
import br.net.du.myequity.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionViewModelInputTest {

    private TransactionViewModelInput transactionViewModelInput;

    @BeforeEach
    public void setUp() {
        transactionViewModelInput = new TransactionViewModelInput();
        populateCommonAttributes(transactionViewModelInput);
    }

    @Test
    public void toTransaction_incomeTransaction_happy() {
        // GIVEN
        populateIncomeTransactionAttributes(transactionViewModelInput);

        // WHEN
        final Transaction transaction = transactionViewModelInput.toTransaction();

        // THEN
        assertTrue(transaction instanceof IncomeTransaction);
    }

    @Test
    public void toTransaction_investmentTransaction_happy() {
        // GIVEN
        populateInvestmentTransactionAttributes(transactionViewModelInput);

        // WHEN
        final Transaction transaction = transactionViewModelInput.toTransaction();

        // THEN
        assertTrue(transaction instanceof InvestmentTransaction);
    }

    @Test
    public void toTransaction_donationTransaction_happy() {
        // GIVEN
        populateDonationTransactionAttributes(transactionViewModelInput);

        // WHEN
        final Transaction transaction = transactionViewModelInput.toTransaction();

        // THEN
        assertTrue(transaction instanceof DonationTransaction);
    }

    @Test
    public void toTransaction_noTransactionType_throws() {
        // WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> {
                    transactionViewModelInput.toTransaction();
                });
    }

    @Test
    public void toTransaction_invalidTransactionType_throws() {
        // GIVEN
        transactionViewModelInput.setTypeName("INVALID_TYPE");

        // WHEN/THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    transactionViewModelInput.toTransaction();
                });
    }

    public static void populateCommonAttributes(
            final TransactionViewModelInput transactionViewModelInput) {
        transactionViewModelInput.setDate("2021-03-27");
        transactionViewModelInput.setCurrencyUnit("USD");
        transactionViewModelInput.setAmount("108.00");
        transactionViewModelInput.setDescription("Transaction Description");
        transactionViewModelInput.setIsRecurring(true);
    }

    public static void populateIncomeTransactionAttributes(
            final TransactionViewModelInput transactionViewModelInput) {
        transactionViewModelInput.setTypeName("INCOME");
        transactionViewModelInput.setTithingPercentage("30.00");
    }

    public static void populateInvestmentTransactionAttributes(
            final TransactionViewModelInput transactionViewModelInput) {
        transactionViewModelInput.setTypeName("INVESTMENT");
        transactionViewModelInput.setCategory("LONG_TERM");
    }

    public static void populateDonationTransactionAttributes(
            final TransactionViewModelInput transactionViewModelInput) {
        transactionViewModelInput.setTypeName("DONATION");
        transactionViewModelInput.setIsTaxDeductible(true);
    }
}
