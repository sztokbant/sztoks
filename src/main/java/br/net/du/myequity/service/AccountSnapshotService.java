package br.net.du.myequity.service;

import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountSnapshotService {
    private final AccountSnapshotRepository accountSnapshotRepository;

    public AccountSnapshot save(@NonNull final AccountSnapshot accountSnapshot) {
        return accountSnapshotRepository.save(accountSnapshot);
    }

    public Optional<AccountSnapshot> findBySnapshotIdAndAccountId(
            @NonNull final Long snapshotId, @NonNull final Long accountId) {
        return accountSnapshotRepository.findBySnapshotIdAndAccountId(snapshotId, accountId);
    }

    public List<AccountSnapshot> findAllByAccount(@NonNull final Account account) {
        return accountSnapshotRepository.findAllByAccount(account);
    }
}
