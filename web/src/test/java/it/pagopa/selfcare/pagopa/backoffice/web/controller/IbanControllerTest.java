package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansEnhanced;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {IbanController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        IbanController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
public class IbanControllerTest {

    private static final String BASE_URL = "/creditorinstitutions/ibans";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @Test
    void getCreditorInstitutionIbans(@Value("classpath:stubs/IbanRequestDto.json") Resource dto) throws Exception {

        IbansEnhanced ibansDetails = mockInstance(new IbansEnhanced());
        IbanEnhanced ibanDetails = mockInstance(new IbanEnhanced());
        ibanDetails.setLabels(new ArrayList<>());
        ibansDetails.setIbanList(List.of(ibanDetails));

        when(apiConfigServiceMock.getCreditorInstitutionIbans(anyString(), anyString(), anyString()))
                .thenReturn(ibansDetails);

        mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL)
                .content(dto.getInputStream().readAllBytes())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON));


        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionIbans(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void createCreditorInstitutionIbans(@Value("classpath:stubs/ibanCreateRequestDto.json") Resource dto) throws Exception {

        IbanCreate ibanCreate = mockInstance(new IbanCreate());
        ibanCreate.setLabels(new ArrayList<>());

        when(apiConfigServiceMock.createCreditorInstitutionIbans(anyString(), any(), anyString()))
                .thenReturn(ibanCreate);

        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/create")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON));


        verify(apiConfigServiceMock, times(1))
                .createCreditorInstitutionIbans(anyString(), any(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updateCreditorInstitutionIbans(@Value("classpath:stubs/ibanCreateRequestDto.json") Resource dto) throws Exception {

        IbanCreate ibanCreate = mockInstance(new IbanCreate());
        ibanCreate.setLabels(new ArrayList<>());
        ibanCreate.setIban("IT12L12312311111");


        when(apiConfigServiceMock.updateCreditorInstitutionIbans(anyString(), any(), anyString()))
                .thenReturn(ibanCreate);

        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/update")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON));


        verify(apiConfigServiceMock, times(1))
                .updateCreditorInstitutionIbans(anyString(), any(), anyString());

    }

    @Test
    void updateCreditorInstitutionIbans_Label(@Value("classpath:stubs/ibanCreateRequestDto.json") Resource dto) throws Exception {

        IbanCreate ibanCreate = mockInstance(new IbanCreate());
        ibanCreate.setLabels(new ArrayList<>());
        IbanLabel label = new IbanLabel();
        label.setName("CUP");
        ibanCreate.getLabels().add(label);
        ibanCreate.setIban("IT12L12312311111");

        IbansEnhanced ibansEnhanced = mockInstance(new IbansEnhanced());
        ibansEnhanced.setIbanList(new ArrayList<>());
        IbanEnhanced ibanEnhanced = mockInstance(new IbanEnhanced());
        ibanEnhanced.setLabels(new ArrayList<>());
        ibanEnhanced.getLabels().add(label);
        ibansEnhanced.getIbanList().add(ibanEnhanced);


        when(apiConfigServiceMock.updateCreditorInstitutionIbans(anyString(), any(), anyString()))
                .thenReturn(ibanCreate);
        when(apiConfigServiceMock.getCreditorInstitutionIbans(anyString(), any(), anyString()))
                .thenReturn(ibansEnhanced);


        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/update")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON));


        verify(apiConfigServiceMock, times(2))
                .updateCreditorInstitutionIbans(anyString(), any(), anyString());
        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionIbans(anyString(), any(), anyString());

    }
}
