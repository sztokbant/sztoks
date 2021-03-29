package br.net.du.myequity.service;

import br.net.du.myequity.model.Snapshot;
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

    public Optional<AccountSnapshot> findById(@NonNull final Long accountId) {
        return accountSnapshotRepository.findById(accountId);
    }

    public List<AccountSnapshot> findBySnapshot(@NonNull final Snapshot snapshot) {
        return accountSnapshotRepository.findBySnapshot(snapshot);
    }

    public void delete(@NonNull final AccountSnapshot accountSnapshot) {
        accountSnapshotRepository.delete(accountSnapshot);
    }
}
