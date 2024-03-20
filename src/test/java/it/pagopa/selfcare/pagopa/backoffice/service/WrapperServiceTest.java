package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.sql.Wrapper;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WrapperServiceTest {

    @Mock
    private ApiConfigClient apiConfigClient;

    @Mock
    private WrapperRepository repository;

    @Mock
    private AuditorAware<String> auditorAware;

    @InjectMocks
    private WrapperService sut;

    @Test
    void getFirstValidChannelCodeV2() {
        Channel channel = new Channel();
        channel.setChannelCode("000001_01");
        channel.setEnabled(true);
        Channels channels = new Channels();
        channels.setChannelList(Collections.singletonList(channel));
        when(apiConfigClient.getChannels(any(), any(), any(), any(), any())).thenReturn(channels);
        WrapperEntities wrapperEntities = new WrapperEntities();
        wrapperEntities.setId("000001_01");
        when(repository.findByTypeAndBrokerCode(any(),any(),any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntities)));
        sut.getFirstValidChannelCodeV2("000001");
        verify(apiConfigClient).getChannels(any(),any(),any(),any(),any());
        verify(repository).findByTypeAndBrokerCode(any(),any(),any());
    }
}