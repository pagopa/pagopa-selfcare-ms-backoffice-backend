package it.pagopa.selfcare.pagopa.backoffice.service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.PAGOPA_BACKOFFICE_PRODUCT_ID;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.QUICKSIGHT_DASHBOARD_PRODUCT_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Onboarding;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserProductStatus;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = AwsQuicksightService.class)
class AwsQuicksightServiceTest {

    private static final String EMBED_URL = "embed_URL";
    private static final String INSTITUTION_ID = "institution_id";

    @MockBean
    private AwsQuicksightClient awsQuicksightClient;
    @MockBean
    private FeatureManager featureManager;
    @MockBean
    private ExternalApiClient externalApiClient;

    @Autowired
    private AwsQuicksightService sut;

    @BeforeEach
    void setup() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("user");
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }


    @Test
    void shouldThrowNotFoundWhenInstitutionIsNull() {
        String userId = "user123";
        String institutionId = "inst123";

        // Mock SecurityContext e Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Institution institution = new Institution();
        institution.setDescription("Test Description");
        institution.setOnboarding(Collections.emptyList());

        when(externalApiClient.getInstitution(anyString())).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> {
            sut.generateEmbedUrlForAnonymousUser(institutionId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Institution Not Found", exception.getTitle());
        assertTrue(exception.getMessage().contains("Required institution data has not been found on the storage"));
    }

    @Test
    void shouldThrowForbiddenWhenInstitutionIsNotPsp() {
        String userId = "user123";
        String institutionId = "inst123";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Institution institution = new Institution();
        institution.setTaxCode("TAX123");
        institution.setInstitutionType("NOT_PSP_TYPE");

        when(externalApiClient.getInstitution(any())).thenReturn(institution);

        try (MockedStatic<RoleType> roleMock = mockStatic(RoleType.class)) {
            roleMock.when(() ->
                    RoleType.fromSelfcareRole("TAX123", "NOT_PSP_TYPE")
            ).thenReturn(RoleType.PT);

            AppException exception = assertThrows(AppException.class, () -> {
                sut.generateEmbedUrlForAnonymousUser(institutionId);
            });

            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertEquals("Forbidden", exception.getTitle());
        }
    }

    @Test
    void shouldReturnEmbedUrlWhenInstitutionIsPspAndFeatureFlagsAllowIt() {
        String userId = "user123";
        String institutionId = "inst123";
        String institutionDescription = "Test PSP";
        String expectedEmbedUrl = "https://quicksight.aws.amazon.com/embedUrl";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userId);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(featureManager.isEnabled("isOperator")).thenReturn(false);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(true); // bypassa check abbonamento

        Institution institution = new Institution();
        institution.setTaxCode("PSP-TAX");
        institution.setInstitutionType("PSP-TYPE");
        institution.setDescription(institutionDescription);

        when(externalApiClient.getInstitution(any())).thenReturn(institution);

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(), anyString()))
                .thenReturn("https://quicksight.aws.amazon.com/embedUrl");

        try (MockedStatic<RoleType> roleMock = mockStatic(RoleType.class)) {
            roleMock.when(() ->
                    RoleType.fromSelfcareRole("PSP-TAX", "PSP-TYPE")
            ).thenReturn(RoleType.PSP);

            QuicksightEmbedUrlResponse result = sut.generateEmbedUrlForAnonymousUser(institutionId);
            assertNotNull(result);
            assertEquals(expectedEmbedUrl, result.getEmbedUrl());
        }
    }

    @Test
    void generateEmbedUrlForAnonymousUser_Success() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithActiveDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        QuicksightEmbedUrlResponse response = assertDoesNotThrow(() -> sut.generateEmbedUrlForAnonymousUser(null));

        assertNotNull(response);
        assertEquals(EMBED_URL, response.getEmbedUrl());

    }

    @Test
    void generateEmbedUrlForAnonymousUser_IsOperator_Success() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.TRUE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithActiveDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        QuicksightEmbedUrlResponse response = assertDoesNotThrow(() -> sut.generateEmbedUrlForAnonymousUser(INSTITUTION_ID));

        assertNotNull(response);
        assertEquals(EMBED_URL, response.getEmbedUrl());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_EnabledFreeTrial_Success() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.TRUE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithActiveDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        QuicksightEmbedUrlResponse response = assertDoesNotThrow(() -> sut.generateEmbedUrlForAnonymousUser(null));

        assertNotNull(response);
        assertEquals(EMBED_URL, response.getEmbedUrl());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_InactiveProduct_Forbidden() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithInactiveDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        AppException e = assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(null));
        
        assertNotNull(e);
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_WithoutProduct_Forbidden() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithoutDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        AppException e = assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(null));

        assertNotNull(e);
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_getInstitution_404() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenThrow(new AppException(AppError.INSTITUTION_NOT_FOUND));

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        AppException e = assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(null));

        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_IsOperatorWithoutProvidingInsitutionId_Success() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.TRUE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithActiveDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString(),anyString())).thenReturn(EMBED_URL);

        AppException e = assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(null));

        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());

        verify(featureManager, never()).isEnabled("quicksight-product-free-trial");
        verify(externalApiClient, never()).getInstitution(anyString());
        verify(awsQuicksightClient, never()).generateEmbedUrlForAnonymousUser(anyString(),anyString());
    }

    Institution institutionWithActiveDashboardProduct() {
        return Institution.builder()
                .description("pspName")
                .institutionType(RoleType.PSP.name())
                .onboarding(
                        List.of(Onboarding.builder()
                                .productId(QUICKSIGHT_DASHBOARD_PRODUCT_ID)
                                .status(UserProductStatus.ACTIVE)
                                .build()))
                .build();
    }

    Institution institutionWithInactiveDashboardProduct() {
        return Institution.builder()
                .description("pspName")
                .institutionType(RoleType.PSP.name())
                .onboarding(
                        List.of(Onboarding.builder()
                                .productId(QUICKSIGHT_DASHBOARD_PRODUCT_ID)
                                .status(UserProductStatus.REJECTED)
                                .build()))
                .build();
    }

    Institution institutionWithoutDashboardProduct() {
        return Institution.builder()
                .description("pspName")
                .institutionType(RoleType.PSP.name())
                .onboarding(
                        List.of(Onboarding.builder()
                                .productId(PAGOPA_BACKOFFICE_PRODUCT_ID)
                                .status(UserProductStatus.ACTIVE)
                                .build()))
                .build();
    }
}
