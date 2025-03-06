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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.QUICKSIGHT_DASHBOARD_PRODUCT_ID;

@Slf4j
@Service
public class AwsQuicksightService {


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
     * @return dashboard's embed url
     */
    public QuicksightEmbedUrlResponse generateEmbedUrlForAnonymousUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = Utility.extractUserIdFromAuth(authentication);
        String institutionId = Utility.extractInstitutionIdFromAuth(authentication);

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

        log.info("Quicksight dashboard url requested by user {} for institution {}. Url: {}", userId, institutionId, quicksightEmbedUrlResponse.getEmbedUrl());
        return quicksightEmbedUrlResponse;
    }

    private static boolean isNotSubscribedToDashboardProduct(Institution institution) {
        return institution.getOnboarding().parallelStream().noneMatch(el -> el.getProductId().equals(QUICKSIGHT_DASHBOARD_PRODUCT_ID) && el.getStatus().equals(UserProductStatus.ACTIVE));
    }
}
