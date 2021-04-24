package br.net.du.myequity.service;

import br.net.du.myequity.model.transaction.Transaction;
import br.net.du.myequity.persistence.TransactionRepository;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction save(@NonNull final Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Optional<Transaction> findByIdAndSnapshotId(
            @NonNull final Long transactionId, @NonNull final Long snapshotId) {
        return transactionRepository.findByIdAndSnapshotId(transactionId, snapshotId);
    }
}
