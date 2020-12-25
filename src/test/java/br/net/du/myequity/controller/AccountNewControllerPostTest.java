package br.net.du.myequity.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AccountNewControllerPostTest extends PostControllerTestBase {

    public AccountNewControllerPostTest() {
        super("/newaccount");
    }
}
