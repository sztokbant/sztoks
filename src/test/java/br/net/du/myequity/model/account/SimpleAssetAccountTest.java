package br.net.du.myequity.model.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimpleAssetAccountTest {
    final Account account = new SimpleAssetAccount();

    @Test
    public void newEmptySnapshot_happy() {
        // WHEN
        final SimpleAssetSnapshot actual = (SimpleAssetSnapshot) account.newEmptySnapshot();

        // THEN
        final SimpleAssetSnapshot expected = new SimpleAssetSnapshot(account, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
