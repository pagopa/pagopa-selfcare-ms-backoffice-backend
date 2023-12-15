package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.service.IbanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class BrokerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IbanService ibanService;

    @BeforeEach
    void setUp() {
        when(ibanService.exportIbansToCsv(anyString()))
                .thenReturn(new byte[0]);

    }

    @Test
    void getBrokers() throws Exception {
        String url = "/brokers/1111/ibans/export";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }

}
