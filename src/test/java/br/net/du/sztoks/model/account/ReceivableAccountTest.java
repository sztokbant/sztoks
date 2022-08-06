package br.net.du.sztoks.model.account;

import static br.net.du.sztoks.test.ModelTestUtils.equalsIgnoreId;
import static br.net.du.sztoks.test.TestConstants.ACCOUNT_NAME;
import static br.net.du.sztoks.test.TestConstants.CURRENCY_UNIT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ReceivableAccountTest {

    @Test
    public void newEmptySnapshot_happy() {
        // GIVEN
        final LocalDate now = LocalDate.of(2020, 12, 31);

        // WHEN
        final ReceivableAccount actual;
        try (MockedStatic<LocalDate> localDateStaticMock = mockStatic(LocalDate.class)) {
            localDateStaticMock.when(LocalDate::now).thenReturn(now);
            actual =
                    new ReceivableAccount(
                            ACCOUNT_NAME,
                            CURRENCY_UNIT,
                            FutureTithingPolicy.NONE,
                            now,
                            now,
                            BigDecimal.ZERO,
                            false);
        }

        // THEN
        final ReceivableAccount expected =
                new ReceivableAccount(
                        ACCOUNT_NAME,
                        CURRENCY_UNIT,
                        FutureTithingPolicy.NONE,
                        now,
                        now,
                        BigDecimal.ZERO,
                        false);

        assertTrue(equalsIgnoreId(actual, expected));
    }
}
