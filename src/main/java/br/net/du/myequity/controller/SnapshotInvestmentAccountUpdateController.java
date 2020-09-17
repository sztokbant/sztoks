package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonRequest;
import br.net.du.myequity.controller.model.SnapshotAccountUpdateJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static br.net.du.myequity.controller.util.ControllerUtils.formatAsDecimal;
import static br.net.du.myequity.controller.util.ControllerUtils.formatAsPercentage;

@RestController
public class SnapshotInvestmentAccountUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/snapshot/updateInvestmentShares")
    public SnapshotAccountUpdateJsonResponse updateInvestmentShares(final Model model,
                                                                    @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {

        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>
                updateInvestmentSharesFunction =
                new BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>() {
                    @Override
                    public SnapshotAccountUpdateJsonResponse apply(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                   final AccountSnapshot accountSnapshot) {
                        final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

                        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());
                        investmentSnapshot.setShares(newValue);

                        final String shares =
                                new BigDecimal(formatAsDecimal(investmentSnapshot.getShares())).toString();
                        return getDefaultResponseBuilder(accountSnapshot).shares(shares).build();
                    }
                };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentSharesFunction);
    }

    @PostMapping("/snapshot/updateInvestmentOriginalShareValue")
    public SnapshotAccountUpdateJsonResponse updateInvestmentOriginalShareValue(final Model model,
                                                                                @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {

        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>
                updateInvestmentOriginalShareValueFunction =
                new BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>() {
                    @Override
                    public SnapshotAccountUpdateJsonResponse apply(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                   final AccountSnapshot accountSnapshot) {
                        final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

                        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());
                        investmentSnapshot.setOriginalShareValue(newValue);

                        final String originalShareValue =
                                new BigDecimal(formatAsDecimal(investmentSnapshot.getOriginalShareValue())).toString();
                        return getDefaultResponseBuilder(investmentSnapshot).originalShareValue(originalShareValue)
                                                                            .profitPercentage(formatAsPercentage(
                                                                                    investmentSnapshot.getProfitPercentage()))
                                                                            .build();
                    }
                };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentOriginalShareValueFunction);
    }

    @PostMapping("/snapshot/updateInvestmentCurrentShareValue")
    public SnapshotAccountUpdateJsonResponse updateInvestmentCurrentShareValue(final Model model,
                                                                               @RequestBody final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest) {
        final BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>
                updateInvestmentCurrentShareValueFunction =
                new BiFunction<SnapshotAccountUpdateJsonRequest, AccountSnapshot, SnapshotAccountUpdateJsonResponse>() {
                    @Override
                    public SnapshotAccountUpdateJsonResponse apply(final SnapshotAccountUpdateJsonRequest snapshotAccountUpdateJsonRequest,
                                                                   final AccountSnapshot accountSnapshot) {
                        final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

                        final BigDecimal newValue = new BigDecimal(snapshotAccountUpdateJsonRequest.getNewValue());
                        investmentSnapshot.setCurrentShareValue(newValue);

                        final String currentShareValue =
                                new BigDecimal(formatAsDecimal(investmentSnapshot.getCurrentShareValue())).toString();
                        return getDefaultResponseBuilder(investmentSnapshot).currentShareValue(currentShareValue)
                                                                            .profitPercentage(formatAsPercentage(
                                                                                    investmentSnapshot.getProfitPercentage()))
                                                                            .build();
                    }
                };

        return updateAccountSnapshotField(model,
                                          snapshotAccountUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentCurrentShareValueFunction);
    }
}
