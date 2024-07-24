package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.MaintenanceHoursSummaryResource;
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
     * Retrieves broker related station maintenance summary for the provided year
     * @param brokerCode broker id to use for summary retrieval
     * @param maintenanceYear year in format yyyy, to be used for summary retreival
     * @return maintenance summary for the provided year and brokerCode
     */
    public MaintenanceHoursSummaryResource getBrokerMaintenancesSummary(String brokerCode, String maintenanceYear) {
        return this.apiConfigClient.getBrokerMaintenancesSummary(brokerCode, maintenanceYear);
    }

}
