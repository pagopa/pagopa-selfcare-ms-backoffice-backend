package it.pagopa.selfcare.pagopa.backoffice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class IbanServiceTest {

    @MockBean
    private ExternalApiClient externalApiClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    @InjectMocks
    private IbanService ibanService;


    @Test
    void exportIbansToCsv() throws IOException {
        String delegations = TestUtil.readJsonFromFile("response/external/delegation.json");

        List<DelegationExternal> delegationResponse = TestUtil.toObject(delegations, new TypeReference<>() {
        });
        when(externalApiClient.getBrokerDelegation(any(), eq("1111"), eq("prod-pagopa"), eq("FULL")))
                .thenReturn(delegationResponse);

        String ibans = TestUtil.readJsonFromFile("response/apiconfigintegration/ibans.json");

        when(apiConfigSelfcareIntegrationClient.getIbans(1, 0, "canary", List.of("1234567890")))
                .thenReturn(TestUtil.toObject(ibans, IbansList.class));
        when(apiConfigSelfcareIntegrationClient.getIbans(100, 0, "canary", List.of("1234567890")))
                .thenReturn(TestUtil.toObject(ibans, IbansList.class));

        byte[] result = ibanService.exportIbansToCsv("1111");
        assertNotNull(result);
    }
}
