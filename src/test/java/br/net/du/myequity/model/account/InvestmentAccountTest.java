package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InvestmentAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final InvestmentAccount actual =
                new InvestmentAccount(ACCOUNT_NAME, CURRENCY_UNIT, FutureTithingPolicy.NONE);

        // THEN
        final InvestmentAccount expected =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
