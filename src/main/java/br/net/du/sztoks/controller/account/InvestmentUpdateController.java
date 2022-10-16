package br.net.du.sztoks.controller.account;

import br.net.du.sztoks.controller.viewmodel.ValueUpdateJsonRequest;
import br.net.du.sztoks.controller.viewmodel.account.GiftCertificateAccountViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.account.GiftCertificateAccount;
import br.net.du.sztoks.model.account.InvestmentAccount;
import br.net.du.sztoks.model.account.SharesUpdatable;
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
                    ((SharesUpdatable) account)
                            .setShares(new BigDecimal(jsonRequest.getNewValue()));

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
                            ((InvestmentAccount) account)
                                    .setAmountInvested(new BigDecimal(jsonRequest.getNewValue()));

                            return InvestmentAccountViewModelOutput.of(account, true);
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
                            ((SharesUpdatable) account)
                                    .setCurrentShareValue(
                                            new BigDecimal(jsonRequest.getNewValue()));

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
