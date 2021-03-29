package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimpleLiabilityAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final SimpleLiabilitySnapshot actual =
                new SimpleLiabilitySnapshot(ACCOUNT_NAME, CURRENCY_UNIT);

        // THEN
        final SimpleLiabilitySnapshot expected =
                new SimpleLiabilitySnapshot(ACCOUNT_NAME, CURRENCY_UNIT, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
