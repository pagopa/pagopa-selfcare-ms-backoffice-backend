package it.pagopa.selfcare.pagopa.backoffice.service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.PAGOPA_BACKOFFICE_PRODUCT_ID;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.QUICKSIGHT_DASHBOARD_PRODUCT_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Onboarding;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserProductStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

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
                .onboarding(
                        List.of(Onboarding.builder()
                                .productId(PAGOPA_BACKOFFICE_PRODUCT_ID)
                                .status(UserProductStatus.ACTIVE)
                                .build()))
                .build();
    }
}
