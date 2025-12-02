package it.pagopa.selfcare.pagopa.backoffice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.selfcare.pagopa.backoffice.model.ibanrequests.IbanDeletionRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.ibanrequests.IbanDeletionRequests;
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
import java.util.List;

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

    private final String ciCode = "77777777777";
    private final String ibanValue = "IT0000000000001000000123456";

    @MockBean
    private IbanDeletionRequestsService ibanDeletionRequestsService;

    @Autowired
    private MockMvc mvc;

    @Test
    void requestCreditorInstitutionIbanDeletion_shouldReturn201WithResponse() throws Exception {

        String scheduledDate = LocalDate.of(2025, 12, 15).toString();

        IbanDeletionRequest mockResponse = IbanDeletionRequest.builder()
                .ciCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate(scheduledDate)
                .status("PENDING")
                .build();

        when(ibanDeletionRequestsService.createIbanDeletionRequest(
                eq(ciCode), eq(ibanValue), any(String.class)))
                .thenReturn(mockResponse);

        String requestBody = String.format("""
            {
                "ibanValue": "%s",
                "scheduledExecutionDate": "%s"
            }
            """, ibanValue, scheduledDate);

        MvcResult mvcResult = mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ciCode").value(ciCode))
                .andExpect(jsonPath("$.ibanValue").value(ibanValue))
                .andExpect(jsonPath("$.scheduledExecutionDate").value(scheduledDate))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        IbanDeletionRequest response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                IbanDeletionRequest.class
        );

        assertNotNull(response);
        assertEquals(ciCode, response.getCiCode());
        assertEquals(ibanValue, response.getIbanValue());
        assertEquals(scheduledDate, response.getScheduledExecutionDate());
        assertEquals("PENDING", response.getStatus());

        verify(ibanDeletionRequestsService).createIbanDeletionRequest(
                ciCode,
                ibanValue,
                scheduledDate
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
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
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
            """, ibanValue);

        mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestCreditorInstitutionIbanDeletion_shouldReturn400_whenRequestBodyIsEmpty() throws Exception {

        String requestBody = "{}";

        mvc.perform(
                        post("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getIbanDeletionRequests_shouldReturnAllRequests_whenIbanValueIsNotProvided() throws Exception {

        IbanDeletionRequest request1 = IbanDeletionRequest.builder()
                .id("req-001")
                .ciCode(ciCode)
                .ibanValue("IT0000000000001000000123456")
                .scheduledExecutionDate("2025-12-15")
                .status("PENDING")
                .build();

        IbanDeletionRequest request2 = IbanDeletionRequest.builder()
                .id("req-002")
                .ciCode(ciCode)
                .ibanValue("IT0000000000001000000999999")
                .scheduledExecutionDate("2025-12-20")
                .status("PENDING")
                .build();

        IbanDeletionRequests mockResponse = IbanDeletionRequests.builder()
                .requests(List.of(request1, request2))
                .build();

        when(ibanDeletionRequestsService.getIbanDeletionRequests(eq(ciCode),null))
                .thenReturn(mockResponse);

        MvcResult mvcResult = mvc.perform(
                        get("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.requests").isArray())
                .andExpect(jsonPath("$.requests.length()").value(2))
                .andExpect(jsonPath("$.requests[0].id").value("req-001"))
                .andExpect(jsonPath("$.requests[1].id").value("req-002"))
                .andReturn();

        IbanDeletionRequests response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                IbanDeletionRequests.class
        );

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(2, response.getRequests().size());

        verify(ibanDeletionRequestsService).getIbanDeletionRequests(ciCode, null);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnFilteredRequests_whenIbanValueIsProvided() throws Exception {

        IbanDeletionRequest request = IbanDeletionRequest.builder()
                .id("req-001")
                .ciCode(ciCode)
                .ibanValue(ibanValue)
                .scheduledExecutionDate("2025-12-15")
                .status("PENDING")
                .build();

        IbanDeletionRequests mockResponse = IbanDeletionRequests.builder()
                .requests(List.of(request))
                .build();

        when(ibanDeletionRequestsService.getIbanDeletionRequests(ciCode, ibanValue))
                .thenReturn(mockResponse);

        MvcResult mvcResult = mvc.perform(
                        get("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
                                .param("ibanValue", ibanValue)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.requests").isArray())
                .andExpect(jsonPath("$.requests.length()").value(1))
                .andExpect(jsonPath("$.requests[0].id").value("req-001"))
                .andExpect(jsonPath("$.requests[0].ibanValue").value(ibanValue))
                .andReturn();

        IbanDeletionRequests response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                IbanDeletionRequests.class
        );

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(1, response.getRequests().size());
        assertEquals(ibanValue, response.getRequests().get(0).getIbanValue());

        verify(ibanDeletionRequestsService).getIbanDeletionRequests(ciCode, ibanValue);
    }

    @Test
    void getIbanDeletionRequests_shouldReturnEmptyList_whenNoRequestsFound() throws Exception {

        IbanDeletionRequests mockResponse = IbanDeletionRequests.builder()
                .requests(List.of())
                .build();

        when(ibanDeletionRequestsService.getIbanDeletionRequests(eq(ciCode), null))
                .thenReturn(mockResponse);

        MvcResult mvcResult = mvc.perform(
                        get("/creditor-institutions/{ci-code}/iban-deletion-requests", ciCode)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.requests").isArray())
                .andExpect(jsonPath("$.requests.length()").value(0))
                .andReturn();

        IbanDeletionRequests response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                IbanDeletionRequests.class
        );

        assertNotNull(response);
        assertNotNull(response.getRequests());
        assertEquals(0, response.getRequests().size());

        verify(ibanDeletionRequestsService).getIbanDeletionRequests(ciCode, null);
    }

    @Test
    void cancelIbanDeletionRequest_shouldReturn204() throws Exception {

        String requestId = "req-123";

        doNothing().when(ibanDeletionRequestsService)
                .cancelIbanDeletionRequest(eq(ciCode), requestId);

        mvc.perform(
                        delete("/creditor-institutions/{ci-code}/iban-deletion-requests/{id}", ciCode, requestId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(ibanDeletionRequestsService).cancelIbanDeletionRequest(ciCode, requestId);
    }
}