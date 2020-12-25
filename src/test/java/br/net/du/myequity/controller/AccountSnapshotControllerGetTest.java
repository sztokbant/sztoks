package br.net.du.myequity.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AccountSnapshotControllerGetTest extends AccountSnapshotControllerGetTestBase {

    public AccountSnapshotControllerGetTest() {
        super(String.format("/snapshot/addAccounts/%d", SNAPSHOT_ID));
    }
}
