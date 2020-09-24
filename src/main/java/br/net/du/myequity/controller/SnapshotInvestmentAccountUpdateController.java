package br.net.du.myequity.controller;

import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.model.AccountSnapshotUpdateJsonResponse;
import br.net.du.myequity.controller.model.InvestmentSnapshotUpdateJsonResponse;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
public class SnapshotInvestmentAccountUpdateController extends SnapshotAccountUpdateControllerBase {

    @PostMapping("/snapshot/updateInvestmentShares")
    public AccountSnapshotUpdateJsonResponse updateInvestmentShares(final Model model,
                                                                    @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateInvestmentSharesFunction = (jsonRequest, accountSnapshot) -> {
            final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            investmentSnapshot.setShares(newValue);

            return InvestmentSnapshotUpdateJsonResponse.of(investmentSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentSharesFunction);
    }

    @PostMapping("/snapshot/updateInvestmentOriginalShareValue")
    public AccountSnapshotUpdateJsonResponse updateInvestmentOriginalShareValue(final Model model,
                                                                                @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateInvestmentOriginalShareValueFunction = (jsonRequest, accountSnapshot) -> {
            final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            investmentSnapshot.setOriginalShareValue(newValue);

            return InvestmentSnapshotUpdateJsonResponse.of(investmentSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentOriginalShareValueFunction);
    }

    @PostMapping("/snapshot/updateInvestmentCurrentShareValue")
    public AccountSnapshotUpdateJsonResponse updateInvestmentCurrentShareValue(final Model model,
                                                                               @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotUpdateJsonResponse>
                updateInvestmentCurrentShareValueFunction = (jsonRequest, accountSnapshot) -> {
            final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            investmentSnapshot.setCurrentShareValue(newValue);

            return InvestmentSnapshotUpdateJsonResponse.of(investmentSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentCurrentShareValueFunction);
    }
}
