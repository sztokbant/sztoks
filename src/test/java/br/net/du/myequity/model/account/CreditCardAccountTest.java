package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CreditCardAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final CreditCardAccount actual =
                new CreditCardAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        // THEN
        final CreditCardAccount expected =
                new CreditCardAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
