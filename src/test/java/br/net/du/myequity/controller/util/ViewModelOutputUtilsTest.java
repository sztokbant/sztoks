package br.net.du.myequity.controller.util;

import static br.net.du.myequity.controller.util.ViewModelOutputUtils.getViewModelOutputClass;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.InvestmentViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.PayableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.ReceivableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.SimpleAssetViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.SimpleLiabilityViewModelOutput;
import org.junit.jupiter.api.Test;

public class ViewModelOutputUtilsTest {

    @Test
    public void getViewModelOutputClass_happy() throws Exception {
        assertEquals(
                CreditCardViewModelOutput.class,
                getViewModelOutputClass("CreditCard", AccountSnapshotViewModelOutput.class));

        assertEquals(
                InvestmentViewModelOutput.class,
                getViewModelOutputClass("Investment", AccountSnapshotViewModelOutput.class));

        assertEquals(
                ReceivableViewModelOutput.class,
                getViewModelOutputClass("Receivable", AccountSnapshotViewModelOutput.class));

        assertEquals(
                PayableViewModelOutput.class,
                getViewModelOutputClass("Payable", AccountSnapshotViewModelOutput.class));

        assertEquals(
                SimpleAssetViewModelOutput.class,
                getViewModelOutputClass("SimpleAsset", AccountSnapshotViewModelOutput.class));

        assertEquals(
                SimpleLiabilityViewModelOutput.class,
                getViewModelOutputClass("SimpleLiability", AccountSnapshotViewModelOutput.class));
    }
}
