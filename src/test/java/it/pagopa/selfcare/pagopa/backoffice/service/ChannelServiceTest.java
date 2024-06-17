package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ChannelService.class})
public class ChannelServiceTest {

    @Autowired
    private ChannelService service;

    @MockBean
    private WrapperService wrapperService;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private AwsSesClient awsSesClient;

    @MockBean
    private JiraServiceManagerClient jiraServiceManagerClient;

    private static final String CHANNEL_CODE = "channelCode";


    @Test
    void updateWrapperStationWithOperatorReviewSuccess() {
        when(wrapperService.updateChannelWithOperatorReview(anyString(), anyString()))
                .thenReturn(buildChannelDetailsWrapperEntities());

        ChannelDetailsResource result = assertDoesNotThrow(() -> service.updateWrapperChannelWithOperatorReview(
                CHANNEL_CODE, "brokerCode", "nota"));

        assertNotNull(result);

        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void updateWrapperStationWithOperatorReviewFail() {
        when(wrapperService.updateChannelWithOperatorReview(anyString(), anyString()))
                .thenThrow(AppException.class);

        AppException e = assertThrows(AppException.class,
                () -> service.updateWrapperChannelWithOperatorReview(
                        CHANNEL_CODE, "brokerCode", "nota"));

        assertNotNull(e);

        verify(awsSesClient, never()).sendEmail(any());
    }

    private @NotNull WrapperEntities<ChannelDetails> buildChannelDetailsWrapperEntities() {
        WrapperEntity<ChannelDetails> entity = new WrapperEntity<>();
        entity.setEntity(buildChannelDetails());
        WrapperEntities<ChannelDetails> entities = new WrapperEntities<>();
        entities.setCreatedAt(Instant.now());
        entities.setEntities(Collections.singletonList(entity));
        return entities;
    }

    private @NotNull ChannelDetails buildChannelDetails() {
        ChannelDetails channelDetails = new ChannelDetails();
        channelDetails.setChannelCode(CHANNEL_CODE);
        channelDetails.setEnabled(true);
        return channelDetails;
    }

}
