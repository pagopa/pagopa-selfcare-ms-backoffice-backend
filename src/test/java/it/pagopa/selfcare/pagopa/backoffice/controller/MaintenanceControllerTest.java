package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.model.maintenance.MaintenanceMessage;
import it.pagopa.selfcare.pagopa.backoffice.service.MaintenanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class MaintenanceControllerTest {

    @MockBean
    private MaintenanceService maintenanceService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getMaintenanceMessage() throws Exception {
        String url = "/maintenance/message";
        when(maintenanceService.getMaintenanceMessages()).thenReturn(
                MaintenanceMessage.builder()
                        .pageMessage("pageMessage")
                        .bannerMessage("bannerMessage")
                        .build()
        );
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}