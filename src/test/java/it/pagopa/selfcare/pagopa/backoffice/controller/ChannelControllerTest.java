package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelPspListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.OperatorChannelReview;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.service.ChannelService;
import it.pagopa.selfcare.pagopa.backoffice.service.WrapperService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class ChannelControllerTest {

    private static final String CHANNEL_CODE = "channelCode";

    @MockBean
    private ChannelService channelService;

    @MockBean
    private WrapperService wrapperService;

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

    @Test
    void createChannel() throws Exception {
        when(channelService.validateChannelCreation(any()))
                .thenReturn(WrapperChannelDetailsResource.builder().channelCode(CHANNEL_CODE).build());

        mvc.perform(post("/channels")
                        .content(objectMapper.writeValueAsString(new ChannelDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateChannel() throws Exception {
        when(channelService.validateChannelUpdate(eq(CHANNEL_CODE), any()))
                .thenReturn(ChannelDetailsResource.builder().channelCode(CHANNEL_CODE).build());

        mvc.perform(put("/channels/{channel-code}", CHANNEL_CODE)
                        .content(objectMapper.writeValueAsString(new ChannelDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteChannel() throws Exception {
        mvc.perform(delete("/channels/{channel-code}", CHANNEL_CODE))
                .andExpect(status().is2xxSuccessful());

        verify(channelService).deleteChannel(CHANNEL_CODE);
    }

    @Test
    void getChannelPaymentTypes() throws Exception {
        when(channelService.getPaymentTypesByChannel(CHANNEL_CODE)).thenReturn(new PspChannelPaymentTypesResource());

        mvc.perform(get("/channels/{channel-code}/payment-types", CHANNEL_CODE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void createChannelPaymentType() throws Exception {
        when(channelService.createPaymentTypeOnChannel(any(), eq(CHANNEL_CODE)))
                .thenReturn(new PspChannelPaymentTypesResource());

        mvc.perform(post("/channels/{channel-code}/payment-types", CHANNEL_CODE)
                        .content(objectMapper.writeValueAsString(new PspChannelPaymentTypes()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteChannelPaymentType() throws Exception {
        String paymentType = "payment-type-code";
        mvc.perform(delete("/channels/{channel-code}/payment-types/{payment-type-code}", CHANNEL_CODE, paymentType))
                .andExpect(status().is2xxSuccessful());

        verify(channelService).deletePaymentTypeOnChannel(CHANNEL_CODE, paymentType);
    }

    @Test
    void getChannelDetail() throws Exception {
        when(channelService.getChannelToBeValidated(CHANNEL_CODE))
                .thenReturn(ChannelDetailsResource.builder().channelCode(CHANNEL_CODE).build());

        mvc.perform(get("/channels/merged/{channel-code}", CHANNEL_CODE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getGenericWrapperEntities() throws Exception {
        when(wrapperService.findById(CHANNEL_CODE)).thenReturn(new WrapperEntities<>());

        mvc.perform(get("/channels/wrapper/{channel-code}", CHANNEL_CODE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void createWrapperChannelDetails() throws Exception {
        when(channelService.createChannelToBeValidated(any())).thenReturn(new WrapperEntities<>());

        mvc.perform(post("/channels/wrapper")
                        .content(objectMapper.writeValueAsString(buildWrapperChannelDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateWrapperChannelDetails() throws Exception {
        when(channelService.updateChannelToBeValidated(anyString(), any())).thenReturn(new WrapperEntities<>());

        mvc.perform(put("/channels/wrapper/{channel-code}", CHANNEL_CODE)
                        .content(objectMapper.writeValueAsString(ChannelDetailsDto.builder().validationUrl("url").build()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateWrapperChannelWithOperatorReview() throws Exception {
        when(channelService.updateWrapperChannelWithOperatorReview(anyString(), anyString(), anyString()))
                .thenReturn(buildChannelDetailsResource());
        mvc.perform(put("/channels/wrapper/{channelCode}/operator", CHANNEL_CODE)
                        .param("brokerPspCode", "brokerPspCode")
                        .content(objectMapper.writeValueAsBytes(
                                OperatorChannelReview.builder().note("note").build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateWrapperChannelWithOperatorReviewKoForBadRequestOnCode() throws Exception {
        mvc.perform(put("/channels/wrapper/{channelCode}/operator", CHANNEL_CODE)
                        .content(objectMapper.writeValueAsBytes(
                                OperatorChannelReview.builder().note("note").build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateWrapperChannelWithOperatorReviewKoForBadRequestOnNote() throws Exception {
        mvc.perform(put("/channels/wrapper/{channelCode}/operator", CHANNEL_CODE)
                        .param("brokerPspCode", "brokerPspCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private @NotNull ChannelDetailsResource buildChannelDetailsResource() {
        ChannelDetailsResource resource = new ChannelDetailsResource();
        resource.setChannelCode(CHANNEL_CODE);
        return resource;
    }

    private WrapperChannelDetailsDto buildWrapperChannelDetailsDto() {
        return WrapperChannelDetailsDto.builder()
                .channelCode(CHANNEL_CODE)
                .brokerDescription("brokerDescription")
                .brokerPspCode("brokerPspCode")
                .targetHost("targetHost")
                .targetPort(8088L)
                .targetPath("targetPath")
                .redirectProtocol(Protocol.HTTPS)
                .paymentTypeList(Collections.emptyList())
                .validationUrl("validationUrl")
                .build();
    }
}