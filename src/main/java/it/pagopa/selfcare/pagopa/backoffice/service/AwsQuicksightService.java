package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.model.SessionTag;

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

    @Autowired
    public AwsQuicksightService(
            AwsQuicksightClient awsQuicksightClient,
            @Value("${aws.quicksight.account-id}") String accountId,
            @Value("${aws.quicksight.dashboard-id}") String initialDashboardId,
            @Value("${aws.quicksight.namespace}") String namespace,
            @Value("${aws.quicksight.allowed-domains}") List<String> allowedDomains
    ) {
        this.awsQuicksightClient = awsQuicksightClient;
        this.accountId = accountId;
        this.initialDashboardId = initialDashboardId;
        this.namespace = namespace;
        this.authorizedResourceArns = Collections.singletonList(String.format("arn:aws:quicksight:%s:%s:dashboard/%s", Region.EU_WEST_1, accountId, initialDashboardId));
        this.allowedDomains = allowedDomains;
    }

    /**
     * Generated embed url for Aws quicksight dashboard
     *
     * @return dashboard's embed url
     */
    public String generateEmbedUrlForAnonymousUser(String pspTaxCode) {
        // TODO verify key session tag
        List<SessionTag> sessionTags = Collections.singletonList(SessionTag.builder().key("comune_tag").value(pspTaxCode).build());
        return awsQuicksightClient.generateEmbedUrlForAnonymousUser(
                this.accountId,
                this.initialDashboardId,
                this.namespace,
                this.authorizedResourceArns,
                this.allowedDomains,
                sessionTags);
    }
}
