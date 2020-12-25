package br.net.du.myequity.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotAddAccountControllerPostTest extends SnapshotControllerPostTestBase {

    public SnapshotAddAccountControllerPostTest() {
        super(String.format("/snapshot/addAccounts/%d", SNAPSHOT_ID));
    }
}
