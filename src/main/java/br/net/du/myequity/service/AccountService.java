package br.net.du.myequity.service;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private final AccountSnapshotRepository accountSnapshotRepository;

    public void deleteAccount(final Account account) {
        final List<AccountSnapshot> snapshots = accountSnapshotRepository.findAllByAccount(account);
        if (!snapshots.isEmpty()) {
            throw new MyEquityException("Account cannot be deleted as it is referred by at least one snapshot.");
        }
        accountRepository.delete(account);
    }
}
