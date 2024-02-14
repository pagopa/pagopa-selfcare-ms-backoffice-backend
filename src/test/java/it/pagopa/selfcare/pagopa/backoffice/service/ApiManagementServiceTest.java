package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class ApiManagementServiceTest {

    @MockBean
    private AzureApiManagerClient apimClient;

    @MockBean
    private ExternalApiClient externalApiClient;

    @SpyBean
    private ModelMapper modelMapper;

    @Autowired
    @InjectMocks
    private ApiManagementService service;

    private final String INSTITUTION_ID = "INSTITUTION_ID";

    private final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";

    private final String BROKER_ID = "BROKER_ID";

    @Test
    void getInstitutions() {
        when(externalApiClient.getInstitutions(any()))
                .thenReturn(Collections.singletonList(new InstitutionInfo()));
        List<InstitutionDetail> institutions = service.getInstitutions();
        assertNotNull(institutions);
        assertFalse(institutions.isEmpty());
        verify(externalApiClient).getInstitutions(any());
    }

    @Test
    void getInstitution() throws IOException {
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json", InstitutionResponse.class));
        Institution institution = service.getInstitution(INSTITUTION_ID);
        assertNotNull(institution);
        verify(externalApiClient).getInstitution(any());
    }

    @Test
    void getInstitutionProducts() {
        when(externalApiClient.getInstitutionUserProducts(any(), any())).thenReturn(Collections.singletonList(
                Product.builder().id("0001").description("Product_Description").build()));
        List<Product> products = service.getInstitutionProducts(INSTITUTION_ID);
        assertNotNull(products);
    }

    @Test
    void getBrokerDelegationPSP() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any())).thenReturn(createDelegations());
        List<Delegation> delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, Collections.singletonList(RoleType.PSP));
        assertNotNull(delegations);
        assertEquals(1, delegations.size());
        assertEquals("00001", delegations.get(0).getBrokerId());
    }

    @Test
    void getBrokerDelegationEC() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any())).thenReturn(createDelegations());
        List<Delegation> delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, Collections.singletonList(RoleType.EC));
        assertNotNull(delegations);
        assertEquals(1, delegations.size());
        assertEquals("00002", delegations.get(0).getBrokerId());
    }

    @Test
    void getBrokerDelegationCombinedRoles() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any())).thenReturn(createDelegations());
        List<Delegation> delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, List.of(RoleType.EC,RoleType.PSP));
        assertNotNull(delegations);
        assertEquals(2, delegations.size());
    }

    @Test
    void getInstitutionApiKeys() {
        when(apimClient.getInstitutionApiKeys(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));
        List<InstitutionApiKeys> institutionApiKeys = service.getInstitutionApiKeys(INSTITUTION_ID);
        assertNotNull(institutionApiKeys);
        verify(apimClient).getInstitutionApiKeys(any());
    }

    @Test
    void createSubscriptionKeys() throws IOException {
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json", InstitutionResponse.class));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));
        List<InstitutionApiKeys> institutionApiKeys = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.GPD);
        assertNotNull(institutionApiKeys);
        verify(apimClient).getApiSubscriptions(any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(any());
    }

    @Test
    void regeneratePrimaryKey() {
        service.regeneratePrimaryKey(SUBSCRIPTION_ID);
        verify(apimClient).regeneratePrimaryKey(SUBSCRIPTION_ID);
    }

    @Test
    void regenerateSecondaryKey() {
        service.regenerateSecondaryKey(SUBSCRIPTION_ID);
        verify(apimClient).regenerateSecondaryKey(SUBSCRIPTION_ID);
    }

    public List<DelegationExternal> createDelegations() {
        List<DelegationExternal> delegationExternals = new ArrayList<>();
        delegationExternals.add(
                DelegationExternal
                        .builder()
                        .id("00001")
                        .brokerId("00001")
                        .brokerName("BrokerPsp")
                        .brokerTaxCode("000001")
                        .brokerType("TypePSP")
                        .institutionId("0001")
                        .institutionName("Institution Psp 1")
                        .institutionRootName("Institution Root Name Psp 1")
                        .institutionType("PSP")
                        .build()
        );
        delegationExternals.add(
                DelegationExternal
                        .builder()
                        .id("00002")
                        .brokerId("00002")
                        .brokerName("BrokerEC")
                        .brokerTaxCode("000002")
                        .brokerType("TypeEC")
                        .institutionId("0002")
                        .institutionName("Institution EC 1")
                        .institutionRootName("Institution Root Name EC 1")
                        .institutionType("SCP")
                        .build()
        );
        return delegationExternals;
    }

}