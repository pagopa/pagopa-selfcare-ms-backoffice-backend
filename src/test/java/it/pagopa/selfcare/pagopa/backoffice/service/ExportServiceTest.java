package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class ExportServiceTest {

    @MockBean
    private BrokerIbansRepository brokerIbansRepository;

    @MockBean
    private BrokerInstitutionsRepository brokerInstitutionsRepository;

    @Autowired
    @InjectMocks
    private ExportService exportService;

    @Test
    void exportIbansToCsv() throws IOException {
        String ibans = TestUtil.readJsonFromFile("entity/broker_iban.json");
        BrokerIbansEntity entity = TestUtil.toObject(ibans, BrokerIbansEntity.class);
        when(brokerIbansRepository.findByBrokerCode(eq("1111"))).thenReturn(Optional.of(entity));

        byte[] result = exportService.exportIbansToCsv("1111");
        assertNotNull(result);
    }

    @Test
    void exportCreditorInstitutionToCsv() throws IOException {
        String institutions = TestUtil.readJsonFromFile("entity/broker_institution.json");
        BrokerInstitutionsEntity entity = TestUtil.toObject(institutions, BrokerInstitutionsEntity.class);
        when(brokerInstitutionsRepository.findByBrokerCode(eq("02438750586"))).thenReturn(Optional.of(entity));

        byte[] result = exportService.exportCreditorInstitutionToCsv("02438750586");
        assertNotNull(result);
    }

    @Test
    void exportCreditorInstitutionToCsv_ko() throws IOException {
        String brokerCode = "02438750586";
        when(brokerIbansRepository.findByBrokerCode(eq(brokerCode))).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> exportService.exportCreditorInstitutionToCsv(brokerCode));
    }
}
