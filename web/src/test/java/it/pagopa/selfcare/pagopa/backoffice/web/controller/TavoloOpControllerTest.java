package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigSelfcareIntegrationService;
import it.pagopa.selfcare.pagopa.backoffice.core.TavoloOpService;
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

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {TavoloOpController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        TavoloOpController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class TavoloOpControllerTest {
    private static final String BASE_URL = "/tavolo-operativo";
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    @MockBean
    private TavoloOpService tavoloOpService;

    @Test
    void insert(@Value("classpath:stubs/tavoloOpDto.json") Resource dto) throws Exception {
        //given
        TavoloOpOperations tavoloOp = mock(TavoloOpOperations.class);

        when(tavoloOpService.insert(any()))
                .thenReturn(tavoloOp);
        //when
        mvc.perform(post(BASE_URL)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[*]", everyItem(notNullValue()))
                );
        //then
        verify(tavoloOpService, times(1))
                .insert(any());
        verifyNoMoreInteractions(tavoloOpService);
    }

    @Test
    void getTavoloOpDetails() throws Exception {
        //given
        String taxCode = "taxCode";
        TavoloOpOperations tavoloOp = mock(TavoloOpOperations.class);


        when(tavoloOpService.findByTaxCode(anyString()))
                .thenReturn(tavoloOp);
        //when
        mvc.perform(get(BASE_URL + "/{ecCode}", taxCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", everyItem(notNullValue()))
                );
        //then
        verify(tavoloOpService, times(1))
                .findByTaxCode(anyString());
        verifyNoMoreInteractions(tavoloOpService);
    }

    @Test
    void update(@Value("classpath:stubs/tavoloOpDto.json") Resource dto) throws Exception {
        TavoloOpOperations tavoloOp = mock(TavoloOpOperations.class);

        when(tavoloOpService.update(any()))
                .thenReturn(tavoloOp);
        //when
        mvc.perform(put(BASE_URL)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", everyItem(notNullValue()))
                );
        //then
        verify(tavoloOpService, times(1))
                .update(any());
        verifyNoMoreInteractions(tavoloOpService);
    }

}



