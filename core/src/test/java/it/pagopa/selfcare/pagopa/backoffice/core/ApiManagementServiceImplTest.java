package it.pagopa.selfcare.pagopa.backoffice.core;

import com.azure.core.management.exception.ManagementException;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.backoffice.core.ApiManagementServiceImpl.AN_INSTITUTION_ID_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, SystemStubsExtension.class})
@ContextConfiguration(classes = {ApiManagementServiceImpl.class})
class ApiManagementServiceImplTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Autowired
    private ApiManagementServiceImpl apiManagementService;

    @MockBean
    private ApiManagerConnector apiManagerConnectorMock;

    @MockBean
    private ExternalApiConnector externalApiConnectorMock;

    @Test
    void createInstitutionKeys_nullInstitutionId() {
        //given
        String institutionId = null;
        //when
        Executable executable = () -> apiManagementService.createInstitutionKeys(institutionId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(AN_INSTITUTION_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }

    @Test
    void createInstitutionKeys_institutionNotFound() {
        //given
        String institutionId = "institutionId";
        //when
        Executable executable = () -> apiManagementService.createInstitutionKeys(institutionId);
        //then
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, executable);
        assertEquals(String.format("The institution %s was not found", institutionId), e.getMessage());
        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verifyNoMoreInteractions(externalApiConnectorMock);
        verifyNoInteractions(apiManagerConnectorMock);
    }


    @Test
    void createInstitutionKeys_noSubscriptionFound_dev_Uat() {
        //given
        String institutionId = "institutionId";
        String testEmail = "mail";
        ApiManagementService apiManagementServiceTest = new ApiManagementServiceImpl(testEmail, apiManagerConnectorMock, externalApiConnectorMock);

        Institution institutionMock = mockInstance(new Institution());
        institutionMock.setId(institutionId);
        InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        List<InstitutionApiKeys> apiKeysList = mockInstance(List.of(apiKeys));
        when(externalApiConnectorMock.getInstitution(any()))
                .thenReturn(institutionMock);
        when(apiManagerConnectorMock.getApiSubscriptions(institutionId))
                .thenReturn(apiKeysList);
        Mockito.doThrow(new RuntimeException()).doNothing().when(apiManagerConnectorMock)
                .createInstitutionSubscription(anyString(), anyString(), anyString(), anyString(), anyString());

        //when
        List<InstitutionApiKeys> institutionKeysList = apiManagementServiceTest.createInstitutionKeys(institutionId);

        //then
        assertNotNull(institutionKeysList);
        assertEquals(apiKeysList.get(0).getPrimaryKey(), institutionKeysList.get(0).getPrimaryKey());
        assertEquals(apiKeysList.get(0).getSecondaryKey(), institutionKeysList.get(0).getSecondaryKey());
        assertEquals(apiKeysList.get(0).getDisplayName(), institutionKeysList.get(0).getDisplayName());

        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verify(apiManagerConnectorMock, times(2))
                .createInstitutionSubscription(anyString(), anyString(), anyString(), anyString(), anyString());
        ArgumentCaptor<CreateInstitutionApiKeyDto> institutionDtoArgumentCaptor = ArgumentCaptor.forClass(CreateInstitutionApiKeyDto.class);
        verify(apiManagerConnectorMock, times(1))
                .createInstitution(eq(institutionId), institutionDtoArgumentCaptor.capture());

        verify(apiManagerConnectorMock, times(1))
                .getApiSubscriptions(institutionId);

        CreateInstitutionApiKeyDto capturedDto = institutionDtoArgumentCaptor.getValue();
        assertEquals(institutionMock.getDescription(), capturedDto.getDescription());
        assertEquals(institutionMock.getTaxCode(), capturedDto.getFiscalCode());
        assertEquals(testEmail, capturedDto.getEmail());
        verifyNoMoreInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }

    @Test
    void createInstitutionKeys_noSubscriptionFound_prod() {
        //given
        String institutionId = "institutionId";
        String testEmail = "";
        ApiManagementService apiManagementServiceTest = new ApiManagementServiceImpl(testEmail, apiManagerConnectorMock, externalApiConnectorMock);

        Institution institutionMock = mockInstance(new Institution());
        institutionMock.setId(institutionId);
        InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        List<InstitutionApiKeys> apiKeysList = mockInstance(List.of(apiKeys));
        when(externalApiConnectorMock.getInstitution(any()))
                .thenReturn(institutionMock);
        when(apiManagerConnectorMock.getApiSubscriptions(institutionId))
                .thenReturn(apiKeysList);
        Mockito.doThrow(new RuntimeException()).doNothing().when(apiManagerConnectorMock)
                .createInstitutionSubscription(anyString(), anyString(), anyString(), anyString(), anyString());

        //when
        List<InstitutionApiKeys> institutionKeysList = apiManagementServiceTest.createInstitutionKeys(institutionId);

        //then
        assertNotNull(institutionKeysList);
        assertEquals(apiKeysList.get(0).getPrimaryKey(), institutionKeysList.get(0).getPrimaryKey());
        assertEquals(apiKeysList.get(0).getSecondaryKey(), institutionKeysList.get(0).getSecondaryKey());
        assertEquals(apiKeysList.get(0).getDisplayName(), institutionKeysList.get(0).getDisplayName());

        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verify(apiManagerConnectorMock, times(2))
                .createInstitutionSubscription(anyString(), anyString(), anyString(), anyString(), anyString());
        ArgumentCaptor<CreateInstitutionApiKeyDto> institutionDtoArgumentCaptor = ArgumentCaptor.forClass(CreateInstitutionApiKeyDto.class);
        verify(apiManagerConnectorMock, times(1))
                .createInstitution(eq(institutionId), institutionDtoArgumentCaptor.capture());

        verify(apiManagerConnectorMock, times(1))
                .getApiSubscriptions(institutionId);

        CreateInstitutionApiKeyDto capturedDto = institutionDtoArgumentCaptor.getValue();
        assertEquals(institutionMock.getDescription(), capturedDto.getDescription());
        assertEquals(institutionMock.getTaxCode(), capturedDto.getFiscalCode());
        assertEquals(institutionMock.getDigitalAddress(), capturedDto.getEmail());
        verifyNoMoreInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }

    @Test
    void createInstitutionKeys() {
        //given
        String institutionId = "institutionId";
        String testEmail = "";
        ApiManagementService apiManagementServiceTest = new ApiManagementServiceImpl(testEmail, apiManagerConnectorMock, externalApiConnectorMock);

        Institution institutionMock = mockInstance(new Institution());
        institutionMock.setId(institutionId);
        InstitutionApiKeys apiKeys = mockInstance(new InstitutionApiKeys());
        List<InstitutionApiKeys> apiKeysList = mockInstance(List.of(apiKeys));
        when(externalApiConnectorMock.getInstitution(any()))
                .thenReturn(institutionMock);
        when(apiManagerConnectorMock.getApiSubscriptions(institutionId))
                .thenReturn(apiKeysList);
        doNothing().when(apiManagerConnectorMock)
                .createInstitutionSubscription(anyString(), anyString(), anyString(), anyString(), anyString());

        //when
        List<InstitutionApiKeys> institutionKeysList = apiManagementServiceTest.createInstitutionKeys(institutionId);

        //then
        assertNotNull(institutionKeysList);
        assertEquals(apiKeysList.get(0).getPrimaryKey(), institutionKeysList.get(0).getPrimaryKey());
        assertEquals(apiKeysList.get(0).getSecondaryKey(), institutionKeysList.get(0).getSecondaryKey());
        assertEquals(apiKeysList.get(0).getDisplayName(), institutionKeysList.get(0).getDisplayName());

        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verify(apiManagerConnectorMock, times(1))
                .createInstitutionSubscription(anyString(), anyString(), anyString(), anyString(), anyString());
        ArgumentCaptor<CreateInstitutionApiKeyDto> institutionDtoArgumentCaptor = ArgumentCaptor.forClass(CreateInstitutionApiKeyDto.class);
        verify(apiManagerConnectorMock, times(1))
                .getApiSubscriptions(institutionId);

        verifyNoMoreInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }


    @Test
    void createSubscriptionKeys_noSubscriptionFound() {
        //given
        String institutionId = "institutionId";
        String subscriptionId = "subscriptionId";
        String subScriptionDisplay = "subScriptionDisplay";
        String scope = "scope";

        String testEmail = "";
        ApiManagementService apiManagementServiceTest = new ApiManagementServiceImpl(testEmail, apiManagerConnectorMock, externalApiConnectorMock);

        //when
        Executable executable = () -> apiManagementServiceTest.createSubscriptionKeys(institutionId, scope, subscriptionId, subScriptionDisplay);

        //then
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, executable);
        assertEquals(String.format("The institution %s was not found", institutionId), e.getMessage());
        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verifyNoMoreInteractions(externalApiConnectorMock);
        verifyNoInteractions(apiManagerConnectorMock);

    }

    @Test
    void createSubscriptionKeys_noSubscriptionFound_dev_uat() {
        //given
        String testEmail = "testEmail";
        String institutionId = "institutionId";
        String subscriptionId = "subscriptionId";
        String subScriptionDisplay = "subScriptionDisplay";
        String scope = "scope";
        ApiManagementService apiManagementServiceTest = new ApiManagementServiceImpl(testEmail, apiManagerConnectorMock, externalApiConnectorMock);
        Institution institutionMock = mockInstance(new Institution());
        institutionMock.setId(institutionId);
        List<InstitutionApiKeys> apiKeysList = mockInstance(List.of(new InstitutionApiKeys()));
        when(externalApiConnectorMock.getInstitution(any()))
                .thenReturn(institutionMock);
        doThrow(RuntimeException.class).doNothing().when(apiManagerConnectorMock).createInstitutionSubscription(any(), any(), any(), any(), any());


        when(apiManagerConnectorMock.getApiSubscriptions(anyString()))
                .thenReturn(apiKeysList);
        //when
        List<InstitutionApiKeys> institutionKeys = apiManagementServiceTest.createSubscriptionKeys(institutionId, scope, subscriptionId, subScriptionDisplay);
        //then
        assertNotNull(institutionKeys);
        assertEquals(apiKeysList.get(0).getPrimaryKey(), institutionKeys.get(0).getPrimaryKey());
        assertEquals(apiKeysList.get(0).getSecondaryKey(), institutionKeys.get(0).getSecondaryKey());
        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verify(apiManagerConnectorMock, times(2))
                .createInstitutionSubscription(anyString(), anyString(), any(), any(), any());
        verify(apiManagerConnectorMock, times(1))
                .getApiSubscriptions(anyString());
        ArgumentCaptor<CreateInstitutionApiKeyDto> institutionDtoArgumentCaptor = ArgumentCaptor.forClass(CreateInstitutionApiKeyDto.class);
        verify(apiManagerConnectorMock, times(1))
                .createInstitution(eq(institutionId), institutionDtoArgumentCaptor.capture());
        CreateInstitutionApiKeyDto captureDto = institutionDtoArgumentCaptor.getValue();
        assertNotNull(captureDto);
        assertEquals(testEmail, captureDto.getEmail());
        assertEquals(institutionMock.getDescription(), captureDto.getDescription());
        assertEquals(institutionMock.getTaxCode(), captureDto.getFiscalCode());
        verifyNoMoreInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }

    @Test
    void createSubscriptionKeys_noSubscriptionFound_prod() {
        //given
        String testEmail = "";
        String institutionId = "institutionId";
        String subscriptionId = "subscriptionId";
        String subScriptionDisplay = "subScriptionDisplay";
        String scope = "scope";

        ApiManagementService apiManagementServiceTest = new ApiManagementServiceImpl(testEmail, apiManagerConnectorMock, externalApiConnectorMock);
        Institution institutionMock = mockInstance(new Institution());
        institutionMock.setId(institutionId);
        List<InstitutionApiKeys> apiKeysList = mockInstance(List.of(new InstitutionApiKeys()));
        when(externalApiConnectorMock.getInstitution(any()))
                .thenReturn(institutionMock);
        doThrow(RuntimeException.class).doNothing().when(apiManagerConnectorMock).createInstitutionSubscription(any(), any(), any(), any(), any());


        when(apiManagerConnectorMock.getApiSubscriptions(anyString()))
                .thenReturn(apiKeysList);
        //when
        List<InstitutionApiKeys> institutionKeys = apiManagementServiceTest.createSubscriptionKeys(institutionId, scope, subscriptionId, subScriptionDisplay);
        //then
        assertNotNull(institutionKeys);
        assertEquals(apiKeysList.get(0).getPrimaryKey(), institutionKeys.get(0).getPrimaryKey());
        assertEquals(apiKeysList.get(0).getSecondaryKey(), institutionKeys.get(0).getSecondaryKey());
        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verify(apiManagerConnectorMock, times(2))
                .createInstitutionSubscription(anyString(), anyString(), any(), any(), any());
        verify(apiManagerConnectorMock, times(1))
                .getApiSubscriptions(anyString());
        ArgumentCaptor<CreateInstitutionApiKeyDto> institutionDtoArgumentCaptor = ArgumentCaptor.forClass(CreateInstitutionApiKeyDto.class);
        verify(apiManagerConnectorMock, times(1))
                .createInstitution(eq(institutionId), institutionDtoArgumentCaptor.capture());
        CreateInstitutionApiKeyDto captureDto = institutionDtoArgumentCaptor.getValue();
        assertNotNull(captureDto);
        assertEquals(institutionMock.getDigitalAddress(), captureDto.getEmail());
        assertEquals(institutionMock.getDescription(), captureDto.getDescription());
        assertEquals(institutionMock.getTaxCode(), captureDto.getFiscalCode());
        verifyNoMoreInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }

    @Test
    void getInstitutionApiKeys_nullInstitutionId() {
        //given
        String institutionId = null;
        //when
        Executable executable = () -> apiManagementService.getInstitutionApiKeys(institutionId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(AN_INSTITUTION_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }

    @Test
    void getInstitutionApiKeys_notFound() {
        //given
        String institutionId = "institutionId";
        doThrow(ManagementException.class)
                .when(apiManagerConnectorMock)
                .getInstitutionApiKeys(any());
        //when
        Executable executable = () -> apiManagementService.getInstitutionApiKeys(institutionId);
        //then
        assertThrows(ManagementException.class, executable);
        verify(apiManagerConnectorMock, times(1))
                .getInstitutionApiKeys(institutionId);
        verifyNoInteractions(externalApiConnectorMock);
        verifyNoMoreInteractions(apiManagerConnectorMock);
    }
    
    @Test
    void getInstitutionApiKeys(){
        //given
        String institutionId = "institutionId";
        List<InstitutionApiKeys> apiKeys = mockInstance(List.of(new InstitutionApiKeys()));
        when(apiManagerConnectorMock.getInstitutionApiKeys(any()))
                .thenReturn(apiKeys);
        //when
        List<InstitutionApiKeys> institutionApiKeys = apiManagementService.getInstitutionApiKeys(institutionId);
        //then
        assertNotNull(institutionApiKeys);
        assertEquals(apiKeys, institutionApiKeys);
        verify(apiManagerConnectorMock, times(1))
                .getInstitutionApiKeys(institutionId);
        verifyNoInteractions(externalApiConnectorMock);
        verifyNoMoreInteractions(apiManagerConnectorMock);
    }
    
    @Test
    void regeneratePrimaryKey_nullInstitutionId(){
        //given
        String institutionId = null;
        //when
        Executable executable = () -> apiManagementService.regeneratePrimaryKey(institutionId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(AN_INSTITUTION_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(apiManagerConnectorMock,externalApiConnectorMock);
    }
    
    @Test
    void regeneratePrimaryKey(){
        //given
        String institutionId = "institutionId";
        //when
        Executable executable = () -> apiManagementService.regeneratePrimaryKey(institutionId);
        //then
        assertDoesNotThrow(executable);
        verify(apiManagerConnectorMock, times(1))
                .regeneratePrimaryKey(institutionId);
        verifyNoInteractions(externalApiConnectorMock);
        verifyNoMoreInteractions(apiManagerConnectorMock);
    }
    
    @Test
    void regenerateSecondaryKey_nullInstitutionId(){
        //given
        String institutionId = null;
        //when
        Executable executable = () -> apiManagementService.regenerateSecondaryKey(institutionId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(AN_INSTITUTION_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(apiManagerConnectorMock, externalApiConnectorMock);
    }
    
    @Test
    void regenerateSecondaryKey(){
        //given
        String institutionId = "institutionId";
        //when
        Executable executable = () -> apiManagementService.regenerateSecondaryKey(institutionId);
        //then
        assertDoesNotThrow(executable);
        verify(apiManagerConnectorMock, times(1))
                .regenerateSecondaryKey(institutionId);
        verifyNoInteractions(externalApiConnectorMock);
        verifyNoMoreInteractions(apiManagerConnectorMock);
    }

}
