package br.net.du.myequity.service;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class AccountServiceTest {

    private static final AccountSnapshot SIMPLE_LIABILITY_SNAPSHOT =
            new SimpleLiabilitySnapshot(
                    "Mortgage", CurrencyUnit.USD, LocalDate.now(), new BigDecimal("10000.00"));

    @Mock private AccountSnapshotRepository accountSnapshotRepository;

    private AccountSnapshotService accountSnapshotService;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        accountSnapshotService = new AccountSnapshotService(accountSnapshotRepository);
    }

    @Test
    public void deleteAccount_happy() {
        // WHEN
        accountSnapshotService.delete(SIMPLE_LIABILITY_SNAPSHOT);

        // THEN
        verify(accountSnapshotRepository).delete(SIMPLE_LIABILITY_SNAPSHOT);
    }
}
