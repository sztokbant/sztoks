package br.net.du.myequity.service;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.persistence.AccountRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public Account save(@NonNull final Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findById(@NonNull final Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Optional<Account> findByIdAndSnapshotId(final Long accountId, final Long snapshotId) {
        return accountRepository.findByIdAndSnapshotId(accountId, snapshotId);
    }

    public List<Account> findBySnapshot(@NonNull final Snapshot snapshot) {
        return accountRepository.findBySnapshot(snapshot);
    }

    @Transactional
    public void delete(@NonNull final Account account) {
        accountRepository.delete(account);
    }
}
