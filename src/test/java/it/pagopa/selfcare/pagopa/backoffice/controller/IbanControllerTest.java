package it.pagopa.selfcare.pagopa.backoffice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import it.pagopa.selfcare.pagopa.backoffice.service.IbanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class IbanControllerTest {

  private static final String CI_CODE = "11111";
  private static final String IBAN = "GB33BUKB20201555555556";

  @MockBean private IbanService ibanService;

  @Autowired private MockMvc mvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void getIban() throws Exception {
    when(ibanService.getIban(CI_CODE, null))
        .thenReturn(TestUtil.fileToObject("response/apiconfig/ibans.json", Ibans.class));

    MvcResult mvcResult =
        mvc.perform(
                get("/creditor-institutions/{ci-code}/ibans", CI_CODE)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    Ibans response =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Ibans.class);
    assertNotNull(response);
    assertNotNull(response.getIbanList());
    assertEquals(28, response.getIbanList().size());
  }

  @Test
  void createCreditorInstitutionIban() throws Exception {
    when(ibanService.createIban(any(), any())).thenReturn(Iban.builder().iban(IBAN).build());

    mvc.perform(
            post("/creditor-institutions/{ci-code}/ibans", CI_CODE)
                .content(TestUtil.readJsonFromFile("request/create_iban.json"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateCreditorInstitutionIban() throws Exception {
    when(ibanService.updateIban(anyString(), anyString(), any()))
        .thenReturn(Iban.builder().iban(IBAN).build());

    mvc.perform(
            put("/creditor-institutions/{ci-code}/ibans/{iban-value}", CI_CODE, IBAN)
                .content(TestUtil.readJsonFromFile("request/create_iban.json"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteCreditorInstitutionIban() throws Exception {
    when(ibanService.updateIban(anyString(), anyString(), any()))
        .thenReturn(Iban.builder().iban("iban").build());

    mvc.perform(delete("/creditor-institutions/{ci-code}/ibans/{iban-value}", CI_CODE, IBAN))
        .andExpect(status().isOk());
  }
}
