package br.net.du.myequity.controller.accountsnapshot;

import br.net.du.myequity.controller.viewmodel.AccountSnapshotUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.InvestmentViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
public class InvestmentUpdateController extends UpdateControllerBase {

    @PostMapping("/snapshot/updateInvestmentShares")
    public AccountSnapshotViewModelOutput updateInvestmentShares(final Model model,
                                                                 @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateInvestmentSharesFunction = (jsonRequest, accountSnapshot) -> {
            final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            investmentSnapshot.setShares(newValue);

            return InvestmentViewModelOutput.of(investmentSnapshot, true);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentSharesFunction);
    }

    @PostMapping("/snapshot/updateInvestmentOriginalShareValue")
    public AccountSnapshotViewModelOutput updateInvestmentOriginalShareValue(final Model model,
                                                                             @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {

        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateInvestmentOriginalShareValueFunction = (jsonRequest, accountSnapshot) -> {
            final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            investmentSnapshot.setOriginalShareValue(newValue);

            return InvestmentViewModelOutput.of(investmentSnapshot);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentOriginalShareValueFunction);
    }

    @PostMapping("/snapshot/updateInvestmentCurrentShareValue")
    public AccountSnapshotViewModelOutput updateInvestmentCurrentShareValue(final Model model,
                                                                            @RequestBody final AccountSnapshotUpdateJsonRequest accountSnapshotUpdateJsonRequest) {
        final BiFunction<AccountSnapshotUpdateJsonRequest, AccountSnapshot, AccountSnapshotViewModelOutput>
                updateInvestmentCurrentShareValueFunction = (jsonRequest, accountSnapshot) -> {
            final InvestmentSnapshot investmentSnapshot = (InvestmentSnapshot) accountSnapshot;

            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
            investmentSnapshot.setCurrentShareValue(newValue);

            return InvestmentViewModelOutput.of(investmentSnapshot, true);
        };

        return updateAccountSnapshotField(model,
                                          accountSnapshotUpdateJsonRequest,
                                          InvestmentSnapshot.class,
                                          updateInvestmentCurrentShareValueFunction);
    }
}
