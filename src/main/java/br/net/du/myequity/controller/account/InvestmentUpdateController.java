package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestmentUpdateController extends AccountUpdateControllerBase {

    @PostMapping("/snapshot/updateInvestmentShares")
    public AccountViewModelOutput updateInvestmentShares(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateInvestmentSharesFunction =
                        (jsonRequest, account) -> {
                            final InvestmentAccount investmentSnapshot =
                                    (InvestmentAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            investmentSnapshot.setShares(newValue);

                            return InvestmentAccountViewModelOutput.of(investmentSnapshot, true);
                        };

        return updateAccountField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentSharesFunction);
    }

    @PostMapping("/snapshot/updateInvestmentOriginalShareValue")
    public AccountViewModelOutput updateInvestmentOriginalShareValue(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateInvestmentOriginalShareValueFunction =
                        (jsonRequest, account) -> {
                            final InvestmentAccount investmentSnapshot =
                                    (InvestmentAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            investmentSnapshot.setOriginalShareValue(newValue);

                            return InvestmentAccountViewModelOutput.of(investmentSnapshot);
                        };

        return updateAccountField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentOriginalShareValueFunction);
    }

    @PostMapping("/snapshot/updateInvestmentCurrentShareValue")
    public AccountViewModelOutput updateInvestmentCurrentShareValue(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateInvestmentCurrentShareValueFunction =
                        (jsonRequest, account) -> {
                            final InvestmentAccount investmentSnapshot =
                                    (InvestmentAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            investmentSnapshot.setCurrentShareValue(newValue);

                            return InvestmentAccountViewModelOutput.of(investmentSnapshot, true);
                        };

        return updateAccountField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentCurrentShareValueFunction);
    }
}
