package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Attribute;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementService;
import it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.AzureManagementExceptionHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.Subscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.everyItem;
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
        List<InstitutionApiKeys> apiKeys = mockInstance(List.of(new InstitutionApiKeys()));
        when(apiManagementServiceMock.getInstitutionApiKeys(anyString()))
                .thenReturn(apiKeys);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{institutionId}/api-keys", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].primaryKey", notNullValue()))
                .andExpect(jsonPath("$[*].secondaryKey", notNullValue()))
                .andExpect(jsonPath("$[*].displayName", notNullValue()));
        //then
        verify(apiManagementServiceMock, times(1))
                .getInstitutionApiKeys(institutionId);
        verifyNoMoreInteractions(apiManagementServiceMock);
    }

    @Test
    void createInstitutionApiKeys() throws Exception {
        //given
        String institutionId = "institutionId";
        Subscription subscriptionCode = Subscription.NODOAUTH;
        String subscriptionCodeName = subscriptionCode.name();

        List<InstitutionApiKeys> apiKeys = mockInstance(List.of(mockInstance(new InstitutionApiKeys())));

        when(apiManagementServiceMock.createSubscriptionKeys(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(apiKeys);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{institutionId}/api-keys", institutionId).queryParam("subscriptionCode", subscriptionCode.name())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[*].primaryKey", notNullValue()))
                .andExpect(jsonPath("$[*].secondaryKey", notNullValue()))
                .andExpect(jsonPath("$[*].displayName", notNullValue()));
        //then
        verify(apiManagementServiceMock, times(1))
                .createSubscriptionKeys(institutionId, subscriptionCode.getScope(), subscriptionCode.getPrefixId(), subscriptionCode.getDisplayName());
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

    @Test
    void getInstitution() throws Exception {
        //given
        String institutionId = "institutionId";
        Institution institutionMock = mockInstance(new Institution());
        Attribute attributeMock = mockInstance(new Attribute());
        institutionMock.setAttributes(List.of(attributeMock));
        when(externalApiServiceMock.getInstitution(anyString()))
                .thenReturn(institutionMock);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{institutionId}", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.externalId", notNullValue()))
                .andExpect(jsonPath("$.originId", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.digitalAddress", notNullValue()))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.zipCode", notNullValue()))
                .andExpect(jsonPath("$.taxCode", notNullValue()))
                .andExpect(jsonPath("$.origin", notNullValue()))
                .andExpect(jsonPath("$.institutionType", notNullValue()))
                .andExpect(jsonPath("$.attributes", notNullValue()))
                .andExpect(jsonPath("$.attributes[0].code", notNullValue()))
                .andExpect(jsonPath("$.attributes[0].description", notNullValue()))
                .andExpect(jsonPath("$.attributes[0].origin", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitution(institutionId);
        verifyNoMoreInteractions(externalApiServiceMock);
    }


    @Test
    void getInstitutions() throws Exception {
        //given
        InstitutionInfo institutionInfoMock = mockInstance(new InstitutionInfo());
        institutionInfoMock.setUserProductRoles(List.of("userProductRole"));

        Authentication auth = mock(Authentication.class);
        SelfCareUser user = mockInstance(new SelfCareUser("1"));
        when(auth.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(externalApiServiceMock.getInstitutions(anyString()))
                .thenReturn(List.of(institutionInfoMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", notNullValue()))
                .andExpect(jsonPath("$..externalId", notNullValue()))
                .andExpect(jsonPath("$..originId", notNullValue()))
                .andExpect(jsonPath("$..name", notNullValue()))
                .andExpect(jsonPath("$..mailAddress", notNullValue()))
                .andExpect(jsonPath("$..address", notNullValue()))
                .andExpect(jsonPath("$..zipCode", notNullValue()))
                .andExpect(jsonPath("$..taxCode", notNullValue()))
                .andExpect(jsonPath("$..origin", notNullValue()))
                .andExpect(jsonPath("$..institutionType", notNullValue()))
                .andExpect(jsonPath("$..userRole", notNullValue()))
                .andExpect(jsonPath("$..userProductRoles[0]", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitutions(anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }


    @Test
    void getInstitutions_AuthenticationNull() throws Exception {
        //given
        InstitutionInfo institutionInfoMock = mockInstance(new InstitutionInfo());
        institutionInfoMock.setUserProductRoles(List.of("userProductRole"));

        when(externalApiServiceMock.getInstitutions(anyString()))
                .thenReturn(List.of(institutionInfoMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", notNullValue()))
                .andExpect(jsonPath("$..externalId", notNullValue()))
                .andExpect(jsonPath("$..originId", notNullValue()))
                .andExpect(jsonPath("$..name", notNullValue()))
                .andExpect(jsonPath("$..mailAddress", notNullValue()))
                .andExpect(jsonPath("$..address", notNullValue()))
                .andExpect(jsonPath("$..zipCode", notNullValue()))
                .andExpect(jsonPath("$..taxCode", notNullValue()))
                .andExpect(jsonPath("$..origin", notNullValue()))
                .andExpect(jsonPath("$..institutionType", notNullValue()))
                .andExpect(jsonPath("$..userRole", notNullValue()))
                .andExpect(jsonPath("$..userProductRoles[0]", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitutions(anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }

    @Test
    void getInstitutions_UserNotSelfCareInstance() throws Exception {
        //given
        InstitutionInfo institutionInfoMock = mockInstance(new InstitutionInfo());
        institutionInfoMock.setUserProductRoles(List.of("userProductRole"));

        Authentication auth = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(externalApiServiceMock.getInstitutions(anyString()))
                .thenReturn(List.of(institutionInfoMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", notNullValue()))
                .andExpect(jsonPath("$..externalId", notNullValue()))
                .andExpect(jsonPath("$..originId", notNullValue()))
                .andExpect(jsonPath("$..name", notNullValue()))
                .andExpect(jsonPath("$..mailAddress", notNullValue()))
                .andExpect(jsonPath("$..address", notNullValue()))
                .andExpect(jsonPath("$..zipCode", notNullValue()))
                .andExpect(jsonPath("$..taxCode", notNullValue()))
                .andExpect(jsonPath("$..origin", notNullValue()))
                .andExpect(jsonPath("$..institutionType", notNullValue()))
                .andExpect(jsonPath("$..userRole", notNullValue()))
                .andExpect(jsonPath("$..userProductRoles[0]", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitutions(anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }


    @Test
    void getInstitutionUserProducts() throws Exception {
        //given
        String institutionId = "institutionId";
        String userId = "userId";
        Product productMock = mockInstance(new Product());

        Authentication auth = mock(Authentication.class);
        SelfCareUser user = mockInstance(new SelfCareUser("1"));
        when(auth.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(externalApiServiceMock.getInstitutionUserProducts(anyString(), anyString()))
                .thenReturn(List.of(productMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{institutionId}/products", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", notNullValue()))
                .andExpect(jsonPath("$..title", notNullValue()))
                .andExpect(jsonPath("$..urlPublic", notNullValue()))
                .andExpect(jsonPath("$..description", notNullValue()))
                .andExpect(jsonPath("$..urlBO", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitutionUserProducts(anyString(), anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }

    @Test
    void getInstitutionUserProducts_AuthenticationNull() throws Exception {
        //given
        String institutionId = "institutionId";
        String userId = "userId";
        Product productMock = mockInstance(new Product());

        when(externalApiServiceMock.getInstitutionUserProducts(anyString(), anyString()))
                .thenReturn(List.of(productMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{institutionId}/products", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", notNullValue()))
                .andExpect(jsonPath("$..title", notNullValue()))
                .andExpect(jsonPath("$..urlPublic", notNullValue()))
                .andExpect(jsonPath("$..description", notNullValue()))
                .andExpect(jsonPath("$..urlBO", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitutionUserProducts(anyString(), anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }

    @Test
    void getInstitutionUserProducts_UserNotSelfCareInstance() throws Exception {
        //given
        String institutionId = "institutionId";
        String userId = "userId";
        Product productMock = mockInstance(new Product());

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);


        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(externalApiServiceMock.getInstitutionUserProducts(anyString(), anyString()))
                .thenReturn(List.of(productMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{institutionId}/products", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", notNullValue()))
                .andExpect(jsonPath("$..title", notNullValue()))
                .andExpect(jsonPath("$..urlPublic", notNullValue()))
                .andExpect(jsonPath("$..description", notNullValue()))
                .andExpect(jsonPath("$..urlBO", notNullValue()));
        //then
        verify(externalApiServiceMock, times(1))
                .getInstitutionUserProducts(anyString(), anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }


    @Test
    void getBrokerDelegation() throws Exception {
        //given
        String institutionId = "institutionId";
        String brokerId = "brokerId";
        Delegation delegationMock = mockInstance(new Delegation());

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);


        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(externalApiServiceMock.getBrokerDelegation(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(delegationMock));
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/delegations", institutionId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("institutionId", institutionId)
                        .queryParam("brokerId", brokerId))
                .andExpect(status().isOk())
               .andExpect(jsonPath("$[*].brokerId",everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].brokerName", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].institutionId", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].productId", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].type", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].institutionName", everyItem(notNullValue())));
        //then
        verify(externalApiServiceMock, times(1))
                .getBrokerDelegation(anyString(), anyString(), anyString(), anyString());
        verifyNoMoreInteractions(externalApiServiceMock);
    }

}

