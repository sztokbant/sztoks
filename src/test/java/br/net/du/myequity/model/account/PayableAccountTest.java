package br.net.du.myequity.model.account;

import static br.net.du.myequity.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.myequity.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.myequity.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

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
        final PayableAccount actual;
        try (MockedStatic<LocalDate> localDateStaticMock = mockStatic(LocalDate.class)) {
            localDateStaticMock.when(LocalDate::now).thenReturn(now);
            actual =
                    new PayableAccount(
                            ACCOUNT_NAME,
                            CURRENCY_UNIT,
                            now,
                            LocalDate.now(),
                            BigDecimal.ZERO,
                            false);
        }

        // THEN
        final PayableAccount expected =
                new PayableAccount(ACCOUNT_NAME, CURRENCY_UNIT, now, now, BigDecimal.ZERO, false);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
