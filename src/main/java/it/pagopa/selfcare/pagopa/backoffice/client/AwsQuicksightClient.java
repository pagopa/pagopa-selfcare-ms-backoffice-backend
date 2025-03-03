package it.pagopa.selfcare.pagopa.backoffice.client;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserRequest;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserResponse;

@Service
@Slf4j
public class AwsQuicksightClient {

    private final QuickSightClient quickSightClient;
    private final String accountId;
    private final String initialDashboardId;
    private final String namespace;
    private final List<String> authorizedResourceArns;
    private final List<String> allowedDomains;
    private final String sessionTagKey;

    @Autowired
    public AwsQuicksightClient(
            QuickSightClient quickSightClient,
            @Value("${aws.quicksight.account-id}") String accountId,
            @Value("${aws.quicksight.dashboard-id}") String initialDashboardId,
            @Value("${aws.quicksight.namespace}") String namespace,
            @Value("${aws.quicksight.region}") Region region,
            @Value("${aws.quicksight.allowed-domains}") List<String> allowedDomains,
            @Value("${aws.quicksight.session-tag-key}") String sessionTagKey
    ) {
        this.quickSightClient = quickSightClient;
        this.accountId = accountId;
        this.initialDashboardId = initialDashboardId;
        this.namespace = namespace;
        this.authorizedResourceArns = Collections.singletonList(String.format("arn:aws:quicksight:%s:%s:dashboard/%s", region, accountId, initialDashboardId));
        this.allowedDomains = allowedDomains;
        this.sessionTagKey = sessionTagKey;
    }

    public String generateEmbedUrlForAnonymousUser(String sessionTagValue, String pspName) {
        GenerateEmbedUrlForAnonymousUserRequest generateEmbedUrlForAnonymousUserRequest = GenerateEmbedUrlForAnonymousUserRequest.builder()
                .awsAccountId(accountId)
                .namespace(namespace)
                .authorizedResourceArns(authorizedResourceArns)
                .experienceConfiguration(e -> e.dashboard(d -> d.initialDashboardId(initialDashboardId)))
                .sessionTags(s -> s.key(sessionTagKey).value(sessionTagValue).build())
                .allowedDomains(allowedDomains)
                .build();

        GenerateEmbedUrlForAnonymousUserResponse dashboardEmbedUrl = this.quickSightClient.generateEmbedUrlForAnonymousUser(generateEmbedUrlForAnonymousUserRequest);

        return String.format("%s#p.PSP=%s", dashboardEmbedUrl.embedUrl(), pspName);
    }
}
