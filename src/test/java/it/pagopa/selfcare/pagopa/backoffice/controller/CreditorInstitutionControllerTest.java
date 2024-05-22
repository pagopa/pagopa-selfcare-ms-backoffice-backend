package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.AvailableCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionAndBrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionContactsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.UpdateCreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.service.CreditorInstitutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
class CreditorInstitutionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreditorInstitutionService ciService;

    @Test
    void getCreditorInstitutions() throws Exception {
        String url = "/creditor-institutions?limit=50&page=0&ci-code=12345&name=comune&sorting=ASC";
        when(ciService.getCreditorInstitutions(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(TestUtil.fileToObject("response/service/get_creditor_institutions_ok.json", CreditorInstitutionsResource.class));
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCreditorInstitutionDetails() throws Exception {
        String url = "/creditor-institutions/12345678900";
        when(ciService.getCreditorInstitutionDetails(anyString()))
                .thenReturn(TestUtil.fileToObject("response/service/get_creditor_institution_details_ok.json", CreditorInstitutionDetailsResource.class));
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getCreditorInstitutionSegregationCodes() throws Exception {
        String url = "/creditor-institutions/12345678900/segregation-codes";
        AvailableCodes response = AvailableCodes.builder().availableCodeList(Collections.singletonList("2")).build();
        when(ciService.getCreditorInstitutionSegregationCodes(anyString())).thenReturn(response);
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    void associateStationToCreditorInstitution() throws Exception {
        String url = "/creditor-institutions/12345678900/station";
        when(ciService.associateStationToCreditorInstitution(anyString(), any(CreditorInstitutionStationDto.class)))
                .thenReturn(TestUtil.fileToObject("response/service/post_creditor_institution_station_association_ok.json", CreditorInstitutionStationEditResource.class));
        mvc.perform(post(url)
                        .content(TestUtil.readJsonFromFile("request/post_creditor_institution_station_association.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void deleteCreditorInstitutionStationRelationship() throws Exception {
        String url = "/creditor-institutions/12345678900/station/00000000000_01";
        doNothing().when(ciService).deleteCreditorInstitutionStationRelationship(anyString(), anyString());
        mvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createCreditorInstitution() throws Exception {
        String url = "/creditor-institutions";
        when(ciService.createCreditorInstitution(any(CreditorInstitutionDto.class)))
                .thenReturn(TestUtil.fileToObject("response/service/post_creditor_institution_ok.json", CreditorInstitutionDetailsResource.class));
        mvc.perform(post(url)
                        .content(TestUtil.readJsonFromFile("request/post_creditor_institution.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void createCreditorInstitutionAndBroker() throws Exception {
        String url = "/creditor-institutions/12345678900/full";
        when(ciService.createCIAndBroker(any(CreditorInstitutionAndBrokerDto.class)))
                .thenReturn(TestUtil.fileToObject("response/service/post_creditor_institution_ok.json", CreditorInstitutionDetailsResource.class));
        mvc.perform(post(url)
                        .content(TestUtil.readJsonFromFile("request/post_creditor_institution.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    void updateCreditorInstitutionDetails() throws Exception {
        String url = "/creditor-institutions/12345678900g";
        when(ciService.updateCreditorInstitutionDetails(anyString(), any(UpdateCreditorInstitutionDto.class)))
                .thenReturn(TestUtil.fileToObject("response/service/post_creditor_institution_ok.json", CreditorInstitutionDetailsResource.class));
        mvc.perform(put(url)
                        .content(TestUtil.readJsonFromFile("request/post_creditor_institution.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    void getBrokerAndEcDetails() throws Exception {
        String url = "/creditor-institutions/12345678900/full";
        when(ciService.getBrokerAndEcDetails(anyString()))
                .thenReturn(TestUtil.fileToObject("response/service/get_broker_and_ec_details_ok.json", BrokerAndEcDetailsResource.class));
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    void getCreditorInstitutionContacts() throws Exception {
        String url = "/creditor-institutions/{ci-tax-code}/contacts";
        when(ciService.getCreditorInstitutionContacts(anyString(), anyString()))
                .thenReturn(new CreditorInstitutionContactsResource());

        mvc.perform(get(url, "ciTaxCode")
                        .param("institutionId", "instiutionId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}
