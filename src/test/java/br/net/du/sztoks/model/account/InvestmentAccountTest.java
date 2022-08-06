package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.sztoks.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InvestmentAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final InvestmentAccount actual =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        // THEN
        final InvestmentAccount expected =
                new InvestmentAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
