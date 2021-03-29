package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CreditCardAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final CreditCardAccount actual = new CreditCardAccount(ACCOUNT_NAME, CURRENCY_UNIT);

        // THEN
        final CreditCardAccount expected =
                new CreditCardAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
