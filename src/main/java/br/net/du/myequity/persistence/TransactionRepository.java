package br.net.du.myequity.persistence;

import br.net.du.myequity.model.transaction.Transaction;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Transaction> findByIdAndSnapshotId(Long id, Long snapshotId);
}
