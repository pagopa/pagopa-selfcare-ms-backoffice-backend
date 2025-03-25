package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserProductStatus;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.QUICKSIGHT_DASHBOARD_PRODUCT_ID;

@Slf4j
@Service
public class AwsQuicksightService {

    private static final String INSTITUTION_ID_MDC_KEY = "institutionId";
    private static final String USER_ID_MDC_KEY = "userId";
    private static final String DASHBOARD_URL_MDC_KEY = "dashboardUrl";

    private final AwsQuicksightClient awsQuicksightClient;
    private final FeatureManager featureManager;
    private final ExternalApiClient externalApiClient;

    @Autowired
    public AwsQuicksightService(AwsQuicksightClient awsQuicksightClient, FeatureManager featureManager, ExternalApiClient externalApiClient) {
        this.awsQuicksightClient = awsQuicksightClient;
        this.featureManager = featureManager;
        this.externalApiClient = externalApiClient;
    }

    /**
     * Generated embed url for Aws quicksight dashboard
     *
     * @param institutionIdForOperator institution's id for dashboard retrieval (Only for pagoPA operator)
     * @return dashboard's embed url
     */
    public QuicksightEmbedUrlResponse generateEmbedUrlForAnonymousUser(String institutionIdForOperator) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = Utility.extractUserIdFromAuth(authentication);
        String institutionId = getInstitutionId(authentication, institutionIdForOperator);

        QuicksightEmbedUrlResponse quicksightEmbedUrlResponse = new QuicksightEmbedUrlResponse();

        Institution institution = this.externalApiClient.getInstitution(institutionId);
        if (Boolean.FALSE.equals(this.featureManager.isEnabled("quicksight-product-free-trial")) &&
                Boolean.FALSE.equals(this.featureManager.isEnabled("isOperator")) &&
                isNotSubscribedToDashboardProduct(institution)
        ) {
            throw new AppException(AppError.FORBIDDEN);
        }
        String embedUrl = this.awsQuicksightClient.generateEmbedUrlForAnonymousUser(institutionId, institution.getDescription());

        quicksightEmbedUrlResponse.setEmbedUrl(embedUrl);

        addDashboardLogMetadata(institutionId, userId, quicksightEmbedUrlResponse.getEmbedUrl());
        log.info("Quicksight dashboard url requested by user {} for institution {}. Url: {}", userId, institutionId, quicksightEmbedUrlResponse.getEmbedUrl());
        removeDashboardLogMetadata();
        return quicksightEmbedUrlResponse;
    }

    private String getInstitutionId(Authentication authentication, String institutionIdForOperator) {
        if (Boolean.TRUE.equals(this.featureManager.isEnabled("isOperator"))) {
            if (institutionIdForOperator == null) {
                throw new AppException(AppError.INVALID_OPERATOR_GENERATE_PSP_DASHBOARD_REQUEST);
            }
            return institutionIdForOperator;
        }
        return Utility.extractInstitutionIdFromAuth(authentication);
    }

    private boolean isNotSubscribedToDashboardProduct(Institution institution) {
        return institution.getOnboarding().parallelStream().noneMatch(el -> el.getProductId().equals(QUICKSIGHT_DASHBOARD_PRODUCT_ID) && el.getStatus().equals(UserProductStatus.ACTIVE));
    }

    private void removeDashboardLogMetadata() {
        MDC.remove(INSTITUTION_ID_MDC_KEY);
        MDC.remove(USER_ID_MDC_KEY);
        MDC.remove(DASHBOARD_URL_MDC_KEY);
    }

    private void addDashboardLogMetadata(
            String institutionId,
            String userId, String embedUrl
    ) {
        MDC.put(INSTITUTION_ID_MDC_KEY, institutionId);
        MDC.put(USER_ID_MDC_KEY, userId);
        MDC.put(DASHBOARD_URL_MDC_KEY, embedUrl);
    }
}
