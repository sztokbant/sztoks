package br.net.du.myequity.model.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CreditCardAccountTest {

    final Account account = new CreditCardAccount();

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final CreditCardSnapshot actual = (CreditCardSnapshot) account.newEmptySnapshot();

        // THEN
        final CreditCardSnapshot expected =
                new CreditCardSnapshot(account, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
