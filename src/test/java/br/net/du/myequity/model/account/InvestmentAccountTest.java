package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InvestmentAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final InvestmentSnapshot actual = new InvestmentSnapshot(ACCOUNT_NAME, CURRENCY_UNIT);

        // THEN
        final InvestmentSnapshot expected =
                new InvestmentSnapshot(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
