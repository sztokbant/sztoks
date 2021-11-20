package br.net.du.myequity.controller.account;

import br.net.du.myequity.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.myequity.controller.viewmodel.account.GiftCertificateAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.model.account.Account;
import br.net.du.myequity.model.account.GiftCertificateAccount;
import br.net.du.myequity.model.account.InvestmentAccount;
import br.net.du.myequity.model.account.SharesUpdatable;
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
    public Object updateAccountShares(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object> updateAccountSharesFunction =
                (jsonRequest, account) -> {
                    final SharesUpdatable sharesUpdatable = (SharesUpdatable) account;

                    final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                    sharesUpdatable.setShares(newValue);

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
                SharesUpdatable.class,
                updateAccountSharesFunction,
                true);
    }

    @PostMapping("/snapshot/updateInvestmentAmountInvested")
    public Object updateInvestmentAmountInvested(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {

        final BiFunction<ValueUpdateJsonRequest, Account, Object>
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
    public Object updateAccountCurrentShareValue(
            final Model model, @RequestBody final ValueUpdateJsonRequest valueUpdateJsonRequest) {
        final BiFunction<ValueUpdateJsonRequest, Account, Object>
                updateAccountCurrentShareValueFunction =
                        (jsonRequest, account) -> {
                            final SharesUpdatable sharesUpdatable = (SharesUpdatable) account;

                            final BigDecimal newValue = new BigDecimal(jsonRequest.getNewValue());
                            sharesUpdatable.setCurrentShareValue(newValue);

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
                SharesUpdatable.class,
                updateAccountCurrentShareValueFunction,
                true);
    }
}
