package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.entity.MaintenanceEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.maintenance.MaintenanceMessage;
import it.pagopa.selfcare.pagopa.backoffice.repository.MaintenanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MaintenanceService.class, MappingsConfiguration.class})
class MaintenanceServiceTest {

    @MockBean
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private MaintenanceService sut;

    @Test
    void getMaintenanceMessageSuccess() {
        MaintenanceEntity maintenanceMessage = MaintenanceEntity.builder()
                .bannerMessage("bannerMessage")
                .pageMessage("pageMessage")
                .build();

        when(maintenanceRepository.findAll()).thenReturn(Collections.singletonList(maintenanceMessage));

        MaintenanceMessage result = assertDoesNotThrow(() -> sut.getMaintenanceMessages());

        assertNotNull(result);
        assertEquals(maintenanceMessage.getPageMessage(), result.getPageMessage());
        assertEquals(maintenanceMessage.getBannerMessage(), result.getBannerMessage());
    }

    @Test
    void getMaintenanceMessageFail() {
        when(maintenanceRepository.findAll()).thenReturn(Collections.emptyList());

        AppException e = assertThrows(AppException.class, () -> sut.getMaintenanceMessages());

        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }
}