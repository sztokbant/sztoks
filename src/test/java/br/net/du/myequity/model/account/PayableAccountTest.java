package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import br.net.du.myequity.model.snapshot.PayableSnapshot;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class PayableAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // GIVEN
        final LocalDate now = LocalDate.of(2020, 12, 31);

        // WHEN
        final PayableSnapshot actual;
        try (MockedStatic<LocalDate> localDateStaticMock = mockStatic(LocalDate.class)) {
            localDateStaticMock.when(LocalDate::now).thenReturn(now);
            actual = new PayableSnapshot(ACCOUNT_NAME, CURRENCY_UNIT);
        }

        // THEN
        final PayableSnapshot expected =
                new PayableSnapshot(ACCOUNT_NAME, CURRENCY_UNIT, now, BigDecimal.ZERO);

        assertTrue(actual.equalsIgnoreId(expected));
    }
}
