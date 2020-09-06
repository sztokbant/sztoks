package br.net.du.myequity.controller;

import br.net.du.myequity.model.Account;
import br.net.du.myequity.model.AccountSnapshotMetadata;
import br.net.du.myequity.model.AccountType;
import br.net.du.myequity.model.AssetSnapshot;
import br.net.du.myequity.model.LiabilitySnapshot;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.User;
import br.net.du.myequity.persistence.AccountRepository;
import br.net.du.myequity.persistence.AccountSnapshotMetadataRepository;
import br.net.du.myequity.persistence.SnapshotRepository;
import lombok.Builder;
import lombok.Data;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import static br.net.du.myequity.controller.util.ControllerConstants.DECIMAL_FORMAT;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsInSnapshot;
import static br.net.du.myequity.controller.util.ControllerUtils.accountBelongsToUser;
import static br.net.du.myequity.controller.util.ControllerUtils.snapshotBelongsToUser;

@RestController
public class AccountBalanceController extends BaseController {
    @Autowired
    private SnapshotRepository snapshotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountSnapshotMetadataRepository accountSnapshotMetadataRepository;

    @PostMapping("/accountbalance")
    public AccountBalanceResponse post(@RequestBody final AccountBalanceJsonRequest accountBalanceJsonRequest) {
        final User user = getCurrentUser();

        final Optional<Snapshot> snapshotOpt = snapshotRepository.findById(accountBalanceJsonRequest.getSnapshotId());
        if (!snapshotBelongsToUser(user, snapshotOpt)) {
            // TODO Error message
            return AccountBalanceResponse.builder().hasError(true).build();
        }

        final Snapshot snapshot = snapshotOpt.get();
        final Optional<Account> accountOpt = accountRepository.findById(accountBalanceJsonRequest.getAccountId());
        if (!accountBelongsToUser(user, accountOpt) || !accountBelongsInSnapshot(snapshot, accountOpt)) {
            // TODO Error message
            return AccountBalanceResponse.builder().hasError(true).build();
        }

        final AccountSnapshotMetadata accountSnapshotMetadata =
                accountSnapshotMetadataRepository.findByAccountId(accountBalanceJsonRequest.getAccountId()).get();

        if (accountSnapshotMetadata instanceof AssetSnapshot) {
            ((AssetSnapshot) accountSnapshotMetadata).setAmount(accountBalanceJsonRequest.getBalance());
        } else {
            ((LiabilitySnapshot) accountSnapshotMetadata).setAmount(accountBalanceJsonRequest.getBalance());
        }

        accountSnapshotMetadataRepository.save(accountSnapshotMetadata);

        final CurrencyUnit currencyUnit = accountSnapshotMetadata.getAccount().getCurrencyUnit();
        final AccountType accountType = accountSnapshotMetadata.getAccount().getAccountType();

        return AccountBalanceResponse.builder()
                                     .hasError(false)
                                     .balance(DECIMAL_FORMAT.format(accountSnapshotMetadata.getTotal().setScale(2)))
                                     .currencyUnit(currencyUnit.toString())
                                     .netWorth(DECIMAL_FORMAT.format(snapshot.getNetWorth()
                                                                             .get(currencyUnit)
                                                                             .setScale(2)))
                                     .accountType(accountType.name())
                                     .totalForAccountType(DECIMAL_FORMAT.format(snapshot.getTotalForAccountType(
                                             accountType).get(currencyUnit).setScale(2)))
                                     .build();
    }

    @Data
    @Builder
    public static class AccountBalanceJsonRequest {
        private Long snapshotId;
        private Long accountId;
        private BigDecimal balance;
    }

    @Data
    @Builder
    public static class AccountBalanceResponse {
        private boolean hasError;

        private String balance;
        private String currencyUnit;
        private String netWorth;
        private String accountType;
        private String totalForAccountType;
    }
}
