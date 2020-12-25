package br.net.du.myequity.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AccountSnapshotControllerPostTest extends AccountSnapshotControllerPostTestBase {

    public AccountSnapshotControllerPostTest() {
        super(String.format("/snapshot/addAccounts/%d", SNAPSHOT_ID));
    }
}
