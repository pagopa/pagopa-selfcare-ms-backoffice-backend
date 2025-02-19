package it.pagopa.selfcare.pagopa.backoffice.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserRequest;
import software.amazon.awssdk.services.quicksight.model.GenerateEmbedUrlForAnonymousUserResponse;

@SpringBootTest(classes = AwsQuicksightClient.class)
class AwsQuicksightClientTest {

    private static final String EMBED_URL = "embed_URL";

    @MockBean
    private QuickSightClient quickSightClient;

    @Autowired
    private AwsQuicksightClient sut;

    @Test
    void generateEmbedUrlForAnonymousUserSuccess() {
        when(quickSightClient.generateEmbedUrlForAnonymousUser(any(GenerateEmbedUrlForAnonymousUserRequest.class)))
                .thenReturn(GenerateEmbedUrlForAnonymousUserResponse.builder().embedUrl(EMBED_URL).build());

        AtomicReference<String> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(sut.generateEmbedUrlForAnonymousUser("sessionTagValue")));
        assertEquals(EMBED_URL, response.get());
    }
}