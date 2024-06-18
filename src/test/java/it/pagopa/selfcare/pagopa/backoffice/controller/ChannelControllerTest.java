package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelPspListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleSubscriptionStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.service.ChannelService;
import it.pagopa.selfcare.pagopa.backoffice.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class ChannelControllerTest {

    private static final String CHANNEL_CODE = "channelCode";
    @MockBean
    private ChannelService channelService;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getChannels() throws Exception {
        when(channelService.getChannels(any(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(new WrapperChannelsResource());
        mvc.perform(get("/channels")
                        .param("status", ConfigurationStatus.ACTIVE.name())
                        .param("channelCode", CHANNEL_CODE)
                        .param("brokerCode", "brokerCode")
                        .param("limit", "10")
                        .param("page", "0"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getChannelDetails() throws Exception {
        when(channelService.getChannel(CHANNEL_CODE))
                .thenReturn(ChannelDetailsResource.builder().channelCode(CHANNEL_CODE).build());

        mvc.perform(get("/channels/{channel-code}", CHANNEL_CODE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getChannelPaymentServiceProviders() throws Exception {
        when(channelService.getPSPsByChannel(anyInt(), anyInt(), eq(CHANNEL_CODE), anyString()))
                .thenReturn(new ChannelPspListResource());

        mvc.perform(get("/channels/{channel-code}/payment-service-providers", CHANNEL_CODE)
                        .param("psp-name", "pspName")
                        .param("limit", "10")
                        .param("page", "0"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getChannelsCSV() throws Exception {
        mvc.perform(get("/channels/csv"))
                .andExpect(status().is2xxSuccessful());

        verify(channelService).getChannelsInCSVFile(any());
    }
//
//    @Test
//    void createChannel() throws Exception {
//        when(channelService.validateChannelCreation(any()))
//                .thenReturn(WrapperChannelDetailsResource.builder().channelCode(CHANNEL_CODE).build());
//
//        mvc.perform(post("/channels")
//                        .content(objectMapper.writeValueAsString(new ChannelDetailsDto()))
//                        .content(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().is2xxSuccessful());
//    }
}