package it.pagopa.selfcare.pagopa.backoffice.controller;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.service.AwsQuicksightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class AwsQuicksightControllerTest {

    private static final String EMBED_URL = "embed_URL";
    private static final String INSTITUTION_ID = "institution_id";

    @MockBean
    private AwsQuicksightService awsQuicksightService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getEmbedUrlForAnonymousUser_200() throws Exception {
        QuicksightEmbedUrlResponse response = new QuicksightEmbedUrlResponse();
        response.setEmbedUrl(EMBED_URL);
        when(awsQuicksightService.generateEmbedUrlForAnonymousUser(null)).thenReturn(response);
        mvc.perform(get("/quicksight/dashboard"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getEmbedUrlForAnonymousUserForOperator_200() throws Exception {
        QuicksightEmbedUrlResponse response = new QuicksightEmbedUrlResponse();
        response.setEmbedUrl(EMBED_URL);
        when(awsQuicksightService.generateEmbedUrlForAnonymousUser(INSTITUTION_ID)).thenReturn(response);
        mvc.perform(get("/quicksight/dashboard")
                        .param("institutionId", INSTITUTION_ID))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getEmbedUrlForAnonymousUser_403() throws Exception {
        when(awsQuicksightService.generateEmbedUrlForAnonymousUser(null)).thenThrow(new AppException(AppError.FORBIDDEN));
        mvc.perform(get("/quicksight/dashboard"))
                .andExpect(status().isForbidden());
    }
}
