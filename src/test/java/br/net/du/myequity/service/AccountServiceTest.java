package br.net.du.myequity.service;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.SimpleLiabilityAccount;
import br.net.du.myequity.persistence.AccountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class AccountServiceTest {

    private static final Account SIMPLE_LIABILITY_SNAPSHOT =
            new SimpleLiabilityAccount(
                    "Mortgage", CurrencyUnit.USD, LocalDate.now(), new BigDecimal("10000.00"));

    @Mock private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        accountService = new AccountService(accountRepository);
    }

    @Test
    public void deleteAccount_happy() {
        // WHEN
        accountService.delete(SIMPLE_LIABILITY_SNAPSHOT);

        // THEN
        verify(accountRepository).delete(SIMPLE_LIABILITY_SNAPSHOT);
    }
}
