package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.AzureManagementExceptionHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {InstitutionController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        InstitutionController.class,
        RestExceptionsHandler.class,
        AzureManagementExceptionHandler.class,
        WebTestConfig.class
})
class InstitutionControllerTest {
    private static final String BASE_URL = "/institutions";

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiManagementService apiManagementServiceMock;
    
    @MockBean
    private ExternalApiService externalApiServiceMock;

    @Autowired
    protected MockMvc mvc;

    @Test
    void getInstitutionApiKeys() throws Exception {
        //given
        String institutionId = "institutionId";
        InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        when(apiManagementServiceMock.getInstitutionApiKeys(anyString()))
                .thenReturn(apiKeys);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{institutionId}/api-keys", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryKey", notNullValue()))
                .andExpect(jsonPath("$.secondaryKey", notNullValue()));
        //then
        verify(apiManagementServiceMock, times(1))
                .getInstitutionApiKeys(institutionId);
        verifyNoMoreInteractions(apiManagementServiceMock);
    }

    @Test
    void createInstitutionApyKeys() throws Exception {
        //given
        String institutionId = "institutionId";
        InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        when(apiManagementServiceMock.createInstitutionKeys(anyString()))
                .thenReturn(apiKeys);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{institutionId}/api-keys", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.primaryKey", notNullValue()))
                .andExpect(jsonPath("$.secondaryKey", notNullValue()));
        //then
        verify(apiManagementServiceMock, times(1))
                .createInstitutionKeys(institutionId);
        verifyNoMoreInteractions(apiManagementServiceMock);
    }
    
    @Test
    void regeneratePrimaryKey() throws Exception {
        // given
        String institutionId = "institutionId";
        doNothing().when(apiManagementServiceMock)
                .regeneratePrimaryKey(any());
        // when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{institutionId}/api-keys/primary/regenerate", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
        // then
        verify(apiManagementServiceMock, times(1))
                .regeneratePrimaryKey(institutionId);
        verifyNoMoreInteractions(apiManagementServiceMock);
    }
    
    @Test
    void regenerateSecondaryKey() throws Exception {
        // given
        String institutionId = "institutionId";
        doNothing().when(apiManagementServiceMock)
                .regenerateSecondaryKey(any());
        // when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{institutionId}/api-keys/secondary/regenerate", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
        // then
        verify(apiManagementServiceMock, times(1))
                .regenerateSecondaryKey(institutionId);
        verifyNoMoreInteractions(apiManagementServiceMock);
    }
}
