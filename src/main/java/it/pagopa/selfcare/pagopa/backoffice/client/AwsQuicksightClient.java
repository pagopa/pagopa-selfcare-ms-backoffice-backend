package it.pagopa.selfcare.pagopa.backoffice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserRequest;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserResponse;
import software.amazon.awssdk.services.quicksight.model.SessionTag;

import java.util.List;

@Service
@Slf4j
public class AwsQuicksightClient {

    private final QuickSightClient quickSightClient;

    @Autowired
    public AwsQuicksightClient(
            @Value("${aws.quicksight.region}") Region region,
            @Value("${aws.quicksight.access-key}") String accessKey,
            @Value("${aws.quicksight.secret-key}") String secretKey) {
        this.quickSightClient = QuickSightClient.builder()
                .region(region)
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .build();
    }

    public String generateEmbedUrlForAnonymousUser(
            final String accountId, // YOUR AWS ACCOUNT ID
            final String initialDashboardId, // DASHBOARD ID TO WHICH THE CONSTRUCTED URL POINTS.
            final String namespace, // ANONYMOUS EMBEDDING REQUIRES SPECIFYING A VALID NAMESPACE FOR WHICH YOU WANT THE EMBEDDING URL
            final List<String> authorizedResourceArns, // DASHBOARD ARN LIST TO EMBED
            final List<String> allowedDomains, // RUNTIME ALLOWED DOMAINS FOR EMBEDDING
            final List<SessionTag> sessionTags // SESSION TAGS USED FOR ROW-LEVEL SECURITY
    ) {
        GenerateEmbedUrlForAnonymousUserRequest generateEmbedUrlForAnonymousUserRequest = GenerateEmbedUrlForAnonymousUserRequest.builder()
                .awsAccountId(accountId)
                .namespace(namespace)
                .authorizedResourceArns(authorizedResourceArns)
                .experienceConfiguration(e -> e.dashboard(d -> d.initialDashboardId(initialDashboardId)))
                .sessionTags(sessionTags)
                .allowedDomains(allowedDomains)
                .build();

        GenerateEmbedUrlForAnonymousUserResponse dashboardEmbedUrl = this.quickSightClient.generateEmbedUrlForAnonymousUser(generateEmbedUrlForAnonymousUserRequest);

        return dashboardEmbedUrl.embedUrl();
    }

}
