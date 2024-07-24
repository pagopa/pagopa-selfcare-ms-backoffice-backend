package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    /**
     * Recovers a station maintenance, given its brokerCode and maintenanceId.
     * If the the provided brokerCode doesnt match the one related to the persisted one for the given maintenance,
     * it will throw the maintenance not found exception
     * @param brokerCode brokerCode to be used as filter in the maintenance recovery
     * @param maintenanceId station maintentance id to be used for the detail recovery
     * @return station maintenance data, provided in an instance of StationMaintenanceResource
     */
    public StationMaintenanceResource getStationMaintenance(String brokerCode, Long maintenanceId) {
        return this.apiConfigClient.getStationMaintenance(brokerCode, maintenanceId);
    }

}
