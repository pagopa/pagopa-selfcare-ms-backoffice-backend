package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.MyCIResource;
import it.pagopa.selfcare.pagopa.backoffice.service.BrokerService;
import it.pagopa.selfcare.pagopa.backoffice.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
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
    private BrokerService brokerService;

    @MockBean
    private ExportService exportService;

    @BeforeEach
    void setUp() {
        when(exportService.exportIbansToCsv(anyString())).thenReturn(new byte[0]);
        when(exportService.exportCreditorInstitutionToCsv(anyString()))
                .thenReturn(new byte[0]);
        when(exportService.getBrokerExportStatus(anyString()))
                .thenReturn(BrokerECExportStatus.builder()
                        .brokerIbansLastUpdate(
                                Calendar.getInstance().toInstant())
                        .brokerInstitutionsLastUpdate(
                                Calendar.getInstance().toInstant())
                        .build());
        when(brokerService.getCIBrokerDelegation(anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(new MyCIResource()));
    }

    @Test
    void getBrokers() throws Exception {
        String url = "/brokers/1111/ibans/export";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void exportCreditorInstitutionToCsv() throws Exception {
        String url = "/brokers/1111/creditor-institutions/export";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void getBrokerExportStatus() throws Exception {
        String url = "/brokers/1111/export-status";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getBrokerDelegation() throws Exception {
        String url = "/brokers/{broker-code}/delegations";
        mvc.perform(get(url, "brokerCode")
                        .param("brokerId", "brokerId")
                        .param("ciCode", "ciCode")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
