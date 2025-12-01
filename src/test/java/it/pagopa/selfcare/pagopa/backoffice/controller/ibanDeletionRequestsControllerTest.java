package it.pagopa.selfcare.pagopa.backoffice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDeletionRequest;
import it.pagopa.selfcare.pagopa.backoffice.service.IbanDeletionRequestsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class IbanDeletionRequestsControllerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final String CI_CODE = "77777777777";
    private final String IBAN_VALUE = "IT0000000000001000000123456";

    @MockBean
    private IbanDeletionRequestsService ibanDeletionRequestsService;

    @Autowired
    private MockMvc mvc;

    @Test
    void requestCreditorInstitutionIbanDeletion_shouldReturn201WithResponse() throws Exception {

        String scheduledDate = LocalDate.of(2025, 12, 15).toString();

        IbanDeletionRequest mockResponse = IbanDeletionRequest.builder()
                .ciCode(CI_CODE)
                .ibanValue(IBAN_VALUE)
                .scheduledExecutionDate(scheduledDate.toString())
                .status("PENDING")
                .build();

        when(ibanDeletionRequestsService.createIbanDeletionRequest(
                eq(CI_CODE), eq(IBAN_VALUE), any(String.class)))
                .thenReturn(mockResponse);

        String requestBody = String.format("""
            {
                "ibanValue": "%s",
                "scheduledExecutionDate": "%s"
            }
            """, IBAN_VALUE, scheduledDate);

        MvcResult mvcResult = mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", CI_CODE)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ciCode").value(CI_CODE))
                .andExpect(jsonPath("$.ibanValue").value(IBAN_VALUE))
                .andExpect(jsonPath("$.scheduledExecutionDate").value(scheduledDate.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        IbanDeletionRequest response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                IbanDeletionRequest.class
        );

        assertNotNull(response);
        assertEquals(CI_CODE, response.getCiCode());
        assertEquals(IBAN_VALUE, response.getIbanValue());
        assertEquals(scheduledDate, response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());

        verify(ibanDeletionRequestsService).createIbanDeletionRequest(
                eq(CI_CODE),
                eq(IBAN_VALUE),
                eq(scheduledDate.toString())
        );
    }

    @Test
    void requestCreditorInstitutionIbanDeletion_shouldReturn400_whenIbanValueIsMissing() throws Exception {

        String requestBody = """
            {
                "scheduledExecutionDate": "2025-12-15"
            }
            """;

        mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", CI_CODE)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestCreditorInstitutionIbanDeletion_shouldReturn400_whenScheduledDateIsMissing() throws Exception {

        String requestBody = String.format("""
            {
                "ibanValue": "%s"
            }
            """, IBAN_VALUE);

        mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", CI_CODE)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestCreditorInstitutionIbanDeletion_shouldReturn400_whenRequestBodyIsEmpty() throws Exception {

        String requestBody = "{}";

        mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", CI_CODE)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}