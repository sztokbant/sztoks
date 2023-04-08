package br.net.du.sztoks.persistence;

import br.net.du.sztoks.model.transaction.Transaction;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

public interface TransactionRepository extends CustomRepository<Transaction, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByIdAndSnapshotId(Long id, Long snapshotId);
}
