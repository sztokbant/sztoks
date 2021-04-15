package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.InvestmentAccount;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestmentUpdateController extends AccountUpdateControllerBase {

    @PostMapping("/snapshot/updateInvestmentShares")
    @Transactional
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

        return updateAccountField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentSharesFunction);
    }

    @PostMapping("/snapshot/updateInvestmentAmountInvested")
    @Transactional
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

        return updateAccountField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentAmountInvestedFunction);
    }

    @PostMapping("/snapshot/updateInvestmentCurrentShareValue")
    @Transactional
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

        return updateAccountField(
                model,
                valueUpdateJsonRequest,
                InvestmentAccount.class,
                updateInvestmentCurrentShareValueFunction);
    }
}
