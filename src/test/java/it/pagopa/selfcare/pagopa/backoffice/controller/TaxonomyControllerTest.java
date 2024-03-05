package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.TaxonomyGroups;
import it.pagopa.selfcare.pagopa.backoffice.service.TaxonomyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class TaxonomyControllerTest {

    @MockBean
    private TaxonomyService taxonomyService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(taxonomyService);
    }

    @Test
    void getTaxonomyGroupsShouldReturnOK() throws Exception {
        when(taxonomyService.getTaxonomyGroups())
                .thenReturn(new TaxonomyGroups());
        mvc.perform(get("/taxonomies/groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(taxonomyService).getTaxonomyGroups();
    }

    @Test
    void getTaxonomyGroupsShouldReturnKO() throws Exception {
        when(taxonomyService.getTaxonomyGroups())
                .then(invocationOnMock -> {
                    throw new Exception();
                });
        mvc.perform(get("/taxonomies/groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(taxonomyService).getTaxonomyGroups();
    }

}