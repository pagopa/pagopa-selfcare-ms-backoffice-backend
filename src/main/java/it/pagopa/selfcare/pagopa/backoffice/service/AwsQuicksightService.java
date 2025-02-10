package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsQuicksightService {

    private final AwsQuicksightClient awsQuicksightClient;

    @Autowired
    public AwsQuicksightService(AwsQuicksightClient awsQuicksightClient
    ) {
        this.awsQuicksightClient = awsQuicksightClient;
    }

    /**
     * Generated embed url for Aws quicksight dashboard
     *
     * @return dashboard's embed url
     */
    public QuicksightEmbedUrlResponse generateEmbedUrlForAnonymousUser(String institutionId) {
        String embedUrl = this.awsQuicksightClient.generateEmbedUrlForAnonymousUser(institutionId);
        QuicksightEmbedUrlResponse quicksightEmbedUrlResponse = new QuicksightEmbedUrlResponse();
        quicksightEmbedUrlResponse.setEmbedUrl(embedUrl);
        return quicksightEmbedUrlResponse;
    }
}
