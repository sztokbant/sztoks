package br.net.du.sztoks.model.transaction;

import static br.net.du.sztoks.test.TestConstants.newRecurringInvestment;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvestmentTest {

    @Test
    public void equals_differentIds() {
        // GIVEN`
        final InvestmentTransaction first = newRecurringInvestment();
        first.setId(42L);
        final InvestmentTransaction second = newRecurringInvestment();
        second.setId(77L);

        // THEN
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameIds() {
        // GIVEN`
        final InvestmentTransaction first = newRecurringInvestment();
        first.setId(42L);
        final InvestmentTransaction second = newRecurringInvestment();
        second.setId(42L);

        // THEN
        assertTrue(first.equals(second));
    }
}
