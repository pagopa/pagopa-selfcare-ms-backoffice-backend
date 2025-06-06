package it.pagopa.selfcare.pagopa.backoffice.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class UtilityTest {

  private static final String USER_ID = "userId";
  private static final String INSTITUTION_TAX_CODE = "institutionTaxCode";
  private static final String INSTITUTION_ID = "institutionId";
  private static final String ADMIN = "admin";

  @Mock private Authentication authenticationMock;

  @Test
  void extractUserIdFromAuthTest() {
    SelfCareUser selfCareUser = buildSelfCareUser();
    doReturn(selfCareUser).when(authenticationMock).getPrincipal();

    String result = Utility.extractUserIdFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals(USER_ID, result);
  }

  @Test
  void extractUserIdFromAuthTestAuthenticationNotExpectedType() {
    String result = Utility.extractUserIdFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractUserIdFromAuthTestAuthenticationNull() {
    String result = Utility.extractUserIdFromAuth(null);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractInstitutionIdFromAuthTest() {
    SelfCareUser selfCareUser = buildSelfCareUser();
    doReturn(selfCareUser).when(authenticationMock).getPrincipal();

    String result = Utility.extractInstitutionIdFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals(INSTITUTION_ID, result);
  }

  @Test
  void extractInstitutionIdFromAuthTestAuthenticationNotExpectedType() {
    String result = Utility.extractInstitutionIdFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractInstitutionIdFromAuthTestAuthenticationNull() {
    String result = Utility.extractInstitutionIdFromAuth(null);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractInstitutionTaxCodeFromAuthTest() {
    SelfCareUser selfCareUser = buildSelfCareUser();
    doReturn(selfCareUser).when(authenticationMock).getPrincipal();

    String result = Utility.extractInstitutionTaxCodeFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals(INSTITUTION_TAX_CODE, result);
  }

  @Test
  void extractInstitutionTaxCodeFromAuthTestAuthenticationNotExpectedType() {
    String result = Utility.extractInstitutionTaxCodeFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractInstitutionTaxCodeFromAuthTestAuthenticationNull() {
    String result = Utility.extractInstitutionTaxCodeFromAuth(null);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractUserProductRoleFromAuthTest() {
    SelfCareUser selfCareUser = buildSelfCareUser();
    doReturn(selfCareUser).when(authenticationMock).getPrincipal();

    String result = Utility.extractUserProductRoleFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals(ADMIN, result);
  }

  @Test
  void extractUserProductRoleFromAuthTestAuthenticationNotExpectedType() {
    String result = Utility.extractUserProductRoleFromAuth(authenticationMock);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void extractUserProductRoleFromAuthTestAuthenticationNull() {
    String result = Utility.extractUserProductRoleFromAuth(null);

    assertNotNull(result);
    assertEquals("", result);
  }

  private SelfCareUser buildSelfCareUser() {
    return SelfCareUser.builder(USER_ID)
        .orgVat(INSTITUTION_TAX_CODE)
        .orgId(INSTITUTION_ID)
        .orgRole(ADMIN)
        .build();
  }
}
