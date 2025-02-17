package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.sanitizeLogParam;

@Slf4j
@Service
public class AwsQuicksightService {

  private final AwsQuicksightClient awsQuicksightClient;

  @Autowired
  public AwsQuicksightService(AwsQuicksightClient awsQuicksightClient) {
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

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = Utility.extractUserIdFromAuth(authentication);
    log.info(
        "Quicksigh dashboard url requested by user {} for institution {}. Url: {}",
        userId,
        institutionId,
        quicksightEmbedUrlResponse.getEmbedUrl());
    return quicksightEmbedUrlResponse;
  }
}
