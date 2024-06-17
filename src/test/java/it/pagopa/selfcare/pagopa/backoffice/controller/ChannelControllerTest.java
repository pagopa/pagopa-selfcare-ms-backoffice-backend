package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.OperatorChannelReview;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.service.ChannelService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
public class ChannelControllerTest {

    @MockBean
    private ChannelService channelService;

    @Autowired
    private MockMvc mvc;

    @Inject
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(channelService);
    }

    private static final String CHANNEL_CODE = "channelCode";

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

}
