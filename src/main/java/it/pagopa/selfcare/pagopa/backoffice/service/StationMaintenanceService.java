package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.UpdateStationMaintenance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class StationMaintenanceService {

    private final ApiConfigClient apiConfigClient;

    @Autowired
    public StationMaintenanceService(ApiConfigClient apiConfigClient) {
        this.apiConfigClient = apiConfigClient;
    }

    public StationMaintenanceResource createStationMaintenance(
            String brokerCode,
            CreateStationMaintenance createStationMaintenance
    ) {
        return this.apiConfigClient.createStationMaintenance(brokerCode, createStationMaintenance);
    }

    /**
     * Terminate the station's maintenance with the specified id.
     * Update the endDateTime field with the current timestamp rounded to the next 15 minutes.
     *
     * @param brokerCode    broker's tax code
     * @param maintenanceId maintenance's id
     */
    public void finishStationMaintenance(String brokerCode, Long maintenanceId) {
        StationMaintenanceResource maintenance = this.apiConfigClient.getStationMaintenance(brokerCode, maintenanceId);

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (!(maintenance.getStartDateTime().isBefore(now) && maintenance.getEndDateTime().isAfter(now))) {
            throw new AppException(AppError.STATION_MAINTENANCE_NOT_IN_PROGRESS);
        }

        long mod = now.getMinute() % 15;
        UpdateStationMaintenance update = UpdateStationMaintenance.builder()
                .endDateTime(now.plusMinutes(15 - mod))
                .build();

        this.apiConfigClient.updateStationMaintenance(brokerCode, maintenanceId, update);
    }
}
