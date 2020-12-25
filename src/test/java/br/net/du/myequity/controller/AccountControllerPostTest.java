package br.net.du.myequity.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerPostTest extends PostControllerTestBase {

    public AccountControllerPostTest() {
        super("/newaccount");
    }

    @BeforeEach
    public void setUp() {}
}
