package it.pagopa.selfcare.pagopa.backoffice.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitutionProduct;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = {MappingsConfiguration.class, ApiManagementComponent.class})
class ApiManagementComponentTest {

  private static final String INSTITUTION_ID = "INSTITUTION_ID";
  private static final String USER_ID = "user-id";
  private static final String INSTITUTION_TAX_CODE = "aTaxCode";

  @MockBean private ExternalApiClient externalApiClient;

  @Autowired private ApiManagementComponent sut;

  @Test
  void getInstitutionDetailForOperatorTestSuccess() {
    when(externalApiClient.getInstitution(INSTITUTION_ID))
        .thenReturn(buildInstitutionResponse());

    InstitutionDetail institutionDetail = sut.getInstitutionDetailForOperator(INSTITUTION_ID);

    assertNotNull(institutionDetail);
  }

  @Test
  void getInstitutionDetailForTestSuccess() {
    when(externalApiClient.getInstitution(INSTITUTION_ID))
        .thenReturn(buildInstitutionResponse());
    when(externalApiClient.getUserInstitution(
            USER_ID, INSTITUTION_ID, null, null, null, null, null))
        .thenReturn(List.of(buildUserInstitution("prod-pagopa")));

    InstitutionDetail institutionDetail = sut.getInstitutionDetail(INSTITUTION_ID, USER_ID);

    assertNotNull(institutionDetail);
  }

  @Test
  void getInstitutionDetailForTestFailNoUserInstitutionFound() {
    when(externalApiClient.getInstitution(INSTITUTION_ID))
        .thenReturn(buildInstitutionResponse());
    when(externalApiClient.getUserInstitution(
            USER_ID, INSTITUTION_ID, null, null, null, null, null))
        .thenReturn(Collections.emptyList());

    AppException e =
        assertThrows(AppException.class, () -> sut.getInstitutionDetail(INSTITUTION_ID, USER_ID));

    assertNotNull(e);
    assertEquals(HttpStatus.UNAUTHORIZED, e.getHttpStatus());
  }

  @Test
  void getInstitutionDetailForTestFailNoUserInstitutionBackofficeProductFound() {
    when(externalApiClient.getInstitution(INSTITUTION_ID))
        .thenReturn(buildInstitutionResponse());
    when(externalApiClient.getUserInstitution(
            USER_ID, INSTITUTION_ID, null, null, null, null, null))
        .thenReturn(List.of(buildUserInstitution("another-product")));

    AppException e =
        assertThrows(AppException.class, () -> sut.getInstitutionDetail(INSTITUTION_ID, USER_ID));

    assertNotNull(e);
    assertEquals(HttpStatus.UNAUTHORIZED, e.getHttpStatus());
  }

  private UserInstitution buildUserInstitution(String productId) {
    return UserInstitution.builder()
        .products(
            List.of(
                UserInstitutionProduct.builder()
                    .productId(productId)
                    .status("ACTIVE")
                    .productRole("admin")
                    .productRoleLabel("administrator")
                    .build()))
        .build();
  }

  private it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution
      buildInstitutionResponse() {
    return it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.builder()
        .id(INSTITUTION_ID)
        .externalId("000001")
        .origin("anOrigin")
        .institutionType(InstitutionType.PA.name())
        .taxCode(INSTITUTION_TAX_CODE)
        .description("aDescription")
        .address("aAddress")
        .originId("123")
        .zipCode("aZipCode")
        .digitalAddress("aDigitalAddress")
        .build();
  }
}
