package br.net.du.myequity.model.transaction;

import static br.net.du.myequity.test.TestConstants.newRecurringDonation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DonationTest {

    @Test
    public void equals_differentIds() {
        // GIVEN`
        final Donation first = newRecurringDonation();
        first.setId(42L);
        final Donation second = newRecurringDonation();
        second.setId(77L);

        // THEN
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameIds() {
        // GIVEN`
        final Donation first = newRecurringDonation();
        first.setId(42L);
        final Donation second = newRecurringDonation();
        second.setId(42L);

        // THEN
        assertTrue(first.equals(second));
    }
}
