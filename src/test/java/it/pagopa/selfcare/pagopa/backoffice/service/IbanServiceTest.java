package it.pagopa.selfcare.pagopa.backoffice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {IbanService.class, MappingsConfiguration.class})
class IbanServiceTest {

  private static final String CI_CODE = "11111";
  private static final String IBAN = "GB33BUKB20201555555556";

  @MockBean private ExternalApiClient externalApiClient;

  @MockBean private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

  @MockBean private ApiConfigClient apiConfigClient;

  @Autowired private IbanService sut;

  @Test
  void getIban() throws IOException {
    when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(CI_CODE, null))
        .thenReturn(TestUtil.fileToObject("response/apiconfig/ibans.json", Ibans.class));

    Ibans response = assertDoesNotThrow(() -> sut.getIban(CI_CODE, null));

    assertNotNull(response);
    assertNotNull(response.getIbanList());
    assertEquals(28, response.getIbanList().size());
  }

  @Test
  void createCreditorInstitutionIban() throws Exception {
    when(apiConfigClient.createCreditorInstitutionIbans(eq(CI_CODE), any()))
        .thenReturn(TestUtil.fileToObject("request/create_iban.json", IbanCreateApiconfig.class));

    IbanCreate ibanCreate = TestUtil.fileToObject("request/create_iban.json", IbanCreate.class);
    Iban response = assertDoesNotThrow(() -> sut.createIban(CI_CODE, ibanCreate));

    assertNotNull(response);
  }

  @Test
  void updateCreditorInstitutionIbanWithoutLabel() throws Exception {
    when(apiConfigClient.updateCreditorInstitutionIbans(eq(CI_CODE), eq(IBAN), any()))
        .thenReturn(TestUtil.fileToObject("request/create_iban.json", IbanCreateApiconfig.class));

    IbanCreate ibanCreate = TestUtil.fileToObject("request/create_iban.json", IbanCreate.class);
    Iban response = assertDoesNotThrow(() -> sut.updateIban(CI_CODE, IBAN, ibanCreate));

    assertNotNull(response);
  }

  @Test
  void deleteCreditorInstitutionIbanWithoutLabel() {
    assertDoesNotThrow(() -> sut.deleteIban(CI_CODE, IBAN));

    verify(apiConfigClient).deleteCreditorInstitutionIbans(CI_CODE, IBAN);
  }
}
