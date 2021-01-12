package br.net.du.myequity.model.transaction;

import static br.net.du.myequity.test.TestConstants.RETIREMENT_FUND_INVESTMENT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvestmentTest {

    @Test
    public void equals_differentIds() {
        // GIVEN`
        final Investment first = RETIREMENT_FUND_INVESTMENT.copy();
        first.setId(42L);
        final Investment second = RETIREMENT_FUND_INVESTMENT.copy();
        second.setId(77L);

        // THEN
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameIds() {
        // GIVEN`
        final Investment first = RETIREMENT_FUND_INVESTMENT.copy();
        first.setId(42L);
        final Investment second = RETIREMENT_FUND_INVESTMENT.copy();
        second.setId(42L);

        // THEN
        assertTrue(first.equals(second));
    }
}
