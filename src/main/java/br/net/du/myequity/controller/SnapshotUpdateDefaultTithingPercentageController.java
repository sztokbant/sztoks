package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

import br.net.du.myequity.controller.viewmodel.UpdatableTotals;
import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.account.AccountType;
import br.net.du.myequity.model.totals.AccountSubtypeDisplayGroup;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotUpdateDefaultTithingPercentageController {

    @Autowired private SnapshotUpdater snapshotUpdater;

    @PostMapping("/snapshot/updateDefaultTithingPercentage")
    @Transactional
    public Object post(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Snapshot, Object>
                updateDefaultTithingPercentageFunction =
                        (jsonRequest, snapshot) -> {
                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());

                            snapshot.setDefaultTithingPercentage(newValue);

                            final UpdatableTotals updatableTotals = new UpdatableTotals(snapshot);

                            final AccountType accountType = AccountType.LIABILITY;
                            return JsonResponse.builder()
                                    .defaultTithingPercentage(
                                            formatAsPercentage(
                                                    snapshot.getDefaultTithingPercentage()))
                                    .futureTithingBalance(updatableTotals.getFutureTithingBalance())
                                    .totalTithingBalance(
                                            updatableTotals.getTotalForAccountSubtype(
                                                    AccountSubtypeDisplayGroup.TITHING))
                                    .accountType(accountType.name())
                                    .totalForAccountType(updatableTotals.getTotalFor(accountType))
                                    .netWorth(updatableTotals.getNetWorth())
                                    .build();
                        };

        return snapshotUpdater.updateField(
                model, valueUpdateJsonRequest, updateDefaultTithingPercentageFunction);
    }

    @Data
    @Builder
    private static class JsonResponse {
        private final String defaultTithingPercentage;
        private final String futureTithingBalance;
        private final String totalTithingBalance;
        private final String accountType;
        private final String totalForAccountType;
        private final String netWorth;
    }
}
