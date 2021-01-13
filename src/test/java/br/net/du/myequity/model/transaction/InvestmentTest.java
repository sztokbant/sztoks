package br.net.du.myequity.model.transaction;

import static br.net.du.myequity.test.TestConstants.newRecurringInvestment;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvestmentTest {

    @Test
    public void equals_differentIds() {
        // GIVEN`
        final Investment first = newRecurringInvestment();
        first.setId(42L);
        final Investment second = newRecurringInvestment();
        second.setId(77L);

        // THEN
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameIds() {
        // GIVEN`
        final Investment first = newRecurringInvestment();
        first.setId(42L);
        final Investment second = newRecurringInvestment();
        second.setId(42L);

        // THEN
        assertTrue(first.equals(second));
    }
}
