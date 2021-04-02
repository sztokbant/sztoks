package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimpleLiabilityAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final SimpleLiabilityAccount actual =
                new SimpleLiabilityAccount(ACCOUNT_NAME, CURRENCY_UNIT);

        // THEN
        final SimpleLiabilityAccount expected =
                new SimpleLiabilityAccount(ACCOUNT_NAME, CURRENCY_UNIT, BigDecimal.ZERO);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
