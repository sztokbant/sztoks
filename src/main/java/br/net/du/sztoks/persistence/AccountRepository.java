package br.net.du.sztoks.persistence;

import br.net.du.sztoks.model.Snapshot;
import br.net.du.sztoks.model.account.Account;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

public interface AccountRepository extends CustomRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIdAndSnapshotId(Long accountId, Long snapshotId);

    List<Account> findBySnapshot(Snapshot snapshot);
}
