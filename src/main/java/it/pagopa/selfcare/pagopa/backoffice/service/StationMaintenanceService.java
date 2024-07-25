package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StationMaintenanceService {

    private final ApiConfigClient apiConfigClient;

    @Autowired
    public StationMaintenanceService(ApiConfigClient apiConfigClient) {
        this.apiConfigClient = apiConfigClient;
    }

    public StationMaintenanceResource createStationMaintenance(String brokerCode,
                                                               CreateStationMaintenance createStationMaintenance) {
        return this.apiConfigClient.createStationMaintenance(brokerCode, createStationMaintenance);
    }

    public void deleteStationMaintenance(String brokerCode,
                                         Long maintenanceId) {
        this.apiConfigClient.deleteStationMaintenance(brokerCode, maintenanceId);
    }
}
