package it.pagopa.selfcare.pagopa.backoffice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.quicksight.QuickSightClient;

@Configuration
public class AwsQuicksightConfig {

  @Value("${aws.quicksight.region}")
  private Region region;

  @Value("${aws.quicksight.access-key}")
  private String accessKey;

  @Value("${aws.quicksight.secret-key}")
  private String secretKey;

  @Bean
  public QuickSightClient quickSightClient() {
    return QuickSightClient.builder()
        .region(region)
        .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
        .build();
  }
}
