package br.net.du.myequity.model.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimpleLiabilityAccountTest {
    final Account account = new SimpleLiabilityAccount();

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final SimpleLiabilitySnapshot actual = (SimpleLiabilitySnapshot) account.newEmptySnapshot();

        // THEN
        final SimpleLiabilitySnapshot expected =
                new SimpleLiabilitySnapshot(account, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
