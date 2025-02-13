package it.pagopa.selfcare.pagopa.backoffice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = AwsQuicksightService.class)
class AwsQuicksightServiceTest {

    private static final String EMBED_URL = "embed_URL";

    @MockBean
    private AwsQuicksightClient awsQuicksightClient;

    @Autowired
    private AwsQuicksightService sut;

    @Test
    void generateEmbedUrlForAnonymousUserSuccess() {
        when(awsQuicksightClient.generateEmbedUrlForAnonymousUser(anyString())).thenReturn(EMBED_URL);

        AtomicReference<QuicksightEmbedUrlResponse> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(sut.generateEmbedUrlForAnonymousUser("institution-id")));
        assertEquals(EMBED_URL, response.get().getEmbedUrl());
    }
}
