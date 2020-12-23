package br.net.du.myequity.controller;

import static br.net.du.myequity.controller.SnapshotController.getViewModelOutputClass;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.net.du.myequity.controller.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.InvestmentViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.PayableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.ReceivableViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.SimpleAssetViewModelOutput;
import br.net.du.myequity.controller.viewmodel.accountsnapshot.SimpleLiabilityViewModelOutput;
import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import br.net.du.myequity.model.snapshot.ReceivableSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import org.junit.jupiter.api.Test;

public class SnapshotControllerTest {

    @Test
    public void getViewModelOutputClass_happy() throws Exception {
        assertEquals(
                CreditCardViewModelOutput.class, getViewModelOutputClass(CreditCardSnapshot.class));

        assertEquals(
                InvestmentViewModelOutput.class, getViewModelOutputClass(InvestmentSnapshot.class));

        assertEquals(
                ReceivableViewModelOutput.class, getViewModelOutputClass(ReceivableSnapshot.class));

        assertEquals(PayableViewModelOutput.class, getViewModelOutputClass(PayableSnapshot.class));

        assertEquals(
                SimpleAssetViewModelOutput.class,
                getViewModelOutputClass(SimpleAssetSnapshot.class));

        assertEquals(
                SimpleLiabilityViewModelOutput.class,
                getViewModelOutputClass(SimpleLiabilitySnapshot.class));
    }
}
