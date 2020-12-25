package br.net.du.myequity.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AccountSnapshotControllerGetTest extends GetControllerTestBase {

    public AccountSnapshotControllerGetTest() {
        super("/snapshot/addAccounts/42");
    }

    @BeforeEach
    public void setUp() {}
}
