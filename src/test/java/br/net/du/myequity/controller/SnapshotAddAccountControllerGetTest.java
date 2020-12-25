package br.net.du.myequity.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotAddAccountControllerGetTest extends SnapshotControllerGetTestBase {

    public SnapshotAddAccountControllerGetTest() {
        super(String.format("/snapshot/addAccounts/%d", SNAPSHOT_ID));
    }
}
