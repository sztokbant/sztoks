package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimpleAssetAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final SimpleAssetSnapshot actual = new SimpleAssetSnapshot(ACCOUNT_NAME, CURRENCY_UNIT);

        // THEN
        final SimpleAssetSnapshot expected =
                new SimpleAssetSnapshot(ACCOUNT_NAME, CURRENCY_UNIT, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
