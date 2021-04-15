package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.SnapshotSummary;
import br.net.du.myequity.model.User;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Snapshot> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Snapshot findTopByUserOrderByYearDescMonthDesc(User user);

    List<SnapshotSummary> findAllByUserOrderByYearDescMonthDesc(User user);
}
