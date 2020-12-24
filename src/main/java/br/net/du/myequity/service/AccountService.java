package br.net.du.myequity.service;

import br.net.du.myequity.exception.MyEquityException;
import br.net.du.myequity.model.User;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotRepository;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private final AccountSnapshotRepository accountSnapshotRepository;

    public Account save(@NonNull final Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findById(@NonNull final Long accountId) {
        return accountRepository.findById(accountId);
    }

    public List<Account> findByUser(@NonNull final User user) {
        return accountRepository.findByUser(user);
    }

    public void deleteAccount(@NonNull final Account account) {
        final List<AccountSnapshot> snapshots = accountSnapshotRepository.findAllByAccount(account);
        if (!snapshots.isEmpty()) {
            throw new MyEquityException(
                    "Account cannot be deleted as it is referred by at least one snapshot.");
        }
        accountRepository.delete(account);
    }
}
