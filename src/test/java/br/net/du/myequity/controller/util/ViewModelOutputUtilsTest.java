package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.util.ViewModelOutputUtils.getViewModelOutputClass;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.net.du.myequity.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.CreditCardAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.InvestmentAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.PayableAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.ReceivableAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.SimpleAssetAccountViewModelOutput;
import br.net.du.myequity.controller.viewmodel.account.SimpleLiabilityAccountViewModelOutput;
import org.junit.jupiter.api.Test;

public class ViewModelOutputUtilsTest {

    @Test
    public void getViewModelOutputClass_happy() throws Exception {
        assertEquals(
                CreditCardAccountViewModelOutput.class,
                getViewModelOutputClass("CreditCardAccount", AccountViewModelOutput.class));

        assertEquals(
                InvestmentAccountViewModelOutput.class,
                getViewModelOutputClass("InvestmentAccount", AccountViewModelOutput.class));

        assertEquals(
                ReceivableAccountViewModelOutput.class,
                getViewModelOutputClass("ReceivableAccount", AccountViewModelOutput.class));

        assertEquals(
                PayableAccountViewModelOutput.class,
                getViewModelOutputClass("PayableAccount", AccountViewModelOutput.class));

        assertEquals(
                SimpleAssetAccountViewModelOutput.class,
                getViewModelOutputClass("SimpleAssetAccount", AccountViewModelOutput.class));

        assertEquals(
                SimpleLiabilityAccountViewModelOutput.class,
                getViewModelOutputClass("SimpleLiabilityAccount", AccountViewModelOutput.class));
    }
}
