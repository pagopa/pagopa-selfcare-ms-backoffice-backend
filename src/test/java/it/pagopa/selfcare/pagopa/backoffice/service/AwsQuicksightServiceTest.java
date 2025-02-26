package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Onboarding;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.PAGOPA_BACKOFFICE_PRODUCT_ID;
import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.QUICKSIGHT_DASHBOARD_PRODUCT_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AwsQuicksightService.class)
class AwsQuicksightServiceTest {

    private static final String EMBED_URL = "embed_URL";

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

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        AtomicReference<QuicksightEmbedUrlResponse> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(sut.generateEmbedUrlForAnonymousUser()));
        assertEquals(EMBED_URL, response.get().getEmbedUrl());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_IsOperator_Success() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.TRUE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        AtomicReference<QuicksightEmbedUrlResponse> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(sut.generateEmbedUrlForAnonymousUser()));
        assertEquals(EMBED_URL, response.get().getEmbedUrl());
        verifyNoInteractions(externalApiClient);
    }

    @Test
    void generateEmbedUrlForAnonymousUser_EnabledFreeTrial_Success() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.TRUE);

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        AtomicReference<QuicksightEmbedUrlResponse> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(sut.generateEmbedUrlForAnonymousUser()));
        assertEquals(EMBED_URL, response.get().getEmbedUrl());
        verifyNoInteractions(externalApiClient);
    }

    @Test
    void generateEmbedUrlForAnonymousUser_InactiveProduct_Forbidden() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithInactiveDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(), AppError.FORBIDDEN.getTitle());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_WithoutProduct_Forbidden() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenReturn(institutionWithoutDashboardProduct());

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(), AppError.FORBIDDEN.getDetails());
    }

    @Test
    void generateEmbedUrlForAnonymousUser_getInstitution_404() {
        when(featureManager.isEnabled("isOperator")).thenReturn(Boolean.FALSE);
        when(featureManager.isEnabled("quicksight-product-free-trial")).thenReturn(Boolean.FALSE);

        when(externalApiClient.getInstitution(anyString())).thenThrow(new AppException(AppError.INSTITUTION_NOT_FOUND));

        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        assertThrows(AppException.class, () -> sut.generateEmbedUrlForAnonymousUser(), AppError.INSTITUTION_NOT_FOUND.getDetails());
    }

    Institution institutionWithActiveDashboardProduct() {
        return Institution.builder().onboarding(List.of(Onboarding.builder().productId(QUICKSIGHT_DASHBOARD_PRODUCT_ID).status("ACTIVE").build())).build();
    }

    Institution institutionWithInactiveDashboardProduct() {
        return Institution.builder().onboarding(List.of(Onboarding.builder().productId(QUICKSIGHT_DASHBOARD_PRODUCT_ID).status("NOT_ACTIVE").build())).build();
    }

    Institution institutionWithoutDashboardProduct() {
        return Institution.builder().onboarding(List.of(Onboarding.builder().productId(PAGOPA_BACKOFFICE_PRODUCT_ID).status("ACTIVE").build())).build();
    }
}
