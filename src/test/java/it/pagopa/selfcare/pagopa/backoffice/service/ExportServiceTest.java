package it.pagopa.selfcare.pagopa.backoffice.service;

import com.mongodb.MongoException;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.ProjectCreatedAt;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    void exportIbansToCsv_ko() throws IOException {
        String brokerCode = "1111";
        when(brokerIbansRepository.findByBrokerCode(eq(brokerCode))).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> exportService.exportIbansToCsv(brokerCode));
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

    @ParameterizedTest
    @CsvSource({
            "false,false",
            "false,true",
            "true,false",
            "true,true"
    })
    void getBrokerExportStatus_ok(String brokerIbanPresence, String brokerInstitutionPresence) throws IOException {

        // set variables
        boolean isBrokerIbanPresent = Boolean.parseBoolean(brokerIbanPresence);
        boolean isBrokerInstitutionPresent = Boolean.parseBoolean(brokerInstitutionPresence);
        String brokerCode = "02438750586";

        // mock repository interaction
        if (isBrokerIbanPresent) {
            when(brokerIbansRepository.findProjectedByBrokerCode(brokerCode)).thenReturn(Optional.of(() -> {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, -6);
                return calendar.toInstant();
            }));
        }
        if (isBrokerInstitutionPresent) {
            when(brokerInstitutionsRepository.findProjectedByBrokerCode(brokerCode)).thenReturn(Optional.of(() -> {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, -3);
                return calendar.toInstant();
            }));
        }

        // execute logic
        BrokerECExportStatus result = exportService.getBrokerExportStatus(brokerCode);

        // check assertion
        assertNotNull(result);
        if (isBrokerIbanPresent) {
            assertNotNull(result.getBrokerIbansLastUpdate());
        }
        if (isBrokerInstitutionPresent) {
            assertNotNull(result.getBrokerInstitutionsLastUpdate());
        }
    }

    @Test
    void getBrokerExportStatus_noBrokerCode() {
        assertThrows(AppException.class, () -> exportService.getBrokerExportStatus(null));
    }


    @ParameterizedTest
    @CsvSource({
            "02438750586,false,true",
            "02438750586,true,false",
            "-,true,true"
    })
    void getBrokerExportStatus_error(String passedBrokerCode, String errorOnBrokerIbanRetrieve, String errorOnBrokerInstitutionRetrieve) throws IOException {

        // set variables
        boolean isBrokerIbanPresent = Boolean.parseBoolean(errorOnBrokerIbanRetrieve);
        boolean isBrokerInstitutionPresent = Boolean.parseBoolean(errorOnBrokerInstitutionRetrieve);
        String brokerCode = "-".equals(passedBrokerCode) ? null : passedBrokerCode;

        // mock repository interaction
        if (brokerCode != null && isBrokerIbanPresent) {
            when(brokerIbansRepository.findProjectedByBrokerCode(anyString())).thenThrow(MongoException.class);
        }
        if (brokerCode != null && isBrokerInstitutionPresent) {
            when(brokerInstitutionsRepository.findProjectedByBrokerCode(anyString())).thenThrow(MongoException.class);
        }


        if (brokerCode == null) {
            // execute logic and check assertion
            assertThrows(AppException.class, () -> exportService.getBrokerExportStatus(null));
        } else {
            // execute logic
            BrokerECExportStatus result = exportService.getBrokerExportStatus(brokerCode);

            // check assertion
            assertNotNull(result);
            assertNull(result.getBrokerIbansLastUpdate());
            assertNull(result.getBrokerInstitutionsLastUpdate());
        }
    }
}
