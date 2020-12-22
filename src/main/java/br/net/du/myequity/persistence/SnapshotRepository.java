package br.net.du.myequity.persistence;

import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO Create a Service
public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {
    List<Snapshot> findAllByUser(User user);
}
