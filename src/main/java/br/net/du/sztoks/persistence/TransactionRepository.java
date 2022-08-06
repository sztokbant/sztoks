package br.net.du.sztoks.persistence;

import br.net.du.sztoks.model.transaction.Transaction;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface TransactionRepository extends CustomRepository<Transaction, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByIdAndSnapshotId(Long id, Long snapshotId);
}
