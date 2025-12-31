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
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    void processBulkIbanOperations() throws Exception {
        // Arrange
        String ciCode = CI_CODE;
        List<IbanOperation> operations = List.of(
                IbanOperation.builder()
                        .type(IbanOperationType.CREATE)
                        .ibanValue("IT60X0542811101000000123456")
                        .description("Test IBAN 1")
                        .validityDate(LocalDate.now().toString())
                        .build(),
                IbanOperation.builder()
                        .type(IbanOperationType.UPDATE)
                        .ibanValue("IT60X0542811101000000654321")
                        .description("Test IBAN 2")
                        .validityDate(LocalDate.now().toString())
                        .build()
        );

        CreditorInstitutionDetails ciDetails = new CreditorInstitutionDetails();
        ciDetails.setBusinessName("Test CI Business Name");

        when(apiConfigClient.getCreditorInstitutionDetails(ciCode))
                .thenReturn(ciDetails);

        assertDoesNotThrow(() -> sut.processBulkIbanOperations(ciCode, operations));

        verify(apiConfigClient).getCreditorInstitutionDetails(ciCode);
        verify(apiConfigClient).createCreditorInstitutionIbansBulk(any(MultipartFile.class));
    }
}
