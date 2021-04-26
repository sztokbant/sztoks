package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.GiftCertificateAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.GiftCertificateAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.account.SharesUpdateable;
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

    @PostMapping("/snapshot/updateAccountShares")
    public AccountViewModelOutput updateAccountShares(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAccountSharesFunction =
                        (jsonRequest, account) -> {
                            final SharesUpdateable sharesUpdateable = (SharesUpdateable) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            sharesUpdateable.setShares(newValue);

                            if (account instanceof InvestmentAccount) {
                                return InvestmentAccountViewModelOutput.of(account, true);
                            } else if (account instanceof GiftCertificateAccount) {
                                return GiftCertificateAccountViewModelOutput.of(account, true);
                            } else {
                                throw new IllegalStateException("Unknown account type");
                            }
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharesUpdateable.class,
                updateAccountSharesFunction,
                true);
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
                updateInvestmentAmountInvestedFunction,
                false);
    }

    @PostMapping("/snapshot/updateAccountCurrentShareValue")
    public AccountViewModelOutput updateAccountCurrentShareValue(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, AccountViewModelOutput>
                updateAccountCurrentShareValueFunction =
                        (jsonRequest, account) -> {
                            final SharesUpdateable sharesUpdateable = (SharesUpdateable) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            sharesUpdateable.setCurrentShareValue(newValue);

                            if (account instanceof InvestmentAccount) {
                                return InvestmentAccountViewModelOutput.of(account, true);
                            } else if (account instanceof GiftCertificateAccount) {
                                return GiftCertificateAccountViewModelOutput.of(account, true);
                            } else {
                                throw new IllegalStateException("Unknown account type");
                            }
                        };

        return accountUpdater.updateField(
                model,
                valueUpdateJsonRequest,
                SharesUpdateable.class,
                updateAccountCurrentShareValueFunction,
                true);
    }
}
