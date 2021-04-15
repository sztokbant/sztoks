package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.Account;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findByIdAndSnapshotId(Long accountId, Long snapshotId);

    List<Account> findBySnapshot(Snapshot snapshot);
}
