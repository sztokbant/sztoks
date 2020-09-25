package br.net.du.myequity.controller;

import br.net.du.myequity.model.snapshot.CreditCardSnapshot;
import br.net.du.myequity.model.snapshot.InvestmentSnapshot;
import br.net.du.myequity.model.snapshot.PayableSnapshot;
import br.net.du.myequity.model.snapshot.ReceivableSnapshot;
import br.net.du.myequity.model.snapshot.SimpleAssetSnapshot;
import br.net.du.myequity.model.snapshot.SimpleLiabilitySnapshot;
import br.net.du.myequity.viewmodel.accountsnapshot.CreditCardViewModelOutput;
import br.net.du.myequity.viewmodel.accountsnapshot.InvestmentViewModelOutput;
import br.net.du.myequity.viewmodel.accountsnapshot.PayableViewModelOutput;
import br.net.du.myequity.viewmodel.accountsnapshot.ReceivableViewModelOutput;
import br.net.du.myequity.viewmodel.accountsnapshot.SimpleAssetViewModelOutput;
import br.net.du.myequity.viewmodel.accountsnapshot.SimpleLiabilityViewModelOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnapshotControllerTest {

    private SnapshotController snapshotController;

    @Test
    public void getViewModelOutputClass_happy() throws Exception {
        snapshotController = new SnapshotController();

        assertEquals(CreditCardViewModelOutput.class,
                     snapshotController.getViewModelOutputClass(CreditCardSnapshot.class));

        assertEquals(InvestmentViewModelOutput.class,
                     snapshotController.getViewModelOutputClass(InvestmentSnapshot.class));

        assertEquals(ReceivableViewModelOutput.class,
                     snapshotController.getViewModelOutputClass(ReceivableSnapshot.class));

        assertEquals(PayableViewModelOutput.class,
                     snapshotController.getViewModelOutputClass(PayableSnapshot.class));

        assertEquals(SimpleAssetViewModelOutput.class,
                     snapshotController.getViewModelOutputClass(SimpleAssetSnapshot.class));

        assertEquals(SimpleLiabilityViewModelOutput.class,
                     snapshotController.getViewModelOutputClass(SimpleLiabilitySnapshot.class));
    }
}
