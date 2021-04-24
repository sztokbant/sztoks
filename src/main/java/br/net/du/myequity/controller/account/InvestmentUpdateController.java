package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestmentUpdateController {

    @Autowired AccountUpdater accountUpdater;

    @PostMapping("/snapshot/updateInvestmentShares")
    public AccountViewModelOutput updateInvestmentShares(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateInvestmentSharesFunction =
                        (jsonRequest, account) -> {
                            final InvestmentAccount investmentAccount = (InvestmentAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            investmentAccount.setShares(newValue);

                            return InvestmentAccountViewModelOutput.of(investmentAccount, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentSharesFunction);
    }

    @PostMapping("/snapshot/updateInvestmentAmountInvested")
    public AccountViewModelOutput updateInvestmentAmountInvested(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateInvestmentAmountInvestedFunction =
                        (jsonRequest, account) -> {
                            final InvestmentAccount investmentAccount = (InvestmentAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            investmentAccount.setAmountInvested(newValue);

                            return InvestmentAccountViewModelOutput.of(investmentAccount, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentAmountInvestedFunction);
    }

    @PostMapping("/snapshot/updateInvestmentCurrentShareValue")
    public AccountViewModelOutput updateInvestmentCurrentShareValue(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateInvestmentCurrentShareValueFunction =
                        (jsonRequest, account) -> {
                            final InvestmentAccount investmentAccount = (InvestmentAccount) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            investmentAccount.setCurrentShareValue(newValue);

                            return InvestmentAccountViewModelOutput.of(investmentAccount, true);
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentCurrentShareValueFunction);
    }
}
