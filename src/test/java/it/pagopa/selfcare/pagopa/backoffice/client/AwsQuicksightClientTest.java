package it.pagopa.selfcare.pagopa.backoffice.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserRequest;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserResponse;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AwsQuicksightClient.class)
class AwsQuicksightClientTest {

    private static final String EMBED_URL = "embed_URL";

    private final QuickSightClient quickSightClient = mock(QuickSightClient.class);

    private final AwsQuicksightClient sut = new AwsQuicksightClient(quickSightClient);

    @Test
    void generateEmbedUrlForAnonymousUserSuccess() {
        when(quickSightClient.generateEmbedUrlForAnonymousUser(any(GenerateEmbedUrlForAnonymousUserRequest.class))).thenReturn(GenerateEmbedUrlForAnonymousUserResponse.builder().embedUrl(EMBED_URL).build());

        AtomicReference<String> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(sut.generateEmbedUrlForAnonymousUser(
                "accountId",
                "dashboardId",
                "namespace",
                Collections.singletonList("authorizedResourceArns"),
                Collections.singletonList("allowed-domain"),
                "sessionTagKey",
                "sessionTagValue")));
        assertEquals(EMBED_URL, response.get());
    }

}