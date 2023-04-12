package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
@WebMvcTest(value = {CreditorInstitutionController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        CreditorInstitutionController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class CreditorInstitutionControllerTest {
    private static final String BASE_URL = "/creditor-institutions";
    @Autowired
    protected MockMvc mvc;
    private CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

//    @Test
    void createCreditorInstitution(@Value("classpath:stubs/creditorInstitutionDetailsDto.json") Resource dto) throws IOException {
        //given
        String xRequestId = "1";
        InputStream resource = dto.getInputStream();

        CreditorInstitutionDto creditorInstitutionDto = objectMapper.readValue(resource, CreditorInstitutionDto.class);

    }
}