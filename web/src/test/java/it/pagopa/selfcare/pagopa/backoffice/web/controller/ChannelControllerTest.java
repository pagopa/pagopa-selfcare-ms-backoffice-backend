package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ChannelController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        ChannelController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
public class ChannelControllerTest {

    private static final String BASE_URL = "/channels";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;


    @Test
    void getChannels() throws Exception {
        //given

        Integer limit = 1;
        Integer page = 1;
        String code = "code";
        String sort = "DESC";
        String xRequestId = "1";

        Channel channel = mockInstance(new Channel());
        Channels channels = mockInstance(new Channels(), "setchannelList");
        channels.setChannelList(List.of(channel));

        when(apiConfigServiceMock.getChannels(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(channels);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("code", code)
                        .queryParam("sort", sort)
                        .header("X-Request-Id", String.valueOf(xRequestId))


                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.page_info", notNullValue()))
                .andExpect(jsonPath("$.channels", notNullValue()))
                .andExpect(jsonPath("$.channels", not(empty())))
                .andExpect(jsonPath("$.channels[0].broker_description", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

}
