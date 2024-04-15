package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerDelegationPage;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerStationPage;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokersResource;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        when(brokerService.createBroker(any())).thenReturn(new BrokerResource());
        when(brokerService.getCIBrokerDelegation(anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(new CIBrokerDelegationPage());
        when(brokerService.getBrokersEC(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new BrokersResource());
        when(brokerService.getCIBrokerStations(anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(new CIBrokerStationPage());
    }

    @Test
    void createBroker() throws Exception {
        String url = "/brokers";
        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(new BrokerDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getBrokersEC() throws Exception {
        String url = "/brokers";
        mvc.perform(get(url)
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateBroker() throws Exception {
        String url = "/brokers/{broker-tax-code}";
        BrokerEcDto brokerEcDto = new BrokerEcDto();
        brokerEcDto.setBrokerCode("brokerTaxCode");

        mvc.perform(put(url, "brokerTaxCode")
                        .content(objectMapper.writeValueAsString(brokerEcDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getStationsDetailsListByBroker() throws Exception {
        String url = "/brokers/{broker-tax-code}/stations";
        mvc.perform(get(url, "brokerTaxCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getBrokerDelegation() throws Exception {
        String url = "/brokers/{broker-code}/delegations";
        mvc.perform(get(url, "brokerCode")
                        .param("brokerId", "brokerId")
                        .param("ciCode", "ciCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCIBrokerStations() throws Exception {
        String url = "/brokers/{broker-tax-code}/creditor-institutions/{ci-tax-code}/stations";
        mvc.perform(get(url, "brokerCode", "ciTaxCode")
                        .param("stationCode", "stationCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCIBroker() throws Exception {
        String url = "/brokers/{broker-tax-code}";
        mvc.perform(delete(url, "brokerCode", "ciTaxCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
