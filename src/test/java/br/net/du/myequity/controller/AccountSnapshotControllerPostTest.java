package br.net.du.myequity.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AccountSnapshotControllerPostTest extends PostControllerTestBase {

    public AccountSnapshotControllerPostTest() {
        super("/snapshot/addAccounts/42");
    }

    @BeforeEach
    public void setUp() {}
}
