package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.*;
import it.pagopa.selfcare.pagopa.backoffice.core.GecService;
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

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {GecController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        GecController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class GecControllerTest {

    private static final String BASE_URL = "/gec";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private GecService gecServiceMock;


//    @Test
//    void getBundlesByCI() throws Exception {
//        //given
//
//        Integer limit = 1;
//        Integer page = 1;
//        String cifiscalcode = "cifiscalcode";
//        String xRequestId = "1";
//
//        Bundle bundle = mockInstance(new Bundle());
//        Bundles bundles = mockInstance(new Bundles());
//        bundles.setBundles(List.of(bundle));
//        PageInfo pageInfo = mockInstance(new PageInfo());
//        bundles.setPageInfo(pageInfo);
//
//        when(gecServiceMock.getBundlesByCI(anyString(), anyInt(), anyInt(),anyString()))
//                .thenReturn(bundles);
//        //when
//        mvc.perform(MockMvcRequestBuilders
//                        .get(BASE_URL+ "/ci/bundles")
//                        .queryParam("limit", String.valueOf(limit))
//                        .queryParam("page", String.valueOf(page))
//                        .queryParam("ciFiscalcode", cifiscalcode)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.pageInfo", notNullValue()));
//                .andExpect(jsonPath("$.bundles", notNullValue()))
//                .andExpect(jsonPath("$.bundles", not(empty())))
//                .andExpect(jsonPath("$.bundles[0].description", notNullValue()));
//        //then
//        verify(gecServiceMock, times(1))
//                .getBundlesByCI(anyString(), anyInt(), anyInt(), anyString());
//        verifyNoMoreInteractions(gecServiceMock);
//    }

    @Test
    void getTouchpoints() throws Exception {
        //given

        Integer limit = 1;
        Integer page = 1;
        String xRequestId = "1";

        Touchpoint touchpoint = mockInstance(new Touchpoint());
        Touchpoints touchpoints = mockInstance(new Touchpoints());
        touchpoints.setTouchpoints(List.of(touchpoint));
        PageInfo pageInfo = mockInstance(new PageInfo());
        touchpoints.setPageInfo(pageInfo);

        when(gecServiceMock.getTouchpoints(anyInt(), anyInt(),anyString()))
                .thenReturn(touchpoints);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL+ "/bundles/touchpoints")
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.touchpoints", notNullValue()))
                .andExpect(jsonPath("$.touchpoints", not(empty())));
        //then
        verify(gecServiceMock, times(1))
                .getTouchpoints(anyInt(), anyInt(), anyString());
        verifyNoMoreInteractions(gecServiceMock);
    }

    @Test
    void getBundlesByPSP() throws Exception {
        //given

        Integer limit = 1;
        Integer page = 1;
        String pspCode = "pspCode";
        String xRequestId = "1";
        final ArrayList<BundleType> bundleType = new ArrayList<>();
        final String name = "name";

        Bundle bundle = mockInstance(new Bundle());
        Bundles bundles = mockInstance(new Bundles());
        bundles.setBundles(List.of(bundle));
        PageInfo pageInfo = mockInstance(new PageInfo());
        bundles.setPageInfo(pageInfo);

        when(gecServiceMock.getBundlesByPSP(anyString(), any(), any(), anyInt(), anyInt(),anyString()))
                .thenReturn(bundles);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL+ "/{pspCode}/bundles", pspCode)
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("name", name)
                        .queryParam("bundleType", "GLOBAL")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.pageInfo", notNullValue()));
                .andExpect(jsonPath("$.bundles", notNullValue()))
                .andExpect(jsonPath("$.bundles", not(empty())))
                .andExpect(jsonPath("$.bundles[0].description", notNullValue()));
        //then
        verify(gecServiceMock, times(1))
                .getBundlesByPSP(anyString(), any(), any(), anyInt(), anyInt(), anyString());
        verifyNoMoreInteractions(gecServiceMock);
    }
}
