package br.net.du.myequity.model.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InvestmentAccountTest {

    final Account account = new InvestmentAccount();

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final InvestmentSnapshot actual = (InvestmentSnapshot) account.newEmptySnapshot();

        // THEN
        final InvestmentSnapshot expected =
                new InvestmentSnapshot(account, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
