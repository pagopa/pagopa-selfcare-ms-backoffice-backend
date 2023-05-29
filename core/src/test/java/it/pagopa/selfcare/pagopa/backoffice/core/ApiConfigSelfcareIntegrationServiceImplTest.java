package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigSelfcareIntegrationConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAddress;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigServiceImpl.CREDITOR_INSTITUTION_CODE_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApiConfigSelfcareIntegrationServiceImpl.class)
class ApiConfigSelfcareIntegrationServiceImplTest {

    @Autowired
    private ApiConfigSelfcareIntegrationServiceImpl apiConfigSelfcareIntegrationService;

    @MockBean
    private ApiConfigSelfcareIntegrationConnector apiConfigConnectorMock;

    @Test
    void getStationsDetailsListByBroker_nullPage() {
        //given
        final Integer limit = 1;
        final Integer page = 0;
        final String broker = "broker";
        final String station = "station";
        final String xRequestId = "xRequestId";

        StationDetailsList stationDetailsListMock = mockInstance(new StationDetailsList());
        StationDetails stationDetailsMock =mockInstance(new StationDetails());
        stationDetailsListMock.setStationsDetailsList(List.of(stationDetailsMock));

        when(apiConfigConnectorMock.getStationsDetailsListByBroker(anyString(),anyString(),anyInt(),anyInt(),any()))
                .thenReturn(stationDetailsListMock);

        //when
        StationDetailsList response = apiConfigSelfcareIntegrationService.getStationsDetailsListByBroker(broker, station, limit,page, xRequestId);
        //then
        assertNotNull(response);
        assertEquals(response, stationDetailsListMock);
        reflectionEqualsByName(response, stationDetailsListMock);
        verify(apiConfigConnectorMock, times(1))
                .getStationsDetailsListByBroker(broker, station, limit,page, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }


}
