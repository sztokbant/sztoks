package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.Snapshot;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static br.net.du.myequity.controller.util.ControllerConstants.DECIMAL_FORMAT;

@RestController
public class SnapshotInvestmentAccountUpdateController extends SnapshotAccountUpdateControllerBase {

    private static final String PROFIT_PERCENTAGE_TEMPLATE = "%s%%";

    @PostMapping("/updateInvestmentShares")
    public SnapshotAccountUpdateJsonResponse updateInvestmentShares(final Model model,
                                                                    @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final InvestmentSnapshot investmentSnapshot = getInvestmentSnapshot(snapshotAccountUpdateJsonRequest);

        investmentSnapshot.setShares(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(investmentSnapshot);

        final String shares =
                new BigDecimal(DECIMAL_FORMAT.format(investmentSnapshot.getShares().setScale(2))).toString();
        return getDefaultResponseBuilder(snapshot, investmentSnapshot).shares(shares).build();
    }

    @PostMapping("/updateInvestmentOriginalShareValue")
    public SnapshotAccountUpdateJsonResponse updateInvestmentOriginalShareValue(final Model model,
                                                                                @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final InvestmentSnapshot investmentSnapshot = getInvestmentSnapshot(snapshotAccountUpdateJsonRequest);

        investmentSnapshot.setOriginalShareValue(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(investmentSnapshot);

        final String originalShareValue =
                new BigDecimal(DECIMAL_FORMAT.format(investmentSnapshot.getOriginalShareValue()
                                                                       .setScale(2))).toString();
        final String profitPercentage =
                new BigDecimal(DECIMAL_FORMAT.format(investmentSnapshot.getProfitPercentage().setScale(2))).toString();
        return getDefaultResponseBuilder(snapshot, investmentSnapshot).originalShareValue(originalShareValue)
                                                                      .profitPercentage(String.format(
                                                                              PROFIT_PERCENTAGE_TEMPLATE,
                                                                              profitPercentage))
                                                                      .build();
    }

    @PostMapping("/updateInvestmentCurrentShareValue")
    public SnapshotAccountUpdateJsonResponse updateInvestmentCurrentShareValue(final Model model,
                                                                               @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final Snapshot snapshot = getSnapshot(model, snapshotAccountUpdateJsonRequest);
        assert snapshot != null;

        final InvestmentSnapshot investmentSnapshot = getInvestmentSnapshot(snapshotAccountUpdateJsonRequest);

        investmentSnapshot.setCurrentShareValue(snapshotAccountUpdateJsonRequest.getNewValue());

        accountSnapshotRepository.save(investmentSnapshot);

        final String currentShareValue =
                new BigDecimal(DECIMAL_FORMAT.format(investmentSnapshot.getCurrentShareValue().setScale(2))).toString();
        final String profitPercentage =
                new BigDecimal(DECIMAL_FORMAT.format(investmentSnapshot.getProfitPercentage().setScale(2))).toString();
        return getDefaultResponseBuilder(snapshot, investmentSnapshot).currentShareValue(currentShareValue)
                                                                      .profitPercentage(String.format(
                                                                              PROFIT_PERCENTAGE_TEMPLATE,
                                                                              profitPercentage))
                                                                      .build();
    }

    private InvestmentSnapshot getInvestmentSnapshot(@RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final AccountSnapshot accountSnapshot =
                accountSnapshotRepository.findByAccountId(snapshotAccountUpdateJsonRequest.getAccountId()).get();

        if (!(accountSnapshot instanceof InvestmentSnapshot)) {
            throw new IllegalArgumentException("accountSnapshot not an instance of " + InvestmentSnapshot.class.getSimpleName());
        }

        return (InvestmentSnapshot) accountSnapshot;
    }
}
