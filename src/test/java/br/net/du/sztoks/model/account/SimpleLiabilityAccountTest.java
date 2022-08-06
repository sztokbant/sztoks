package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.sztoks.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SimpleLiabilityAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final SimpleLiabilityAccount actual =
                new SimpleLiabilityAccount(
                        ACCOUNT_NAME, CURRENCY_UNIT, LocalDate.now(), BigDecimal.ZERO);

        // THEN
        final SimpleLiabilityAccount expected =
                new SimpleLiabilityAccount(
                        ACCOUNT_NAME, CURRENCY_UNIT, LocalDate.now(), BigDecimal.ZERO);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
