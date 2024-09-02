package it.pagopa.selfcare.pagopa.backoffice.service;

import com.mongodb.MongoException;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.export.BrokerECExportStatus;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ExportService.class})
class ExportServiceTest {

    @MockBean
    private BrokerIbansRepository brokerIbansRepository;

    @MockBean
    private BrokerInstitutionsRepository brokerInstitutionsRepository;

    @MockBean
    private BundleAllPages bundleAllPages;

    @Autowired
    private ExportService exportService;

    @Test
    void exportIbansToCsv() throws IOException {
        String ibans = TestUtil.readJsonFromFile("entity/broker_iban.json");
        BrokerIbansEntity entity = TestUtil.toObject(ibans, BrokerIbansEntity.class);
        when(brokerIbansRepository.findByBrokerCode("1111")).thenReturn(Optional.of(entity));

        byte[] result = exportService.exportIbansToCsv("1111");
        assertNotNull(result);
    }

    @Test
    void exportIbansToCsv_ko() throws IOException {
        String brokerCode = "1111";
        when(brokerIbansRepository.findByBrokerCode(brokerCode)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> exportService.exportIbansToCsv(brokerCode));
    }

    @Test
    void exportCreditorInstitutionToCsv() throws IOException {
        String institutions = TestUtil.readJsonFromFile("entity/broker_institution.json");
        BrokerInstitutionsEntity entity = TestUtil.toObject(institutions, BrokerInstitutionsEntity.class);
        when(brokerInstitutionsRepository.findByBrokerCode("02438750586")).thenReturn(Optional.of(entity));

        byte[] result = exportService.exportCreditorInstitutionToCsv("02438750586");
        assertNotNull(result);
    }

    @Test
    void exportCreditorInstitutionToCsv_ko() throws IOException {
        String brokerCode = "02438750586";
        when(brokerIbansRepository.findByBrokerCode(brokerCode)).thenReturn(Optional.empty());

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

    @Test
    void exportPSPBundleToCSV() throws IOException {
        when(bundleAllPages.getAllPSPBundles("pspCode", Collections.singletonList(BundleType.GLOBAL)))
                .thenReturn(Set.of(buildBundle("id_bundle1"), buildBundle("id_bundle2")));

        byte[] result = assertDoesNotThrow(() ->
                exportService.exportPSPBundlesToCsv("pspCode", Collections.singletonList(BundleType.GLOBAL)));

        assertNotNull(result);
    }

    private Bundle buildBundle(String idBundle) {
        return Bundle.builder()
                .id(idBundle)
                .name("bundle_name")
                .description("description")
                .paymentAmount(5L)
                .minPaymentAmount(0L)
                .maxPaymentAmount(100L)
                .type(BundleType.GLOBAL)
                .validityDateTo(LocalDate.now())
                .build();
    }
}
