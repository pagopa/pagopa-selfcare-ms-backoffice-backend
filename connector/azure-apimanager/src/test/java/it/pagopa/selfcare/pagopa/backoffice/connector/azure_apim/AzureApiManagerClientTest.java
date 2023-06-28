package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim;


import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponse;
import com.azure.core.http.rest.PagedResponseBase;
import com.azure.core.management.exception.ManagementException;
import com.azure.resourcemanager.apimanagement.ApiManagementManager;
import com.azure.resourcemanager.apimanagement.implementation.SubscriptionsImpl;
import com.azure.resourcemanager.apimanagement.implementation.UserContractImpl;
import com.azure.resourcemanager.apimanagement.implementation.UserSubscriptionsImpl;
import com.azure.resourcemanager.apimanagement.implementation.UsersImpl;
import com.azure.resourcemanager.apimanagement.models.Confirmation;
import com.azure.resourcemanager.apimanagement.models.SubscriptionContract;
import com.azure.resourcemanager.apimanagement.models.SubscriptionCreateParameters;
import com.azure.resourcemanager.apimanagement.models.UserContract;
import it.pagopa.selfcare.pagopa.TestUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model.DummyKeyContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model.DummySubscriptionContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model.DummyUserContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AzureApiManagerClientTest {

    private final HttpHeaders httpHeaders = new HttpHeaders().set("header1", "value1").set("header2", "value2");
    private final HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "http://localhost");
    private final String deserializedHeaders = "header1,value1,header2,value2";

    private List<PagedResponse<Integer>> pagedResponses;
    private List<PagedResponse<SubscriptionContract>> pagedStringResponses;
    private final String institutionId = "institutionId";
    private final String institutionName = "institutionName";
    private final String scope = "scope";
    private final String serviceName = "serviceName";
    private final String resourceGroup = "resourceGroup";
    private final String subscriptionId = "subscriptionId";
    private final String tenantId = "tenantId";

    @Test
    void createInstitutionSubscription_ok() throws NoSuchFieldException, IllegalAccessException {
        //given

        SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);////////////////////////////
        UserSubscriptionsImpl userSubscriptionsMock = mock(UserSubscriptionsImpl.class);
        AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);
        SubscriptionContract contractMock = mock(SubscriptionContract.class);
        DummyKeyContract keyContract = mockInstance(new DummyKeyContract());
        PagedIterable<SubscriptionContract> subscriptionContractListMock = mockInstance(getPi(1));

        InstitutionApiKeys apiKeysMock = TestUtils.mockInstance(new InstitutionApiKeys(), "sePrimaryKey", "setSecondaryKey", "setDisplayName");
        apiKeysMock.setPrimaryKey("primaryKey");
        apiKeysMock.setSecondaryKey("secondaryKey");
        apiKeysMock.setDisplayName("displayName");
        apiKeysMock.setId("id");

        List<InstitutionApiKeys> apiKeysMockList = mockInstance(List.of(apiKeysMock));
        ApiManagementManager managerMock = mock(ApiManagementManager.class);
        AzureApiManagerClient apiManagerClient = mock(AzureApiManagerClient.class);
        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        when(managerMock.subscriptions().createOrUpdate(any(), any(), any(), any()))
                .thenReturn(contractMock);
        when(apiManagerClient.getApiSubscriptions(any()))
                .thenReturn(apiKeysMockList);
        when(managerMock.userSubscriptions())
                .thenReturn(userSubscriptionsMock);
        when(managerMock.userSubscriptions().list(any(), any(), any()))
                .thenReturn(subscriptionContractListMock);
        when(managerMock.subscriptions().listSecretsWithResponse(any(), any(), any(), any()))
                .thenReturn(keyContract);
        //when
        List<InstitutionApiKeys> apiKeys = managerClient.createInstitutionSubscription(institutionId, institutionName);
        //then
        assertNotNull(apiKeys);
        assertNotNull(apiKeys.get(0).getPrimaryKey());
        assertNotNull(apiKeys.get(0).getSecondaryKey());
        assertNotNull(apiKeys.get(0).getDisplayName());
        assertNotNull(apiKeys.get(0).getId());
        assertEquals(keyContract.getValue().primaryKey(), apiKeys.get(0).getPrimaryKey());
        assertEquals(keyContract.getValue().secondaryKey(), apiKeys.get(0).getSecondaryKey());
        verify(managerMock, times(6))
                .subscriptions();
        ArgumentCaptor<SubscriptionCreateParameters> parametersArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreateParameters.class);
        verify(subscriptionMock, times(1))
                .createOrUpdate(eq(resourceGroup), eq(serviceName), eq(institutionId), parametersArgumentCaptor.capture());
        SubscriptionCreateParameters capturedParameters = parametersArgumentCaptor.getValue();
        assertEquals(String.format("/users/%s", institutionId), capturedParameters.ownerId());
        assertEquals(institutionName, capturedParameters.displayName());
        assertEquals("/apis", capturedParameters.scope());
        verify(subscriptionMock, times(3))
                .listSecretsWithResponse(any(), any(), any(), any());
    }


    @Test
    void createInstitutionSubscription2_ok() throws NoSuchFieldException, IllegalAccessException {
        //given

        SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);////////////////////////////
        UserSubscriptionsImpl userSubscriptionsMock = mock(UserSubscriptionsImpl.class);
        AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);
        SubscriptionContract contractMock = mock(SubscriptionContract.class);

        PagedIterable<SubscriptionContract> subscriptionContractListMock = mockInstance(getPi(1));

        InstitutionApiKeys apiKeysMock = TestUtils.mockInstance(new InstitutionApiKeys(), "sePrimaryKey", "setSecondaryKey", "setDisplayName");
        apiKeysMock.setPrimaryKey("primaryKey");
        apiKeysMock.setSecondaryKey("secondaryKey");
        apiKeysMock.setDisplayName("displayName");
        apiKeysMock.setId("id");

        List<InstitutionApiKeys> apiKeysMockList = mockInstance(List.of(apiKeysMock));
        ApiManagementManager managerMock = mock(ApiManagementManager.class);
        AzureApiManagerClient apiManagerClient = mock(AzureApiManagerClient.class);
        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        when(managerMock.subscriptions().createOrUpdate(any(), any(), any(), any()))
                .thenReturn(contractMock);
        when(apiManagerClient.getApiSubscriptions(any()))
                .thenReturn(apiKeysMockList);
        when(managerMock.userSubscriptions())
                .thenReturn(userSubscriptionsMock);
        when(managerMock.userSubscriptions().list(any(), any(), any()))
                .thenReturn(subscriptionContractListMock);

        //when
        managerClient.createInstitutionSubscription(institutionId, institutionName, scope, subscriptionId, serviceName);
        //then

        verify(managerMock, times(2))
                .subscriptions();

    }


    @Test
    void regeneratePrimaryKey_ok() throws NoSuchFieldException, IllegalAccessException {
        //given
        final SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);

        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);

        final ApiManagementManager managerMock = mock(ApiManagementManager.class);

        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        doNothing()
                .when(subscriptionMock)
                .regeneratePrimaryKey(any(), any(), any());
        //when
        Executable executable = () -> managerClient.regeneratePrimaryKey(institutionId);
        //then
        assertDoesNotThrow(executable);
        verify(managerMock, times(1))
                .subscriptions();
        verify(subscriptionMock, times(1))
                .regeneratePrimaryKey(resourceGroup, serviceName, institutionId);
        verifyNoMoreInteractions(managerMock);
    }

    @Test
    void regenerateSecondaryKey_ok() throws NoSuchFieldException, IllegalAccessException {
        //given
        final SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);

        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);

        final ApiManagementManager managerMock = mock(ApiManagementManager.class);

        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        doNothing()
                .when(subscriptionMock)
                .regenerateSecondaryKey(any(), any(), any());
        //when
        Executable executable = () -> managerClient.regenerateSecondaryKey(institutionId);
        //then
        assertDoesNotThrow(executable);
        verify(managerMock, times(1))
                .subscriptions();
        verify(subscriptionMock, times(1))
                .regenerateSecondaryKey(resourceGroup, serviceName, institutionId);
        verifyNoMoreInteractions(managerMock);
    }

    @Test
    void getInstitutionApiKeys() throws NoSuchFieldException, IllegalAccessException {
        //given
        final SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);
        final UserSubscriptionsImpl userSubscriptionsImpl = mock(UserSubscriptionsImpl.class);
        final DummyKeyContract keyContract = mockInstance(new DummyKeyContract());
        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);

        final ApiManagementManager managerMock = mock(ApiManagementManager.class);

        mockAzureApiManagement(managerClient, managerMock);
        PagedIterable<SubscriptionContract> subscriptionContractListMock = mockInstance(getPi(1));
        InstitutionApiKeys apiKeysMock = TestUtils.mockInstance(new InstitutionApiKeys(), "sePrimaryKey", "setSecondaryKey", "setDisplayName");
        apiKeysMock.setPrimaryKey("primaryKey");
        apiKeysMock.setSecondaryKey("secondaryKey");
        apiKeysMock.setDisplayName("displayName");
        apiKeysMock.setId("id");

        List<InstitutionApiKeys> apiKeysMockList = mockInstance(List.of(apiKeysMock));
        AzureApiManagerClient apiManagerClient = mock(AzureApiManagerClient.class);

        when(managerMock.userSubscriptions())
                .thenReturn(userSubscriptionsImpl);

        when(userSubscriptionsImpl.list(any(), any(), any()))
                .thenReturn(subscriptionContractListMock);
        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        when(managerMock.subscriptions().listSecretsWithResponse(any(), any(), any(), any()))
                .thenReturn(keyContract);

        //when
        List<InstitutionApiKeys> apiKeys = managerClient.getInstitutionApiKeys(institutionId);

        //then
        assertNotNull(apiKeys);
        assertNotNull(apiKeys.get(0).getPrimaryKey());
        assertNotNull(apiKeys.get(0).getSecondaryKey());
        assertNotNull(apiKeys.get(0).getDisplayName());
        assertNotNull(apiKeys.get(0).getId());
        assertEquals(keyContract.getValue().primaryKey(), apiKeys.get(0).getPrimaryKey());
        assertEquals(keyContract.getValue().secondaryKey(), apiKeys.get(0).getSecondaryKey());
        verify(managerMock, times(1))
                .userSubscriptions();
        verify(userSubscriptionsImpl, times(1))
                .list(any(), any(), any());
        verify(managerMock, times(4))
                .subscriptions();
        verify(subscriptionMock, times(3))
                .listSecretsWithResponse(any(), any(), any(), any());
        verifyNoMoreInteractions(managerMock, subscriptionMock);
    }

    @Test
    void createInstitution() throws NoSuchFieldException, IllegalAccessException {
        //given
        final CreateInstitutionApiKeyDto dto = mockInstance(new CreateInstitutionApiKeyDto());
        final UsersImpl usersImplMock = mock(UsersImpl.class);
        final UserContractImpl userContractImplMock = mock(UserContractImpl.class, Answers.RETURNS_DEEP_STUBS);
        final UserContract contractMock = mockInstance(new DummyUserContract());
        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);
        final ApiManagementManager managerMock = mock(ApiManagementManager.class);

        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.users())
                .thenReturn(usersImplMock);

        when(managerMock
                .users())
                .thenReturn(usersImplMock);
        when(usersImplMock.define(any()))
                .thenReturn(userContractImplMock);

        when(userContractImplMock
                .withExistingService(any(), any())
                .withEmail(any())
                .withFirstName(any())
                .withLastName(any())
                .withConfirmation(any()))
                .thenReturn(userContractImplMock);
        when(userContractImplMock.create())
                .thenReturn(contractMock);

        //when
        it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract contract = managerClient.createInstitution(institutionId, dto);
        //then
        assertNotNull(contract);
        assertEquals(contractMock.email(), contract.getEmail());
        assertEquals(contractMock.name(), contract.getFullName());
        assertEquals(contractMock.lastName(), contract.getName());
        assertEquals(contractMock.firstName(), contract.getTaxCode());
        verify(managerMock, times(1))
                .users();
        verify(usersImplMock, times(1))
                .define(institutionId);
        verify(userContractImplMock, times(1))
                .withExistingService(resourceGroup, serviceName);
        verify(userContractImplMock
                .withExistingService(resourceGroup, serviceName), times(1))
                .withEmail(dto.getEmail());
        verify(userContractImplMock
                .withExistingService(resourceGroup, serviceName)
                .withEmail(dto.getEmail()), times(1))
                .withFirstName(dto.getFiscalCode());
        verify(userContractImplMock
                .withExistingService(resourceGroup, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getFiscalCode()), times(1))
                .withLastName(dto.getDescription());
        verify(userContractImplMock
                .withExistingService(resourceGroup, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getFiscalCode())
                .withLastName(dto.getDescription()), times(1))
                .withConfirmation(Confirmation.SIGNUP);
        verify(userContractImplMock
                .withExistingService(resourceGroup, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getFiscalCode())
                .withLastName(dto.getDescription())
                .withConfirmation(Confirmation.SIGNUP), times(1))
                .create();
        verifyNoMoreInteractions(managerMock, usersImplMock);

    }

    private void mockAzureApiManagement(AzureApiManagerClient client, ApiManagementManager manager) throws NoSuchFieldException, IllegalAccessException {
        Field field = AzureApiManagerClient.class.getDeclaredField("manager");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(client, manager);
    }

    private PagedIterable<SubscriptionContract> getPi(int numberOfPages) {
        PagedFlux<SubscriptionContract> pagedFlux = getIntegerPagedFlux(numberOfPages);
        return new PagedIterable<>(pagedFlux);
    }


    private PagedFlux<SubscriptionContract> getIntegerPagedFlux(int numberOfPages) {
        createPagedResponse(numberOfPages);

        return new PagedFlux<SubscriptionContract>(() -> pagedStringResponses.isEmpty() ? Mono.empty() : Mono.just(pagedStringResponses.get(0)),
                continuationToken -> getNextPage(continuationToken, pagedStringResponses));
    }

    private Mono<PagedResponse<SubscriptionContract>> getNextPage(String continuationToken,
                                                                  List<PagedResponse<SubscriptionContract>> pagedResponses) {

        int parsedToken = Integer.parseInt(continuationToken);
        if (parsedToken >= pagedResponses.size()) {
            return Mono.empty();
        }
        return Mono.just(pagedResponses.get(parsedToken));
    }


    private void createPagedResponse(int numberOfPages) {

        pagedStringResponses = IntStream.range(0, numberOfPages)
                .boxed()
                .map(i -> createPagedResponse(httpRequest, httpHeaders, deserializedHeaders, numberOfPages,
                        this::getStringItems, i))
                .collect(Collectors.toList());
    }

    private <T> PagedResponseBase<String, T> createPagedResponse(HttpRequest httpRequest, HttpHeaders headers,
                                                                 String deserializedHeaders, int numberOfPages, Function<Integer, List<T>> valueSupplier, int i) {
        return new PagedResponseBase<>(httpRequest, 200, headers, valueSupplier.apply(i),
                (i < numberOfPages - 1) ? String.valueOf(i + 1) : null,
                deserializedHeaders);
    }

    private List<SubscriptionContract> getStringItems(int i) {
        DummySubscriptionContract dummySubscriptionContract1 = mockInstance(new DummySubscriptionContract(), 1);
        DummySubscriptionContract dummySubscriptionContract2 = mockInstance(new DummySubscriptionContract(), 2);
        DummySubscriptionContract dummySubscriptionContract3 = mockInstance(new DummySubscriptionContract(), 3);
        return List.of(dummySubscriptionContract1, dummySubscriptionContract2, dummySubscriptionContract3);
    }

    @Test
    void getApiSubscriptions_ok() throws NoSuchFieldException, IllegalAccessException {

        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);
        final UserSubscriptionsImpl userSubscriptionMock = mock(UserSubscriptionsImpl.class);
        final SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);
        final ApiManagementManager managerMock = mock(ApiManagementManager.class);
        mockAzureApiManagement(managerClient, managerMock);
        //given
        DummyKeyContract keyContract = mockInstance(new DummyKeyContract());
        InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        List<InstitutionApiKeys> InstitutionApiKeysList = mockInstance(List.of(apiKeys));
        ;
        PagedIterable<SubscriptionContract> subscriptionContractListMock = mockInstance(getPi(1));


        when(managerMock.userSubscriptions())
                .thenReturn(userSubscriptionMock);
        when(managerMock.userSubscriptions().list(resourceGroup, serviceName, institutionId))
                .thenReturn(subscriptionContractListMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        when(managerMock.subscriptions().listSecretsWithResponse(any(), any(), any(), any()))
                .thenReturn(keyContract);

        //when
        List<InstitutionApiKeys> InstitutionApiKeysListResult = managerClient.getApiSubscriptions(institutionId);

        assertNotNull(InstitutionApiKeysList);
        assertNotNull(InstitutionApiKeysList.get(0).getPrimaryKey());
        assertNotNull(InstitutionApiKeysList.get(0).getSecondaryKey());
        assertNotNull(InstitutionApiKeysList.get(0).getDisplayName());
        assertEquals(keyContract.getValue().primaryKey(), InstitutionApiKeysListResult.get(0).getPrimaryKey());
        assertEquals(keyContract.getValue().secondaryKey(), InstitutionApiKeysListResult.get(0).getSecondaryKey());
        assertEquals(keyContract.getValue().secondaryKey(), InstitutionApiKeysListResult.get(0).getSecondaryKey());

        verify(managerMock, times(2))
                .userSubscriptions();
        verify(subscriptionMock, times(3))
                .listSecretsWithResponse(any(), any(), any(), any());
        verify(userSubscriptionMock, times(1))
                .list(any(), any(), any());

    }

    @Test
    void getApiSubscriptions_user_not_exist() throws NoSuchFieldException, IllegalAccessException {

        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);
        final UserSubscriptionsImpl userSubscriptionMock = mock(UserSubscriptionsImpl.class);
        final SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);
        final ApiManagementManager managerMock = mock(ApiManagementManager.class);
        mockAzureApiManagement(managerClient, managerMock);
        //given
        DummyKeyContract keyContract = mockInstance(new DummyKeyContract());
        //InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        List<InstitutionApiKeys> InstitutionApiKeysList = mockInstance(new ArrayList<>());
        ;
        PagedIterable<SubscriptionContract> subscriptionContractListMock = mockInstance(getPi(1));


        when(managerMock.userSubscriptions())
                .thenReturn(userSubscriptionMock);
        when(managerMock.userSubscriptions().list(resourceGroup, serviceName, institutionId))
                .thenReturn(subscriptionContractListMock);

        when(managerMock.userSubscriptions().list(resourceGroup, serviceName, institutionId).stream())
                .thenThrow(ManagementException.class);
        //when
        List<InstitutionApiKeys> InstitutionApiKeysListResult = managerClient.getApiSubscriptions(institutionId);
        assertNotNull(InstitutionApiKeysList);
        assertTrue(InstitutionApiKeysListResult.isEmpty());
        verify(managerMock, times(3))
                .userSubscriptions();
    }
}
