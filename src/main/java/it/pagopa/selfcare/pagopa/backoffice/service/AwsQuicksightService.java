package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;

import java.util.Collections;
import java.util.List;

@Service
public class AwsQuicksightService {

    private final String accountId;
    private final String initialDashboardId;
    private final String namespace;
    private final List<String> authorizedResourceArns;
    private final List<String> allowedDomains;
    private final AwsQuicksightClient awsQuicksightClient;
    private final String sessionTagKey;

    @Autowired
    public AwsQuicksightService(
            AwsQuicksightClient awsQuicksightClient,
            @Value("${aws.quicksight.account-id}") String accountId,
            @Value("${aws.quicksight.dashboard-id}") String initialDashboardId,
            @Value("${aws.quicksight.namespace}") String namespace,
            @Value("${aws.quicksight.region}") Region region,
            @Value("${aws.quicksight.allowed-domains}") List<String> allowedDomains,
            @Value("${aws.quicksight.session-tag-key}") String sessionTagKey
    ) {
        this.awsQuicksightClient = awsQuicksightClient;
        this.accountId = accountId;
        this.initialDashboardId = initialDashboardId;
        this.namespace = namespace;
        this.authorizedResourceArns = Collections.singletonList(String.format("arn:aws:quicksight:%s:%s:dashboard/%s", region, accountId, initialDashboardId));
        this.allowedDomains = allowedDomains;
        this.sessionTagKey = sessionTagKey;
    }

    /**
     * Generated embed url for Aws quicksight dashboard
     *
     * @return dashboard's embed url
     */
    public QuicksightEmbedUrlResponse generateEmbedUrlForAnonymousUser(String pspTaxCode) {
        String embedUrl = awsQuicksightClient.generateEmbedUrlForAnonymousUser(
                this.accountId,
                this.initialDashboardId,
                this.namespace,
                this.authorizedResourceArns,
                this.allowedDomains,
                sessionTagKey,
                pspTaxCode);
        QuicksightEmbedUrlResponse quicksightEmbedUrlResponse = new QuicksightEmbedUrlResponse();
        quicksightEmbedUrlResponse.setEmbedUrl(embedUrl);
        return quicksightEmbedUrlResponse;
    }
}
