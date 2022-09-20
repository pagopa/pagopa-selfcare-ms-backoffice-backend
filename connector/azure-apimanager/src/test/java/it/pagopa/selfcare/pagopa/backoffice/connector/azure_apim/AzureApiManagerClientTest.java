package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim;

import com.azure.core.util.Context;
import com.azure.resourcemanager.apimanagement.ApiManagementManager;
import com.azure.resourcemanager.apimanagement.implementation.SubscriptionsImpl;
import com.azure.resourcemanager.apimanagement.implementation.UserContractImpl;
import com.azure.resourcemanager.apimanagement.implementation.UsersImpl;
import com.azure.resourcemanager.apimanagement.models.Confirmation;
import com.azure.resourcemanager.apimanagement.models.SubscriptionContract;
import com.azure.resourcemanager.apimanagement.models.SubscriptionCreateParameters;
import com.azure.resourcemanager.apimanagement.models.UserContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model.DummyKeyContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model.DummyUserContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AzureApiManagerClientTest {
    private final String institutionId = "institutionId";
    private final String institutionName = "institutionName";
    private final String serviceName = "serviceName";
    private final String resourceGroup = "resourceGroup";
    private final String subscriptionId = "subscriptionId";
    private final String tenantId = "tenantId";

    @Test
    void createInstitutionSubscription_ok() throws NoSuchFieldException, IllegalAccessException {
        //given

        SubscriptionsImpl subscriptionMock = mock(SubscriptionsImpl.class);

        AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);
        SubscriptionContract contractMock = mock(SubscriptionContract.class);
        DummyKeyContract keyContract = mockInstance(new DummyKeyContract());

        ApiManagementManager managerMock = mock(ApiManagementManager.class);

        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        when(managerMock.subscriptions().createOrUpdate(any(), any(), any(), any()))
                .thenReturn(contractMock);
        when(managerMock.subscriptions().listSecretsWithResponse(any(), any(), any(), any()))
                .thenReturn(keyContract);
        //when
        InstitutionApiKeys apiKeys = managerClient.createInstitutionSubscription(institutionId, institutionName);
        //then
        assertNotNull(apiKeys);
        assertNotNull(apiKeys.getPrimaryKey());
        assertNotNull(apiKeys.getSecondaryKey());
        assertEquals(keyContract.getValue().primaryKey(), apiKeys.getPrimaryKey());
        assertEquals(keyContract.getValue().secondaryKey(), apiKeys.getSecondaryKey());
        verify(managerMock, times(4))
                .subscriptions();
        ArgumentCaptor<SubscriptionCreateParameters> parametersArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreateParameters.class);
        verify(subscriptionMock, times(1))
                .createOrUpdate(eq(resourceGroup), eq(serviceName), eq(institutionId), parametersArgumentCaptor.capture());
        SubscriptionCreateParameters capturedParameters = parametersArgumentCaptor.getValue();
        assertEquals(String.format("/users/%s", institutionId), capturedParameters.ownerId());
        assertEquals(institutionName, capturedParameters.displayName());
        assertEquals("/apis", capturedParameters.scope());
        verify(subscriptionMock, times(1))
                .listSecretsWithResponse(resourceGroup, serviceName, institutionId, Context.NONE);
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
        final DummyKeyContract keyContract = mockInstance(new DummyKeyContract());
        final AzureApiManagerClient managerClient = new AzureApiManagerClient(serviceName,
                resourceGroup,
                subscriptionId,
                tenantId);

        final ApiManagementManager managerMock = mock(ApiManagementManager.class);

        mockAzureApiManagement(managerClient, managerMock);

        when(managerMock.subscriptions())
                .thenReturn(subscriptionMock);
        when(managerMock.subscriptions().listSecretsWithResponse(any(), any(), any(), any()))
                .thenReturn(keyContract);
        //when
        InstitutionApiKeys apiKeys = managerClient.getInstitutionApiKeys(institutionId);
        //then
        assertNotNull(apiKeys);
        assertNotNull(apiKeys.getPrimaryKey());
        assertNotNull(apiKeys.getSecondaryKey());
        assertEquals(keyContract.getValue().primaryKey(), apiKeys.getPrimaryKey());
        assertEquals(keyContract.getValue().secondaryKey(), apiKeys.getSecondaryKey());
        verify(managerMock, times(2))
                .subscriptions();
        verify(subscriptionMock, times(1))
                .listSecretsWithResponse(resourceGroup, serviceName, institutionId, Context.NONE);
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
                .withExistingService(any(),any())
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
                .withLastName(dto.getDescription()),times(1))
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

}
