package br.net.du.sztoks.model.transaction;

import static br.net.du.sztoks.test.TestConstants.newRecurringNonTaxDeductibleDonation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class DonationTest {

    @Test
    public void equals_differentIds() {
        // GIVEN`
        final DonationTransaction first = newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD);
        first.setId(42L);
        final DonationTransaction second = newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD);
        second.setId(77L);

        // THEN
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameIds() {
        // GIVEN`
        final DonationTransaction first = newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD);
        first.setId(42L);
        final DonationTransaction second = newRecurringNonTaxDeductibleDonation(CurrencyUnit.USD);
        second.setId(42L);

        // THEN
        assertTrue(first.equals(second));
    }
}
