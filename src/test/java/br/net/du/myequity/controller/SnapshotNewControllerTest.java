package br.net.du.myequity.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class SnapshotNewControllerTest extends PostControllerTestBase {

    public SnapshotNewControllerTest() {
        super("/snapshot/new");
    }

    @BeforeEach
    public void setUp() {}
}
